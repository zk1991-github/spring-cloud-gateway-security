package com.github.zk.spring.cloud.gateway.security.filter.factory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;

import java.time.Duration;

/**
 * Session 设置拦截
 *
 * @author zk
 * @date 2021/8/16 11:04
 */
public class SessionGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    @Value("${spring.cloud.gateway.session.timeout}")
    private long timeout;

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            exchange.getSession().doOnNext(webSession -> webSession.setMaxIdleTime(Duration.ofMinutes(timeout))).subscribe();   //修改session时间
            return chain.filter(exchange);
        };
    }
}
