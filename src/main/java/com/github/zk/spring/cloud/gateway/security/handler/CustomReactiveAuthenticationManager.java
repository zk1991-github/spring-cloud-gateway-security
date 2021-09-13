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

    /**
     * 已经登录过的用户，因异地登录或重复登录时，不受最大登录用户数限制。
     * 流程如下：
     * 1. 获取用户 SessionId
     * 2. 判断用户 Session 是否存在
     *      如果存在：返回用户信息
     *      如果不存在：判断在线用户数是否超过允许登录最大用户数
     *                  如果超过：返回错误信息
     *                  如果未超过：返回用户信息
     * @param username
     * @return
     */
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

    /**
     * 获取 SessionId
     * @param username
     * @return
     */
    public Mono<String> getSessionId(String username) {
        return reactiveStringRedisTemplate
                .opsForHash()
                .get("sessions", username)
                .switchIfEmpty(Mono.empty())
                .cast(String.class);
    }

    /**
     * 根据 SessionId， 查询用户 Session 是否存在
     * @param sessionId
     * @return
     */
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
