/*
 *
 *  * Copyright 2021-2022 the original author or authors.
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
 * Security ??????
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
        //??????????????????
        if (proxyUrl == null) {
            logger.info("???????????????");
            proxyUrl = "";
        } else {
            logger.info("?????????????????????{}???", proxyUrl);
        }
    }

    /**
     * ??????????????????
     */
    private final String LOGIN_URL = "/login";
    /**
     * ??????????????????
     */
    private final String SUCCESS_URL = "/login/success";
    /**
     * ??????????????????
     */
    private final String FAIL_URL = "/login/fail";
    /**
     * Session????????????
     */
    private final String INVALID_URL = "/login/invalid";

    private final String WECHAT_URL = "/login/weChatLogin";

    private final String LOGOUT_URL = "/login/logout";

    /**
     * ?????? Security ????????? bean
     * @param http http??????
     * @param gatewayProperties ????????????
     * @param loginProcessor ???????????????
     * @return ????????????bean
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
            // ??????????????????
            if (antPatterns != null && antPatterns.length > 0) {
                access.pathMatchers(antPatterns).permitAll();
            }
            // ?????????????????????
            access.anyExchange()
                    .access(new CustomReactiveAuthorizationManager(gatewayProperties, iPermission));
        })
                // ??????http????????????
                .httpBasic().disable()
                // ????????????
                .formLogin()
                // ?????????????????????
                .authenticationManager(new WebReactiveAuthenticationManager(userDetailsService, loginProcessor))
                //??????????????????
                .loginPage(LOGIN_URL)
                // ???????????????????????????
                .authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler(
                        proxyUrl + SUCCESS_URL))
                // ???????????????????????????
                .authenticationFailureHandler(new WebRedirectServerAuthenticationFailureHandler(
                        proxyUrl + FAIL_URL))
                // ????????????????????????
                .authenticationEntryPoint(new RedirectServerAuthenticationEntryPoint(proxyUrl + INVALID_URL))
                .and()
                // ????????????
                .logout()
                // ?????????????????????
                .logoutHandler(new WebSessionServerLogoutHandler())
                // ???????????????????????????
                .logoutSuccessHandler(createRedirectServerLogoutSuccessHandler())
                .and()
                // ??????csrf??????
                .csrf().disable()
                // ??????????????????
                .cors()
//                .and()
//                .exceptionHandling().accessDeniedHandler(new HttpStatusServerAccessDeniedHandler(HttpStatus.FORBIDDEN))
        ;
        return http.build();
    }

    /**
     * ????????????????????? bean
     * @param reactiveStringRedisTemplate ????????? Redis ??????
     * @param sessionRepository ????????????
     * @return ??????????????? bean
     */
    @Bean
    public LoginProcessor loginProcessor(ReactiveStringRedisTemplate reactiveStringRedisTemplate,
                                         ReactiveRedisSessionRepository sessionRepository) {
        LoginProcessor loginProcessor = new LoginProcessor(reactiveStringRedisTemplate, sessionRepository);
        loginProcessor.setMaxSessions(maxSessions);
        return loginProcessor;
    }

    /**
     *  Security ????????????
     * @return ??????????????????
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
     * ???????????????????????? Bean
     *
     * @return ????????????
     */
    @Bean
    @ConditionalOnMissingBean
    public DefaultUserImpl userDetailsService(LoginProperties properties,
                                                      UserMapper userMapper) {
        return new DefaultUserImpl(properties, userMapper);
    }

    /**
     * ?????????????????????????????????
     * @return ???????????????????????????
     */
    private RedirectServerLogoutSuccessHandler createRedirectServerLogoutSuccessHandler() {
        RedirectServerLogoutSuccessHandler redirectServerLogoutSuccessHandler = new RedirectServerLogoutSuccessHandler();
        redirectServerLogoutSuccessHandler.setLogoutSuccessUrl(URI.create(proxyUrl + LOGOUT_URL));
        return redirectServerLogoutSuccessHandler;
    }
}
