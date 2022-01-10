package com.github.zk.spring.cloud.gateway.security.config;

import com.github.zk.spring.cloud.gateway.security.filter.factory.SessionGatewayFilterFactory;
import com.github.zk.spring.cloud.gateway.security.filter.factory.TokenCheckGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 拦截配置
 * @author zk
 * @date 2019/6/5 9:56
 */
@Configuration
public class FilterConfig {

    /**
     * 注入token拦截 bean
     * @return token拦截工厂
     */
    @Bean
    public TokenCheckGatewayFilterFactory getTokenCheck() {
        return new TokenCheckGatewayFilterFactory();
    }

    /**
     * 注入session拦截 bean
     * @return session拦截工厂
     */
    @Bean
    public SessionGatewayFilterFactory sessionGatewayFilterFactory() {
        return new SessionGatewayFilterFactory();
    }
}
