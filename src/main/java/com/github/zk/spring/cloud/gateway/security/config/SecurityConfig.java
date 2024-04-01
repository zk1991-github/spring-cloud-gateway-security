/*
 *
 *  * Copyright 2021-2024 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.github.zk.spring.cloud.gateway.security.config;

import com.github.zk.spring.cloud.gateway.security.authentication.CustomReactiveAuthorizationManager;
import com.github.zk.spring.cloud.gateway.security.authentication.WebReactiveAuthenticationManager;
import com.github.zk.spring.cloud.gateway.security.authentication.WebRedirectServerAuthenticationFailureHandler;
import com.github.zk.spring.cloud.gateway.security.core.LoginProcessor;
import com.github.zk.spring.cloud.gateway.security.dao.UserMapper;
import com.github.zk.spring.cloud.gateway.security.property.LoginProperties;
import com.github.zk.spring.cloud.gateway.security.service.IPermission;
import com.github.zk.spring.cloud.gateway.security.service.impl.DefaultUserImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.WebSessionServerLogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.Collections;

/**
 * Security 配置
 *
 * @author zk
 * @since 1.0
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private final static Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

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

    /**
     * 小程序登录地址
     */
    private final String WECHAT_URL = "/login/weChatLogin";

    /**
     * 登出地址
     */
    private final String LOGOUT_URL = "/login/logout";

    /**
     * 注入 Security 拦截链 bean
     *
     * @param http              http对象
     * @param gatewayProperties 网关配置
     * @param loginProcessor    登录处理器
     * @return 拦截对象bean
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                            GatewayProperties gatewayProperties,
                                                            LoginProcessor loginProcessor,
                                                            DefaultUserImpl userDetailsService,
                                                            IPermission iPermission) {
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
                    .access(new CustomReactiveAuthorizationManager(gatewayProperties, iPermission));
        })
                // 禁用http默认设置
                .httpBasic().disable()
                // 登录设置
                .formLogin()
                // 设置认证管理器
                .authenticationManager(new WebReactiveAuthenticationManager(userDetailsService, loginProcessor))
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
        ;


        http.anonymous();
        return http.build();
    }

    /**
     * Security 跨域处理
     *
     * @return 跨域配置对象
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许所有头请求
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        // 允许所有域请求
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        // 允许所有方法请求
        configuration.setAllowedMethods(Collections.singletonList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 允许所有地址请求
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 定义默认用户实现 Bean
     *
     * @return 用户实现
     */
    @Bean
    @ConditionalOnMissingBean
    public DefaultUserImpl userDetailsService(LoginProperties properties,
                                              UserMapper userMapper) {
        return new DefaultUserImpl(properties, userMapper);
    }

    /**
     * 创建登出成功跳转处理器
     *
     * @return 跳转成功处理器对象
     */
    private RedirectServerLogoutSuccessHandler createRedirectServerLogoutSuccessHandler() {
        RedirectServerLogoutSuccessHandler redirectServerLogoutSuccessHandler = new RedirectServerLogoutSuccessHandler();
        redirectServerLogoutSuccessHandler.setLogoutSuccessUrl(URI.create(proxyUrl + LOGOUT_URL));
        return redirectServerLogoutSuccessHandler;
    }
}
