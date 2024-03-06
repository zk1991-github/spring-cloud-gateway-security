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

package com.github.zk.spring.cloud.gateway.security.controller;

import com.github.zk.spring.cloud.gateway.security.authentication.exception.SessionStoreAuthenticationException;
import com.github.zk.spring.cloud.gateway.security.common.Response;
import com.github.zk.spring.cloud.gateway.security.core.GatewaySecurityCache;
import com.github.zk.spring.cloud.gateway.security.core.LoginProcessor;
import com.github.zk.spring.cloud.gateway.security.pojo.WeChatDTO;
import com.github.zk.spring.cloud.gateway.security.service.IWeChatAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.session.DefaultWebSessionManager;
import org.springframework.web.server.session.WebSessionManager;
import reactor.core.publisher.Mono;

/**
 * 微信登录 请求控制
 *
 * @author zk
 * @since 3.0
 */
@RestController
@RequestMapping("/login")
public class WeChatLoginController extends LoginProcessor {

    @Autowired
    private IWeChatAuthentication iWeChatAuthentication;
    @Autowired
    private LoginProcessor loginProcessor;

    public final static String WECHAT_LOGIN_URL = "/weChatLogin";

    public WeChatLoginController(GatewaySecurityCache gatewaySecurityCache, WebSessionManager webSessionManager) {
        super(gatewaySecurityCache, ((DefaultWebSessionManager) webSessionManager).getSessionStore());
    }

    /**
     * 微信登录
     * @param weChatParam 微信参数
     * @param exchange 请求
     * @return 登录状态
     */
    @PostMapping(WECHAT_LOGIN_URL)
    public Mono<Response> weChatLogin(@RequestBody WeChatDTO weChatParam, ServerWebExchange exchange) {
        return iWeChatAuthentication
                .authenticate(loginProcessor, weChatParam.getWeChatCode(), weChatParam.getWeChatUserInfo(), exchange)
                .flatMap(weChatUserInfo ->
                            exchange.getSession().flatMap(webSession ->
                            webSessionProcess(weChatUserInfo.getOpenid(), webSession))
                            .map(flag -> {
                                if (flag) {
                                    return Response.setOk(weChatUserInfo);
                                } else {
                                    throw new SessionStoreAuthenticationException("会话存储失败");
                                }
                            })
                )
                .onErrorResume(AuthenticationException.class, (ex) ->
                        Mono.just(Response.setError(ex.getMessage())));
    }
}
