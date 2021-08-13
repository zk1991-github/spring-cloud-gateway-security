package com.github.zk.spring.cloud.gateway.security.config;

import com.github.zk.spring.cloud.gateway.security.handler.CustomReactiveAuthorizationManager;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;

/**
 * Security 配置
 *
 * @author zk
 * @date 2021/7/10 9:43
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, GatewayProperties gatewayProperties) {
        http.csrf().disable().cors();
        http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers( "/login/success", "/login/fail", "/login/invalid").permitAll()
                        .anyExchange()
                        .access(new CustomReactiveAuthorizationManager(gatewayProperties))
                )
                .httpBasic().disable()
                .formLogin()
                //登录服务地址
                .loginPage("/login")
                .authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler(
                        "/login/success"))
                .authenticationFailureHandler(new RedirectServerAuthenticationFailureHandler(
                        "/login/fail"))
                .authenticationEntryPoint(new RedirectServerAuthenticationEntryPoint("/login/invalid"))
//                .and()
//                .exceptionHandling().accessDeniedHandler(new HttpStatusServerAccessDeniedHandler(HttpStatus.FORBIDDEN))
        ;
        return http.build();
    }
}
