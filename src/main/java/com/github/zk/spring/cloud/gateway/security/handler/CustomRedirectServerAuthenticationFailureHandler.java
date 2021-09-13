package com.github.zk.spring.cloud.gateway.security.handler;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationFailureHandler;
import reactor.core.publisher.Mono;

/**
 * 自定义登录失败处理器
 *
 * @author zk
 * @date 2021/9/7 11:33
 */
public class CustomRedirectServerAuthenticationFailureHandler extends RedirectServerAuthenticationFailureHandler {


    /**
     * Creates an instance
     *
     * @param location the location to redirect to (i.e. "/login?failed")
     */
    public CustomRedirectServerAuthenticationFailureHandler(String location) {
        super(location);
    }

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        return webFilterExchange.getExchange().getSession().doOnNext(webSession -> {
            webSession.getAttributes().put(WebAttributes.AUTHENTICATION_EXCEPTION, exception);
        }).then(super.onAuthenticationFailure(webFilterExchange, exception));
    }
}
