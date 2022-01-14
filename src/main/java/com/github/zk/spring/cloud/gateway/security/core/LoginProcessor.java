/*
 *
 *  * Copyright 2021-2022 the original author or authors.
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

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.session.data.redis.ReactiveRedisSessionRepository;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

/**
 * 基础登录
 *
 * @author zk
 * @date 2021/11/30 15:06
 */
public class LoginProcessor {

    private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    private final ReactiveRedisSessionRepository sessionRepository;

    /**
     * 同时在线最大用户数
     */
    private int maxSessions;

    public LoginProcessor(ReactiveStringRedisTemplate reactiveStringRedisTemplate,
                          ReactiveRedisSessionRepository sessionRepository) {
        this.reactiveStringRedisTemplate = reactiveStringRedisTemplate;
        this.sessionRepository = sessionRepository;
    }

    /**
     * 设置同时在线最大用户数
     *
     * @param maxSessions 同时在线最大用户数
     */
    public void setMaxSessions(int maxSessions) {
        this.maxSessions = maxSessions;
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
                .flatMap(sessionId -> changeSession(hashKey, sessionId, webSession.getId()))
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
                .flatMap(this::userSessionExistBySessionId)
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
        return reactiveStringRedisTemplate
                .opsForHash()
                .get("sessions", hashKey)
                .switchIfEmpty(Mono.empty())
                .cast(String.class);
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
        Mono<Void> delSession = sessionRepository.deleteById(oldSessionId);
        Mono<Boolean> removeSession = removeSession(hashKey);
        Mono<Boolean> saveSession = saveSession(hashKey, newSessionId);
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
        return reactiveStringRedisTemplate
                .opsForHash()
                .put("sessions", hashKey, sessionId);
    }

    /**
     * 删除会话
     *
     * @param hashKey 用户缓存key
     * @return 删除 Session 状态
     */
    public Mono<Boolean> removeSession(String hashKey) {
        return reactiveStringRedisTemplate
                .opsForHash()
                .remove("sessions", hashKey).map(delNum -> delNum > 0);
    }

    /**
     * 在线用户数
     *
     * @return 在线用户数
     */
    public Mono<Long> onlineNum() {
        ScanOptions options = ScanOptions
                .scanOptions()
                .match(ReactiveRedisSessionRepository.DEFAULT_NAMESPACE + ":*")
                .build();
        return reactiveStringRedisTemplate.scan(options).count();
    }

    /**
     * 根据 SessionId， 查询用户 Session 是否存在
     *
     * @param sessionId 会话id
     * @return 用户存在状态
     */
    public Mono<Boolean> userSessionExistBySessionId(String sessionId) {
        ScanOptions options = ScanOptions
                .scanOptions()
                .match(ReactiveRedisSessionRepository.DEFAULT_NAMESPACE + ":sessions:" + sessionId)
                .build();
        return reactiveStringRedisTemplate.scan(options).count().flatMap(count -> {
            if (count > 0) {
                return Mono.just(true);
            }
            return Mono.empty();
        });
    }
}
