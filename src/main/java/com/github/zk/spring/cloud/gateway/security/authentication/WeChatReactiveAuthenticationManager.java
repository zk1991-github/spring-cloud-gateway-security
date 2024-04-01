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

package com.github.zk.spring.cloud.gateway.security.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zk.spring.cloud.gateway.security.core.LoginProcessor;
import com.github.zk.spring.cloud.gateway.security.core.WeChatUserDetails;
import com.github.zk.spring.cloud.gateway.security.pojo.WeChatDO;
import com.github.zk.spring.cloud.gateway.security.pojo.WeChatUserInfo;
import com.github.zk.spring.cloud.gateway.security.property.WeChatProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 微信登录认证管理器
 *
 * @author zk
 * @since 3.0
 */
public class WeChatReactiveAuthenticationManager implements ReactiveAuthenticationManager {
    private final Logger logger = LoggerFactory.getLogger(WeChatReactiveAuthenticationManager.class);

    /** rest请求模板 */
    private final WebClient webClient = WebClient.builder().build();
    /** 登录处理器 */
    private final LoginProcessor loginProcessor;
    /** 微信配置 */
    private final WeChatProperties weChatProperties;
    /** 微信用户详情 */
    private final WeChatUserDetails weChatUserDetails;
    /** 请求 */
    private final ServerWebExchange exchange;
    /** Security 上下文仓储 */
    private ServerSecurityContextRepository securityContextRepository = new WebSessionServerSecurityContextRepository();

    public WeChatReactiveAuthenticationManager(LoginProcessor loginProcessor, WeChatProperties weChatProperties,
                                               WeChatUserDetails weChatUserDetails,
                                               ServerWebExchange exchange) {
        this.loginProcessor = loginProcessor;
        this.weChatProperties = weChatProperties;
        this.weChatUserDetails = weChatUserDetails;
        this.exchange = exchange;
    }

    public void setSecurityContextRepository(ServerSecurityContextRepository securityContextRepository) {
        this.securityContextRepository = securityContextRepository;
    }


    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        // 登录认证
        String weChatCode = (String) authentication.getPrincipal();

        return weChatRequest(weChatCode)
                .filter(weChatResult ->
                        // 过滤掉认证失败的请求
                        weChatResult.getErrcode() == null || weChatResult.getErrcode().equals(0))
                // 返回空时抛出失败异常
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BadCredentialsException("微信认证失败!"))))
                .flatMap(weChatResult -> {
                    String openId = weChatResult.getOpenid();
                    // 登录处理
                    return loginProcessor
                            .sessionLimitProcess(openId)
                            .filter(isAllow -> isAllow)
                            .switchIfEmpty(Mono.defer(() -> Mono.error(new SessionAuthenticationException("超过最大登录人数！"))))
                            .then(Mono.defer(() -> {
                                WeChatUserInfo weChatUserInfo = (WeChatUserInfo) weChatUserDetails;
                                weChatUserInfo.setOpenid(openId);
                                return Mono.just(createWeChatAuthenticationToken(weChatUserInfo));
                            }));
                })
                .doOnNext(weChatAuthenticationToken -> {
                    // 登录成功存储认证信息
                    SecurityContextImpl securityContext = new SecurityContextImpl();
                    securityContext.setAuthentication(weChatAuthenticationToken);
                    this.securityContextRepository.save(exchange, securityContext)
                            .then(Mono.defer(() -> Mono.just(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext))))).subscribe();
                });
    }

    /**
     * 微信请求
     * @param weChatCode 微信 code
     * @return 请求结果
     */
    private Mono<WeChatDO> weChatRequest(String weChatCode) {
        String formatUrl = String.format(weChatProperties.getUrl(),
                weChatProperties.getAppid(), weChatProperties.getAppsecret(), weChatCode);
        return webClient
                .get()
                .uri(formatUrl)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(String.class))
                .map(s -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        return objectMapper.readValue(s, WeChatDO.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return new WeChatDO();
                })
                .onErrorResume(Exception.class, (ex) -> {
                    logger.info(ex.getMessage());
                    return Mono.empty();
                });
    }

    /**
     * 创建认证令牌
     *
     * @param weChatUserDetails 微信用户详情
     * @return 认证对象
     */
    private Authentication createWeChatAuthenticationToken(WeChatUserDetails weChatUserDetails) {
        return new WeChatAuthenticationToken(weChatUserDetails, weChatUserDetails.getAuthorities());
    }
}
