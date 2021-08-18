package com.github.zk.spring.cloud.gateway.security.check;

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

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            exchange.getSession().doOnNext(res -> res.setMaxIdleTime(Duration.ofMinutes(30))).subscribe();   //修改session时间
            return chain.filter(exchange);
        };
    }
}
