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
import org.springframework.web.server.WebSession;
import org.springframework.web.server.session.InMemoryWebSessionStore;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 使用本地缓存实现
 *
 * @author zk
 * @since 4.3.0
 */
public class GatewaySecurityCacheMap implements GatewaySecurityCache {

    private final InMemoryWebSessionStore webSessionStore;

    public GatewaySecurityCacheMap(InMemoryWebSessionStore webSessionStore) {
        this.webSessionStore = webSessionStore;
    }

    /**
     * session缓存
     */
    private final Map<String, String> sessionCache = new ConcurrentHashMap<>();

    /**
     * 锁定计数
     */
    private final Map<String, AtomicInteger> lockCount = new ConcurrentHashMap<>();

    /**
     * 权限缓存
     */
    private final Map<String, List<PermissionInfo>> permissionsCache = new ConcurrentHashMap<>();

    @Override
    public Mono<String> getSessionId(String hashKey) {
        return Mono.fromCallable(() -> sessionCache.get(hashKey));
    }

    @Override
    public Mono<Boolean> saveSession(String hashKey, String sessionId) {
        return Mono.fromCallable(() -> {
            sessionCache.put(hashKey, sessionId);
            return true;
        }).onErrorReturn(false);
    }

    @Override
    public Mono<Boolean> removeSession(String hashKey) {
        return Mono.fromCallable(() -> {
            sessionCache.remove(hashKey);
            return true;
        }).onErrorReturn(false);
    }

    @Override
    public Mono<Boolean> userSessionExistBySessionId(String sessionId) {
        return Mono.fromCallable(() -> {
            WebSession webSession = webSessionStore.getSessions().get(sessionId);
            return webSession != null;
        }).onErrorReturn(false);
    }

    @Override
    public Mono<Long> onlineNum() {
        return Mono.fromCallable(() -> (long) webSessionStore.getSessions().size());
    }

    @Override
    public Mono<Boolean> isLockUser(String username, String namespace, int allowPasswordErrorRecord) {
        int num = lockCount.computeIfAbsent(username, k -> new AtomicInteger(-1)).incrementAndGet();
        if (allowPasswordErrorRecord == -1 || num < allowPasswordErrorRecord) {
            return Mono.empty();
        }
        // 锁定后删除计数
        return removeLockRecord(username, namespace);
    }

    @Override
    public Mono<Boolean> removeLockRecord(String username, String namespace) {
        return Mono.fromCallable(() -> {
            lockCount.remove(username);
            return true;
        }).onErrorReturn(false);
    }

    @Override
    public Mono<Boolean> cachePermissions(String permissionKey, List<PermissionInfo> permissionInfos) {
        return Mono.fromCallable(() -> {
            permissionsCache.put(permissionKey, permissionInfos);
            return true;
        }).onErrorReturn(false);
    }

    @Override
    public Mono<List<PermissionInfo>> getCachePermission(String permissionKey) {
        return Mono.fromCallable(() -> permissionsCache.get(permissionKey))
                .onErrorReturn(new ArrayList<>());
    }

}
