///*
// * Copyright 2014-2019 the original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      https://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package org.springframework.session.data.redis;
//
//import org.reactivestreams.Publisher;
//import org.springframework.data.redis.core.ReactiveRedisOperations;
//import org.springframework.session.MapSession;
//import org.springframework.session.ReactiveSessionRepository;
//import org.springframework.session.SaveMode;
//import org.springframework.session.Session;
//import org.springframework.util.Assert;
//import reactor.core.publisher.Mono;
//
//import java.time.Duration;
//import java.time.Instant;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//
///**
// * A {@link ReactiveSessionRepository} that is implemented using Spring Data's
// * {@link ReactiveRedisOperations}.
// *
// * @author zk
// * @since 2.2.0
// */
//public class ReactiveRedisSessionRepository
//        implements ReactiveSessionRepository<ReactiveRedisSessionRepository.RedisSession> {
//
//    /**
//     * The default namespace for each key and channel in Redis used by Spring Session.
//     */
//    public static final String DEFAULT_NAMESPACE = "spring:session";
//
//    private final ReactiveRedisOperations<String, Object> sessionRedisOperations;
//
//    /**
//     * The namespace for every key used by Spring Session in Redis.
//     */
//    private String namespace = DEFAULT_NAMESPACE + ":";
//
//    /**
//     * If non-null, this value is used to override the default value for
//     * {@link RedisSession#setMaxInactiveInterval(Duration)}.
//     */
//    private Integer defaultMaxInactiveInterval;
//
//    private SaveMode saveMode = SaveMode.ON_SET_ATTRIBUTE;
//
//    /**
//     * Create a new {@link ReactiveRedisSessionRepository} instance.
//     * @param sessionRedisOperations the {@link ReactiveRedisOperations} to use for
//     * managing sessions
//     */
//    public ReactiveRedisSessionRepository(ReactiveRedisOperations<String, Object> sessionRedisOperations) {
//        Assert.notNull(sessionRedisOperations, "sessionRedisOperations cannot be null");
//        this.sessionRedisOperations = sessionRedisOperations;
//    }
//
//    public void setRedisKeyNamespace(String namespace) {
//        Assert.hasText(namespace, "namespace cannot be null or empty");
//        this.namespace = namespace.trim() + ":";
//    }
//
//    /**
//     * Sets the maximum inactive interval in seconds between requests before newly created
//     * sessions will be invalidated. A negative time indicates that the session will never
//     * timeout. The default is 1800 (30 minutes).
//     * @param defaultMaxInactiveInterval the number of seconds that the {@link Session}
//     * should be kept alive between client requests.
//     */
//    public void setDefaultMaxInactiveInterval(int defaultMaxInactiveInterval) {
//        this.defaultMaxInactiveInterval = defaultMaxInactiveInterval;
//    }
//
//    /**
//     * Set the save mode.
//     * @param saveMode the save mode
//     */
//    public void setSaveMode(SaveMode saveMode) {
//        Assert.notNull(saveMode, "saveMode must not be null");
//        this.saveMode = saveMode;
//    }
//
//    /**
//     * Returns the {@link ReactiveRedisOperations} used for sessions.
//     * @return the {@link ReactiveRedisOperations} used for sessions
//     */
//    public ReactiveRedisOperations<String, Object> getSessionRedisOperations() {
//        return this.sessionRedisOperations;
//    }
//
//    @Override
//    public Mono<RedisSession> createSession() {
//        return Mono.defer(() -> {
//            MapSession cached = new MapSession();
//            if (this.defaultMaxInactiveInterval != null) {
//                cached.setMaxInactiveInterval(Duration.ofSeconds(this.defaultMaxInactiveInterval));
//            }
//            RedisSession session = new RedisSession(cached, true);
//            return Mono.just(session);
//        });
//    }
//
//    @Override
//    public Mono<Void> save(RedisSession session) {
//        if (session.isNew) {
//            return session.save();
//        }
//        String sessionKey = getSessionKey(session.hasChangedSessionId() ? session.originalSessionId : session.getId());
//        return this.sessionRedisOperations.hasKey(sessionKey).flatMap(
//                (exists) -> exists ? session.save() : Mono.error(new IllegalStateException("Session was invalidated")));
//    }
//
//    @Override
//    public Mono<RedisSession> findById(String id) {
//        String sessionKey = getSessionKey(id);
//
//        // @formatter:off
//        return this.sessionRedisOperations.opsForHash().entries(sessionKey)
//                .collectMap((e) -> e.getKey().toString(), Map.Entry::getValue)
//                .filter((map) -> !map.isEmpty())
//                .map(new RedisSessionMapper(id))
//                .filter((session) -> !session.isExpired())
//                .map((session) -> new RedisSession(session, false))
//                .switchIfEmpty(Mono.defer(() -> deleteById(id).then(Mono.empty())));
//        // @formatter:on
//    }
//
//    @Override
//    public Mono<Void> deleteById(String id) {
//        String sessionKey = getSessionKey(id);
//
//        return this.sessionRedisOperations.delete(sessionKey).then();
//    }
//
//    private static String getAttributeKey(String attributeName) {
//        return RedisSessionMapper.ATTRIBUTE_PREFIX + attributeName;
//    }
//
//    private String getSessionKey(String sessionId) {
//        return this.namespace + "sessions:" + sessionId;
//    }
//
//    /**
//     * A custom implementation of {@link Session} that uses a {@link MapSession} as the
//     * basis for its mapping. It keeps track of any attributes that have changed. When
//     * {@link RedisSession#saveDelta()} is invoked all the attributes that have been
//     * changed will be persisted.
//     */
//    final class RedisSession implements Session {
//
//        private final MapSession cached;
//
//        private final Map<String, Object> delta = new HashMap<>();
//
//        private boolean isNew;
//
//        private String originalSessionId;
//
//        RedisSession(MapSession cached, boolean isNew) {
//            this.cached = cached;
//            this.isNew = isNew;
//            this.originalSessionId = cached.getId();
//            if (this.isNew) {
//                this.delta.put(RedisSessionMapper.CREATION_TIME_KEY, cached.getCreationTime().toEpochMilli());
//                this.delta.put(RedisSessionMapper.MAX_INACTIVE_INTERVAL_KEY,
//                        (int) cached.getMaxInactiveInterval().getSeconds());
//                this.delta.put(RedisSessionMapper.LAST_ACCESSED_TIME_KEY, cached.getLastAccessedTime().toEpochMilli());
//            }
//            if (this.isNew || (ReactiveRedisSessionRepository.this.saveMode == SaveMode.ALWAYS)) {
//                getAttributeNames().forEach((attributeName) -> this.delta.put(getAttributeKey(attributeName),
//                        cached.getAttribute(attributeName)));
//            }
//        }
//
//        @Override
//        public String getId() {
//            return this.cached.getId();
//        }
//
//        @Override
//        public String changeSessionId() {
//            return this.cached.changeSessionId();
//        }
//
//        @Override
//        public <T> T getAttribute(String attributeName) {
//            T attributeValue = this.cached.getAttribute(attributeName);
//            if (attributeValue != null
//                    && ReactiveRedisSessionRepository.this.saveMode.equals(SaveMode.ON_GET_ATTRIBUTE)) {
//                this.delta.put(getAttributeKey(attributeName), attributeValue);
//            }
//            return attributeValue;
//        }
//
//        @Override
//        public Set<String> getAttributeNames() {
//            return this.cached.getAttributeNames();
//        }
//
//        @Override
//        public void setAttribute(String attributeName, Object attributeValue) {
//            this.cached.setAttribute(attributeName, attributeValue);
//            this.delta.put(getAttributeKey(attributeName), attributeValue);
//        }
//
//        @Override
//        public void removeAttribute(String attributeName) {
//            this.cached.removeAttribute(attributeName);
//            this.delta.put(getAttributeKey(attributeName), null);
//        }
//
//        @Override
//        public Instant getCreationTime() {
//            return this.cached.getCreationTime();
//        }
//
//        @Override
//        public void setLastAccessedTime(Instant lastAccessedTime) {
//            this.cached.setLastAccessedTime(lastAccessedTime);
//            this.delta.put(RedisSessionMapper.LAST_ACCESSED_TIME_KEY, getLastAccessedTime().toEpochMilli());
//        }
//
//        @Override
//        public Instant getLastAccessedTime() {
//            return this.cached.getLastAccessedTime();
//        }
//
//        @Override
//        public void setMaxInactiveInterval(Duration interval) {
//            this.cached.setMaxInactiveInterval(interval);
//            this.delta.put(RedisSessionMapper.MAX_INACTIVE_INTERVAL_KEY, (int) getMaxInactiveInterval().getSeconds());
//        }
//
//        @Override
//        public Duration getMaxInactiveInterval() {
//            return this.cached.getMaxInactiveInterval();
//        }
//
//        @Override
//        public boolean isExpired() {
//            return this.cached.isExpired();
//        }
//
//        private boolean hasChangedSessionId() {
//            return !getId().equals(this.originalSessionId);
//        }
//
//        private Mono<Void> save() {
//            return Mono.defer(() -> saveChangeSessionId().then(saveDelta()).doOnSuccess((aVoid) -> this.isNew = false));
//        }
//
//        private Mono<Void> saveDelta() {
//            if (this.delta.isEmpty()) {
//                return Mono.empty();
//            }
//
//            String sessionKey = getSessionKey(getId());
//            Mono<Boolean> update = ReactiveRedisSessionRepository.this.sessionRedisOperations.opsForHash()
//                    .putAll(sessionKey, new HashMap<>(this.delta));
//            Mono<Boolean> setTtl;
//            if (getMaxInactiveInterval().getSeconds() >= 0) {
//                setTtl = ReactiveRedisSessionRepository.this.sessionRedisOperations.expire(sessionKey,
//                        getMaxInactiveInterval());
//            } else {
//                setTtl = ReactiveRedisSessionRepository.this.sessionRedisOperations.persist(sessionKey);
//            }
//
//            return update.and(setTtl).and((s) -> {
//                this.delta.clear();
//                s.onComplete();
//            }).then();
//        }
//
//        private Mono<Void> saveChangeSessionId() {
//            if (!hasChangedSessionId()) {
//                return Mono.empty();
//            }
//
//            String sessionId = getId();
//
//            Publisher<Void> replaceSessionId = (s) -> {
//                this.originalSessionId = sessionId;
//                s.onComplete();
//            };
//
//            if (this.isNew) {
//                return Mono.from(replaceSessionId);
//            }
//            else {
//                String originalSessionKey = getSessionKey(this.originalSessionId);
//                String sessionKey = getSessionKey(sessionId);
//
//                return ReactiveRedisSessionRepository.this.sessionRedisOperations.rename(originalSessionKey, sessionKey)
//                        .and(replaceSessionId);
//            }
//        }
//
//    }
//
//}
