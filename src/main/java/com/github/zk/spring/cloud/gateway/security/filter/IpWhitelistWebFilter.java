/*
 *
 *  * Copyright 2021-2026 the original author or authors.
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

package com.github.zk.spring.cloud.gateway.security.filter;

import com.github.zk.spring.cloud.gateway.security.service.IWhitelist;
import com.github.zk.spring.cloud.gateway.security.util.IpUtils;
import com.github.zk.spring.cloud.gateway.security.util.MacUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 白名单拦截器
 * 在表单登录之前校验客户端 IP 地址是否在白名单中
 *
 * @author zhaokai
 * @since 5.1.0
 */
public class IpWhitelistWebFilter implements WebFilter {
    private final Logger logger = LoggerFactory.getLogger(IpWhitelistWebFilter.class);
    private final static String LOGIN_URL = "/login";
    private final IWhitelist iWhitelist;

    public IpWhitelistWebFilter(IWhitelist iWhitelist) {
        this.iWhitelist = iWhitelist;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        // 非 POST 或 非/login请求，直接放行
        if (request.getMethod() != HttpMethod.POST || !LOGIN_URL.equals(request.getURI().getPath())) {
            return chain.filter(exchange);
        }
        String ipAddr = IpUtils.getIpAddr(request);
        String macAddr = MacUtils.getMacAddr(request);
        return iWhitelist.isWhiteList(ipAddr, macAddr)
                // 查询结果为空，证明不在白名单
                .defaultIfEmpty(false)
                .flatMap(allowed -> {
                    if (allowed) {
                        // 白名单通过，继续认证
                        return chain.filter(exchange);
                    }
                    // 白名单未通过，直接返回401
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    response.getHeaders().add("Content-Type", "application/json; charset=UTF-8");
                    String body = "{\"code\":401,\"msg\":\"登录失败,当前主机未在白名单！\"}";
                    DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
                    return response.writeWith(Mono.just(buffer));
                });
    }
}
