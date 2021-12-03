package com.github.zk.spring.cloud.gateway.security.config;

import com.github.zk.spring.cloud.gateway.security.core.LoginProcessor;
import com.github.zk.spring.cloud.gateway.security.handler.CustomReactiveAuthenticationManager;
import com.github.zk.spring.cloud.gateway.security.handler.CustomReactiveAuthorizationManager;
import com.github.zk.spring.cloud.gateway.security.handler.CustomRedirectServerAuthenticationFailureHandler;
import com.github.zk.spring.cloud.gateway.security.service.impl.DefaultUserImpl;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.session.data.redis.ReactiveRedisSessionRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * Security 配置
 *
 * @author zk
 * @date 2021/7/10 9:43
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${spring.cloud.gateway.session.maxSessions}")
    private int maxSessions;
    @Autowired
    private ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    /**
     * 登录服务地址
     */
    private final String LOGIN_URL = "/login";
    /**
     * 登录成功地址
     */
    private final String SUCCESS_URL = "/login/success";
    /**
     * 登录失败地址
     */
    private final String FAIL_URL = "/login/fail";
    /**
     * Session失效地址
     */
    private final String INVALID_URL = "/login/invalid";

    private final String WECHAT_URL = "/login/weChatLogin";

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                            GatewayProperties gatewayProperties,
                                                            LoginProcessor loginProcessor) {
        http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(SUCCESS_URL, FAIL_URL, INVALID_URL, WECHAT_URL).permitAll()
                        .anyExchange()
                        .access(new CustomReactiveAuthorizationManager(gatewayProperties))
                )
                .httpBasic().disable()
                .formLogin()
                .authenticationManager(new CustomReactiveAuthenticationManager(defaultUserImpl(), loginProcessor))
                //登录服务地址
                .loginPage(LOGIN_URL)
                .authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler(
                        SUCCESS_URL))
                .authenticationFailureHandler(new CustomRedirectServerAuthenticationFailureHandler(
                        FAIL_URL))
                .authenticationEntryPoint(new RedirectServerAuthenticationEntryPoint(INVALID_URL))
                .and()
                .csrf().disable()
                .cors()
//                .and()
//                .exceptionHandling().accessDeniedHandler(new HttpStatusServerAccessDeniedHandler(HttpStatus.FORBIDDEN))
        ;
        return http.build();
    }

    @Bean
    public LoginProcessor loginProcessor(ReactiveStringRedisTemplate reactiveStringRedisTemplate,
                                         ReactiveRedisSessionRepository sessionRepository) {
        LoginProcessor loginProcessor = new LoginProcessor(reactiveStringRedisTemplate, sessionRepository);
        loginProcessor.setMaxSessions(maxSessions);
        return loginProcessor;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 定义默认用户实现 Bean
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public DefaultUserImpl defaultUserImpl() {
        return new DefaultUserImpl() {
        };
    }
}
