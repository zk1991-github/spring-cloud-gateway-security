/*
 *
 *  * Copyright 2021-2024 the original author or authors.
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

import com.github.zk.spring.cloud.gateway.security.pojo.PermissionInfo;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 网关鉴权缓存接口
 *
 * @author zk
 * @since 4.3.0
 */
public interface GatewaySecurityCache {

    /**
     * 获取 SessionId
     *
     * @param hashKey 用户名
     * @return sessionId
     */
    Mono<String> getSessionId(String hashKey);

    /**
     * 保存会话
     *
     * @param hashKey   用户缓存key
     * @param sessionId 会话id
     * @return 保存 Session 状态
     */
    Mono<Boolean> saveSessionId(String hashKey, String sessionId);

    /**
     * 删除会话
     *
     * @param hashKey 用户缓存key
     * @return 删除 Session 状态
     */
    Mono<Boolean> removeSessionId(String hashKey);

    Mono<Boolean> removeAllSessions();

    /**
     * 根据 SessionId， 查询用户 Session 是否存在
     *
     * @param sessionId 会话id
     * @return 用户存在状态
     */
    Mono<Boolean> userSessionExistBySessionId(String sessionId);

    /**
     * 在线用户数
     *
     * @return 在线用户数
     */
    Mono<Long> onlineNum();

    /**
     * 是否锁定用户
     *
     * @param username 用户名
     * @param namespace 密码错误 key 命名空间
     * @param allowPasswordErrorRecord 允许密码错误次数
     * @return 锁定是否成功
     */
    Mono<Boolean> isLockUser(String username, String namespace, int allowPasswordErrorRecord);

    /**
     * 删除锁定计数
     *
     * @param username 用户名
     * @param namespace 密码错误 key 命名空间
     * @return 删除是否成功状态
     */
    Mono<Boolean> removeLockRecord(String username, String namespace);

    /**
     * 缓存权限
     *
     * @param permissionKey 权限 key
     * @param permissionInfos 权限列表
     * @return 缓存权限是否成功状态
     */
    Mono<Boolean> cachePermissions(String permissionKey, List<PermissionInfo> permissionInfos);

    /**
     * 获取缓存中的权限
     *
     * @param permissionKey 权限 key
     * @return 权限列表
     */
    Mono<List<PermissionInfo>> getCachePermission(String permissionKey);

}
