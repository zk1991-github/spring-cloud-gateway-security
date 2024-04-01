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

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.DefaultServerRedirectStrategy;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationFailureHandler;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

/**
 * 自定义登录失败处理器
 *
 * @author zk
 * @since 3.0
 */
public class WebRedirectServerAuthenticationFailureHandler extends RedirectServerAuthenticationFailureHandler {

    private final String location;

    private final ServerRedirectStrategy redirectStrategy = new DefaultServerRedirectStrategy();

    /**
     * Creates an instance
     *
     * @param location the location to redirect to (i.e. "/login?failed")
     */
    public WebRedirectServerAuthenticationFailureHandler(String location) {
        super(location);
        this.location = location;
    }

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        String encodeUrl = null;
        try {
            // 编码url参数，防止空格等特殊符号问题
            encodeUrl = URLEncoder.encode(exception.getMessage(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 跳转增加参数，防止无session存储的请求工具，请求时造成redis存储大量session，无法清除
        return redirectStrategy.sendRedirect(webFilterExchange.getExchange(),
                URI.create(location + "?exception=" + encodeUrl));
    }


}
