/*
 *
 *  * Copyright 2021-2023 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.github.zk.spring.cloud.gateway.security.core;

import java.time.Duration;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.web.server.WebSession;
import org.springframework.web.server.session.WebSessionStore;
import reactor.core.publisher.Mono;

/**
 * 基础登录
 *
 * @author zk
 * @date 2021/11/30 15:06
 */
public class LoginProcessor {

    private final GatewaySecurityCache gatewaySecurityCache;

    private final WebSessionStore webSessionStore;

    /**
     * 同时在线最大用户数
     */
    private int maxSessions;

    /**
     * 允许密码输入错误次数
     */
    private Integer allowPasswordErrorRecord = 3;

    /**
     * 锁定时长
     */
    private Duration lockedTime;

    /**
     * 密码错误存储 key
     */
    private final String DEFAULT_NAMESPACE = "password:error";

    /**
     * 命名空间
     */
    private final String namespace = DEFAULT_NAMESPACE + ":";

    public LoginProcessor(GatewaySecurityCache gatewaySecurityCache,
                          WebSessionStore webSessionStore) {
        this.gatewaySecurityCache = gatewaySecurityCache;
        this.webSessionStore = webSessionStore;
    }

    /**
     * 设置同时在线最大用户数
     *
     * @param maxSessions 同时在线最大用户数
     */
    public void setMaxSessions(int maxSessions) {
        this.maxSessions = maxSessions;
    }

    public void setAllowPasswordErrorRecord(Integer allowPasswordErrorRecord) {
        this.allowPasswordErrorRecord = allowPasswordErrorRecord;
    }

    public void setLockedTime(Duration lockedTime) {
        this.lockedTime = lockedTime;
    }

    public Duration getLockedTime() {
        return lockedTime;
    }

    /**
     * web会话处理
     *
     * @param hashKey    用户缓存key
     * @param webSession web会话
     * @return Session 处理状态
     */
    public Mono<Boolean> webSessionProcess(String hashKey, WebSession webSession) {
        return getSessionId(hashKey)
                // session 存在，更新 session
                .flatMap(sessionId -> changeSession(hashKey, sessionId, webSession.getId()))
                // session 不存在 保存 session
                .switchIfEmpty(Mono.defer(() -> saveSession(hashKey, webSession.getId())));
    }

    /**
     * 会话限制处理
     * 已经登录过的用户，因异地登录或重复登录时，不受最大登录用户数限制。
     * 流程如下：
     * 1. 获取用户 SessionId
     * 2. 判断用户 Session 是否存在
     * 如果存在：返回用户信息
     * 如果不存在：判断在线用户数是否超过允许登录最大用户数
     * 如果超过：返回错误信息
     * 如果未超过：返回用户信息
     *
     * @param hashKey 缓存key
     * @return 最大同时登录人数处理状态
     */
    public Mono<Boolean> sessionLimitProcess(String hashKey) {
        //判断是否超过允许最大登录人数
        return getSessionId(hashKey)
                // 查询用户 session 是否存在，存在说明当前为登录状态，登录状态允许再次登录，
                // 不受最大登录人数限制
                .flatMap(this::userSessionExistBySessionId)
                .filter(logined -> logined)
                // 未登录用户，验证是否超过允许登录人数
                .switchIfEmpty(onlineNum()
                        .map(aLong -> maxSessions != -1 && aLong >= maxSessions)
                        .flatMap(aBoolean -> {
                            if (aBoolean) {
                                return Mono.defer(() -> Mono.error(new SessionAuthenticationException("超过最大登录人数")));
                            } else {
                                return Mono.just(true);
                            }
                        }));
    }

    /**
     * 获取会话id
     * 用于判断当前用户是否登录
     *
     * @param hashKey 用户缓存key
     * @return SessionId
     */
    public Mono<String> getSessionId(String hashKey) {
        return gatewaySecurityCache.getSessionId(hashKey);
    }

    /**
     * 改变登录会话，删除老会话，保存新会话(异地踢出)
     *
     * @param hashKey      用户缓存key
     * @param oldSessionId 老会话id
     * @param newSessionId 新会话id
     * @return 更改 Session 状态
     */
    public Mono<Boolean> changeSession(String hashKey, String oldSessionId, String newSessionId) {
        // 根据 sessionId 删除存储的 session 信息
        Mono<Void> delSession = webSessionStore.removeSession(oldSessionId);
        // 删除登录时存储的用户信息
        Mono<Boolean> removeSession = gatewaySecurityCache.removeSession(hashKey);
        // 存储新的用户信息
        Mono<Boolean> saveSession = gatewaySecurityCache.saveSession(hashKey, newSessionId);
        // 依次执行响应式方法
        return delSession.then(removeSession).then(saveSession);
    }

    /**
     * 保存会话
     *
     * @param hashKey   用户缓存key
     * @param sessionId 会话id
     * @return 保存 Session 状态
     */
    public Mono<Boolean> saveSession(String hashKey, String sessionId) {
        return gatewaySecurityCache.saveSession(hashKey, sessionId);
    }

    /**
     * 在线用户数
     *
     * @return 在线用户数
     */
    public Mono<Long> onlineNum() {
        return gatewaySecurityCache.onlineNum();
    }

    /**
     * 根据 SessionId， 查询用户 Session 是否存在
     *
     * @param sessionId 会话id
     * @return 用户存在状态
     */
    public Mono<Boolean> userSessionExistBySessionId(String sessionId) {
        return gatewaySecurityCache.userSessionExistBySessionId(sessionId);
    }

    /**
     * 是否锁定用户
     *
     * @param username 用户名
     * @return 锁定是否成功
     */
    public Mono<Boolean> isLockUser(String username) {
        return gatewaySecurityCache.isLockUser(username, namespace, allowPasswordErrorRecord);
    }

    /**
     * 删除锁定计数
     *
     * @param username 用户名
     * @return 删除是否成功状态
     */
    public Mono<Boolean> removeLockRecord(String username) {
        return gatewaySecurityCache.removeLockRecord(username, namespace);
    }

}
