package com.github.zk.spring.cloud.gateway.security.config;

import com.github.zk.spring.cloud.gateway.security.authentication.CustomReactiveAuthorizationManager;
import com.github.zk.spring.cloud.gateway.security.authentication.WebReactiveAuthenticationManager;
import com.github.zk.spring.cloud.gateway.security.authentication.WebRedirectServerAuthenticationFailureHandler;
import com.github.zk.spring.cloud.gateway.security.core.LoginProcessor;
import com.github.zk.spring.cloud.gateway.security.service.impl.DefaultUserImpl;
import java.net.URI;
import java.util.Collections;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.WebSessionServerLogoutHandler;
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
    private final static Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

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

    private final String LOGOUT_URL = "/login/logout";

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                            GatewayProperties gatewayProperties,
                                                            LoginProcessor loginProcessor) {
        http.authorizeExchange(exchanges -> {
            ServerHttpSecurity.AuthorizeExchangeSpec access = exchanges
                    .pathMatchers(SUCCESS_URL, FAIL_URL, INVALID_URL,
                            WECHAT_URL, LOGOUT_URL).permitAll();
            // 静态资源放行
            if (antPatterns != null && antPatterns.length > 0) {
                access.pathMatchers(antPatterns).permitAll();
            }
            // 设置授权管理器
            access.anyExchange()
                    .access(new CustomReactiveAuthorizationManager(gatewayProperties));
        })
                // 禁用http默认设置
                .httpBasic().disable()
                // 登录设置
                .formLogin()
                // 设置认证管理器
                .authenticationManager(new WebReactiveAuthenticationManager(defaultUserImpl(), loginProcessor))
                //登录服务地址
                .loginPage(LOGIN_URL)
                // 设置认证成功处理器
                .authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler(
                        proxyUrl + SUCCESS_URL))
                // 设置认证失败处理器
                .authenticationFailureHandler(new WebRedirectServerAuthenticationFailureHandler(
                        proxyUrl + FAIL_URL))
                // 设置无权限时端点
                .authenticationEntryPoint(new RedirectServerAuthenticationEntryPoint(proxyUrl + INVALID_URL))
                .and()
                // 登出设置
                .logout()
                // 设置登出处理器
                .logoutHandler(new WebSessionServerLogoutHandler())
                // 设置登出成功处理器
                .logoutSuccessHandler(createRedirectServerLogoutSuccessHandler())
                .and()
                // 禁用csrf拦截
                .csrf().disable()
                // 启用跨域拦截
                .cors()
//                .and()
//                .exceptionHandling().accessDeniedHandler(new HttpStatusServerAccessDeniedHandler(HttpStatus.FORBIDDEN))
        ;
        return http.build();
    }

    /**
     * 登录处理器
     * @param reactiveStringRedisTemplate 响应式 Redis 模板
     * @param sessionRepository 会话仓储
     * @return
     */
    @Bean
    public LoginProcessor loginProcessor(ReactiveStringRedisTemplate reactiveStringRedisTemplate,
                                         ReactiveRedisSessionRepository sessionRepository) {
        LoginProcessor loginProcessor = new LoginProcessor(reactiveStringRedisTemplate, sessionRepository);
        loginProcessor.setMaxSessions(maxSessions);
        return loginProcessor;
    }

    /**
     *  Security 跨域处理
     * @return
     */
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

    /**
     * 创建登出成功跳转处理器
     * @return
     */
    private RedirectServerLogoutSuccessHandler createRedirectServerLogoutSuccessHandler() {
        RedirectServerLogoutSuccessHandler redirectServerLogoutSuccessHandler = new RedirectServerLogoutSuccessHandler();
        redirectServerLogoutSuccessHandler.setLogoutSuccessUrl(URI.create(proxyUrl + LOGOUT_URL));
        return redirectServerLogoutSuccessHandler;
    }
}
