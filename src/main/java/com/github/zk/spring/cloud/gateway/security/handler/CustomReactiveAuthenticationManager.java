package com.github.zk.spring.cloud.gateway.security.handler;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.session.data.redis.ReactiveRedisSessionRepository;
import reactor.core.publisher.Mono;

/**
 * 自定义认证管理器
 *
 * @author zk
 * @date 2021/9/3 10:48
 */
public class CustomReactiveAuthenticationManager extends UserDetailsRepositoryReactiveAuthenticationManager {

    /**
     * 允许的最大登录人数
     */
    private int maxSessions;

    private ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    public CustomReactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService, int maxSessions,
                                               ReactiveStringRedisTemplate reactiveStringRedisTemplate) {
        super(userDetailsService);
        this.maxSessions = maxSessions;
        this.reactiveStringRedisTemplate = reactiveStringRedisTemplate;
    }

    @Override
    public Mono<UserDetails> retrieveUser(String username) {
        //判断是否超过允许最大登录人数
        return getSessionId(username)
                .flatMap(this::userSessionExistBySessionId)
                .flatMap(sessionId -> super.retrieveUser(username))
                .switchIfEmpty(onlineNum()
                        .map(aLong -> aLong >= maxSessions)
                        .flatMap(aBoolean -> {
                            if (aBoolean) {
                                return Mono.defer(() -> Mono.error(new BadCredentialsException("超过最大登录人数")));
                            } else {
                                return super.retrieveUser(username);
                            }
                        }));
    }

    /**
     * 在线用户数
     *
     * @return
     */
    public Mono<Long> onlineNum() {
        ScanOptions options = ScanOptions
                .scanOptions()
                .match(ReactiveRedisSessionRepository.DEFAULT_NAMESPACE + ":*")
                .build();
        return reactiveStringRedisTemplate.scan(options).count();
    }

    public Mono<String> getSessionId(String username) {
        return reactiveStringRedisTemplate
                .opsForHash()
                .get("sessions", username)
                .switchIfEmpty(Mono.empty())
                .cast(String.class);
    }

    public Mono<String> userSessionExistBySessionId(String sessionId) {
        ScanOptions options = ScanOptions
                .scanOptions()
                .match(ReactiveRedisSessionRepository.DEFAULT_NAMESPACE + ":sessions:" + sessionId)
                .build();
        return reactiveStringRedisTemplate.scan(options).count().flatMap(count -> {
            if (count > 0) {
                return Mono.just(sessionId);
            }
            return Mono.empty();
        });
    }

}
