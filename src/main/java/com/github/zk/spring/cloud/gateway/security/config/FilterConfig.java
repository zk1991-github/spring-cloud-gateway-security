package com.github.zk.spring.cloud.gateway.security.config;

import com.github.zk.spring.cloud.gateway.security.filter.factory.SessionGatewayFilterFactory;
import com.github.zk.spring.cloud.gateway.security.filter.factory.TokenCheckGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zk
 * @date 2019/6/5 9:56
 */
@Configuration
public class FilterConfig {

    @Bean
    public TokenCheckGatewayFilterFactory getJwtCheck() {
        return new TokenCheckGatewayFilterFactory();
    }

    @Bean
    public SessionGatewayFilterFactory sessionGatewayFilterFactory() {
        return new SessionGatewayFilterFactory();
    }
}
