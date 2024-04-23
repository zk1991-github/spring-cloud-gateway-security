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
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.session.data.redis.ReactiveRedisSessionRepository;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 使用redis缓存实现
 *
 * @author zk
 * @since 4.3.0
 */
public class GatewaySecurityCacheRedis implements GatewaySecurityCache {

    private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    private final ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate;

    private final ReactiveRedisConnection reactiveRedisConnection;

    private final static String SESSION_KEY = "sessions";

    public GatewaySecurityCacheRedis(ReactiveStringRedisTemplate reactiveStringRedisTemplate,
                                     ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate,
                                     ReactiveRedisConnection reactiveRedisConnection) {
        this.reactiveStringRedisTemplate = reactiveStringRedisTemplate;
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.reactiveRedisConnection = reactiveRedisConnection;
    }
    @Override
    public Mono<String> getSessionId(String hashKey) {
        return reactiveStringRedisTemplate
                .opsForHash()
                .get(SESSION_KEY, hashKey)
                .switchIfEmpty(Mono.empty())
                .cast(String.class);
    }

    @Override
    public Mono<Boolean> saveSessionId(String hashKey, String sessionId) {
        return reactiveStringRedisTemplate
                .opsForHash()
                .put(SESSION_KEY, hashKey, sessionId);
    }

    @Override
    public Mono<Boolean> removeSessionId(String hashKey) {
        return reactiveStringRedisTemplate
                .opsForHash()
                .remove(SESSION_KEY, hashKey).map(delNum -> delNum > 0);
    }

    @Override
    public Mono<Boolean> removeAllSessions() {
        return reactiveRedisConnection.keyCommands()
                .scan()
                .map(ByteBuffer::array)
                .map(keyBytes -> new String(keyBytes, StandardCharsets.UTF_8))
                .filter(key -> key.matches(ReactiveRedisSessionRepository.DEFAULT_NAMESPACE + ":.*"))
                .flatMap(key -> reactiveStringRedisTemplate.delete(key).map(count -> count > 0))
                // 如果有一个删除成功，则返回true
                .reduce((previous, current) -> previous || current)
                // 如果没有找到匹配的 key，则返回false
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<Long> onlineNum() {
        ScanOptions options = ScanOptions
                .scanOptions()
                .match(ReactiveRedisSessionRepository.DEFAULT_NAMESPACE + ":*")
                .build();
        return reactiveStringRedisTemplate.scan(options).count();
    }

    @Override
    public Mono<Boolean> userSessionExistBySessionId(String sessionId) {
        // 构建过滤条件
        ScanOptions options = ScanOptions
                .scanOptions()
                .match(ReactiveRedisSessionRepository.DEFAULT_NAMESPACE + ":sessions:" + sessionId)
                .build();
        // 根据查询数量
        return reactiveStringRedisTemplate.scan(options).count().flatMap(count -> {
            if (count > 0) {
                return Mono.just(true);
            }
            return Mono.empty();
        });
    }

    @Override
    public Mono<Boolean> isLockUser(String username, String namespace, int allowPasswordErrorRecord) {
        return reactiveStringRedisTemplate
                .opsForValue()
                // 计数增加
                .increment(namespace + username)
                .flatMap(num -> {
                    // 密码输入错误次数小于允许的次数，只计数，返回false
                    if (allowPasswordErrorRecord == -1 || num < allowPasswordErrorRecord) {
                        return Mono.empty();
                    }
                    // 锁定后删除计数
                    return removeLockRecord(username, namespace);
                });
    }

    @Override
    public Mono<Boolean> removeLockRecord(String username, String namespace) {
        return reactiveStringRedisTemplate.opsForValue().delete(namespace + username);
    }

    @Override
    public Mono<Boolean> cachePermissions(String permissionKey, List<PermissionInfo> permissionInfos) {
        return reactiveRedisTemplate.opsForValue().set(permissionKey, permissionInfos);
    }

    @Override
    public Mono<List<PermissionInfo>> getCachePermission(String permissionKey) {
        return reactiveRedisTemplate.opsForValue().get(permissionKey).map(o -> (List<PermissionInfo>) o);
    }

}
