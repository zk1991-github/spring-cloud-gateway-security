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
 * ???????????????????????????
 *
 * @author zk
 * @date 2021/11/15 10:35
 */
public class WeChatReactiveAuthenticationManager implements ReactiveAuthenticationManager {
    private final Logger logger = LoggerFactory.getLogger(WeChatReactiveAuthenticationManager.class);

    /** rest???????????? */
    private final WebClient webClient = WebClient.builder().build();
    /** ??????????????? */
    private final LoginProcessor loginProcessor;
    /** ???????????? */
    private final WeChatProperties weChatProperties;
    /** ?????????????????? */
    private final WeChatUserDetails weChatUserDetails;
    /** ?????? */
    private final ServerWebExchange exchange;
    /** Security ??????????????? */
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
        // ????????????
        String weChatCode = (String) authentication.getPrincipal();

        return weChatRequest(weChatCode)
                .filter(weChatResult ->
                        weChatResult.getErrcode() == null || weChatResult.getErrcode().equals(0))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BadCredentialsException("??????????????????!"))))
                .flatMap(weChatResult -> {
                    String openId = weChatResult.getOpenid();
                    return loginProcessor
                            .sessionLimitProcess(openId)
                            .filter(isAllow -> isAllow)
                            .switchIfEmpty(Mono.defer(() -> Mono.error(new SessionAuthenticationException("???????????????????????????"))))
                            .then(Mono.defer(() -> {
                                WeChatUserInfo weChatUserInfo = (WeChatUserInfo) weChatUserDetails;
                                weChatUserInfo.setOpenid(openId);
                                return Mono.just(createWeChatAuthenticationToken(weChatUserInfo));
                            }));
                })
                .doOnNext(weChatAuthenticationToken -> {
                    SecurityContextImpl securityContext = new SecurityContextImpl();
                    securityContext.setAuthentication(weChatAuthenticationToken);
                    this.securityContextRepository.save(exchange, securityContext)
                            .then(Mono.defer(() -> Mono.just(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext))))).subscribe();
                });
    }

    /**
     * ????????????
     * @param weChatCode ?????? code
     * @return ????????????
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
     * ??????????????????
     *
     * @param weChatUserDetails ??????????????????
     * @return ????????????
     */
    private Authentication createWeChatAuthenticationToken(WeChatUserDetails weChatUserDetails) {
        return new WeChatAuthenticationToken(weChatUserDetails, weChatUserDetails.getAuthorities());
    }
}
