package com.github.zk.spring.cloud.gateway.security.config;

import com.github.zk.spring.cloud.gateway.security.handler.CustomReactiveAuthorizationManager;
import com.github.zk.spring.cloud.gateway.security.service.impl.DefaultUserImpl;
import java.net.URI;
import java.util.Collections;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.WebSessionServerLogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.session.DefaultWebSessionManager;
import org.springframework.web.server.session.InMemoryWebSessionStore;
import org.springframework.web.server.session.WebSessionIdResolver;
import org.springframework.web.server.session.WebSessionManager;

/**
 * Security 配置
 *
 * @author zk
 * @date 2021/7/10 9:43
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
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
    /**
     * 登出地址
     */
    private final String LOGOUT_URL = "/login/logout";


    @Value("${spring.cloud.gateway.session.maxSessions}")
    private int maxSessions;
    @Value("${spring.web.proxy.url:#{null}}")
    private String proxyUrl;
    @Value("${spring.static.antpatterns:#{null }}")
    private String[] antPatterns;

    @PostConstruct
    public void init() {
        //代理地址处理
        if (proxyUrl == null) {
            logger.info("前端无代理");
            proxyUrl = "";
        } else {
            logger.info("前端代理地址【{}】", proxyUrl);
        }
    }


    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, GatewayProperties gatewayProperties) {

        http.authorizeExchange(exchanges -> {
            ServerHttpSecurity.AuthorizeExchangeSpec access = exchanges
                    .pathMatchers(SUCCESS_URL, FAIL_URL, INVALID_URL, LOGOUT_URL).permitAll();
            // 静态资源放行
            if (antPatterns != null && antPatterns.length > 0) {
                access.pathMatchers(antPatterns).permitAll();
            }
            // 设置授权管理器
            access.anyExchange().access(new CustomReactiveAuthorizationManager(gatewayProperties));
        })
                .httpBasic().disable()
                .formLogin()
                //登录服务地址
                .loginPage(LOGIN_URL)
                .authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler(
                        proxyUrl + SUCCESS_URL))
                .authenticationFailureHandler(new RedirectServerAuthenticationFailureHandler(
                        proxyUrl + FAIL_URL))
                .authenticationEntryPoint(new RedirectServerAuthenticationEntryPoint(proxyUrl + INVALID_URL))
                .and()
                // 登出设置
                .logout()
                .logoutHandler(new WebSessionServerLogoutHandler())
                // 设置登出成功处理器
                .logoutSuccessHandler(createRedirectServerLogoutSuccessHandler())
                .and()
                .csrf().disable()
                .cors()
//                .and()
//                .exceptionHandling().accessDeniedHandler(new HttpStatusServerAccessDeniedHandler(HttpStatus.FORBIDDEN))
        ;

        return http.build();
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
     * Session 管理 Bean
     *
     * @param webSessionManager
     * @return
     * @see WebFluxAutoConfiguration.EnableWebFluxConfiguration#webSessionManager(ObjectProvider < WebSessionIdResolver >)
     */
    @Bean
    public InMemoryWebSessionStore sessionStore(WebSessionManager webSessionManager) {
        InMemoryWebSessionStore sessionStore = (InMemoryWebSessionStore) ((DefaultWebSessionManager) webSessionManager).getSessionStore();
        // 设置最大同时在线人数
        sessionStore.setMaxSessions(maxSessions);
        return sessionStore;
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

    /**
     * 创建登出成功跳转处理器
     *
     * @return
     */
    private RedirectServerLogoutSuccessHandler createRedirectServerLogoutSuccessHandler() {
        RedirectServerLogoutSuccessHandler redirectServerLogoutSuccessHandler = new RedirectServerLogoutSuccessHandler();
        redirectServerLogoutSuccessHandler.setLogoutSuccessUrl(URI.create(proxyUrl + LOGOUT_URL));
        return redirectServerLogoutSuccessHandler;
    }

}
