package com.github.zk.spring.cloud.gateway.security.authentication;

import com.github.zk.spring.cloud.gateway.security.core.LoginProcessor;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

/**
 * 自定义认证管理器
 *
 * @author zk
 * @date 2021/9/3 10:48
 */
public class WebReactiveAuthenticationManager extends UserDetailsRepositoryReactiveAuthenticationManager {

    /**
     * 允许的最大登录人数
     */
    private final LoginProcessor loginProcessor;

    public WebReactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService,
                                            LoginProcessor loginProcessor) {
        super(userDetailsService);
        this.loginProcessor = loginProcessor;
    }

    /**
     * 获取用户
     * @param username
     * @return
     */
    @Override
    public Mono<UserDetails> retrieveUser(String username) {
        //判断是否超过允许最大登录人数
        return loginProcessor
                .sessionLimitProcess(username)
                .filter(isAllow -> isAllow)
                .then(super.retrieveUser(username));
    }

}
