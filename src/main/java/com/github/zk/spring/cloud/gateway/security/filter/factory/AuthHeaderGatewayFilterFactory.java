package com.github.zk.spring.cloud.gateway.security.filter.factory;

import com.github.zk.spring.cloud.gateway.security.pojo.UserInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.WeChatUserInfo;
import com.github.zk.spring.cloud.gateway.security.util.IpUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author zhaokai
 * @since 6.0.1
 */
public class AuthHeaderGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthHeaderGatewayFilterFactory.Config> {

    @Value("${spring.security.source-ip-enable:false}")
    private boolean sourceIpEnable;

    public AuthHeaderGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // 1. 基础头信息（不依赖认证）
            ServerHttpRequest.Builder builder = exchange.getRequest().mutate();
            if (sourceIpEnable) {
                builder.header("XReal-IP", IpUtils.getIpAddr(exchange.getRequest()));
            }

            // 2. 认证头信息（依赖登录用户）
            return ReactiveSecurityContextHolder.getContext()
                    .map(SecurityContext::getAuthentication)
                    .flatMap(auth -> {
                        if (auth != null && auth.isAuthenticated()
                                && !"anonymousUser".equals(auth.getPrincipal())) {
                            Object principal = auth.getPrincipal();
                            if (principal instanceof UserInfo userInfo) {
                                builder.header("username", userInfo.getUsername());
                                builder.header("userId", String.valueOf(userInfo.getId()));
                            } else if (principal instanceof WeChatUserInfo weChatUserInfo) {
                                String encodedName = URLEncoder.encode(
                                        weChatUserInfo.getNickName(), StandardCharsets.UTF_8);
                                builder.header("username", encodedName);
                                builder.header("userId", weChatUserInfo.getOpenid());
                            }
                        }
                        return Mono.just(builder.build());
                    })
                    .defaultIfEmpty(builder.build())
                    .map(newRequest -> exchange.mutate().request(newRequest).build())
                    .flatMap(chain::filter);
        };
    }

    static class Config {
    }
}
