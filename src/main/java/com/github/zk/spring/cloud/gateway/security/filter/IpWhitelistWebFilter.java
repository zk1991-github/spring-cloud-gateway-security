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

import com.github.zk.spring.cloud.gateway.security.core.WhitelistToggle;
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
 * 在表单登录之前校验客户端 IP 地址和 MAC 地址是否在白名单中
 * 支持通过 {@link WhitelistToggle} 在运行时分别控制 IP 白名单和 MAC 白名单的开关
 *
 * @author zhaokai
 * @since 5.1.0
 */
public class IpWhitelistWebFilter implements WebFilter {
    private final Logger logger = LoggerFactory.getLogger(IpWhitelistWebFilter.class);
    private final static String LOGIN_URL = "/login";
    private final IWhitelist iWhitelist;
    private final WhitelistToggle whitelistToggle;

    public IpWhitelistWebFilter(IWhitelist iWhitelist, WhitelistToggle whitelistToggle) {
        this.iWhitelist = iWhitelist;
        this.whitelistToggle = whitelistToggle;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        // 非 POST 或 非/login请求，直接放行
        if (request.getMethod() != HttpMethod.POST || !LOGIN_URL.equals(request.getURI().getPath())) {
            return chain.filter(exchange);
        }
        // IP 和 MAC 白名单都关闭，直接放行
        if (whitelistToggle.isAllDisabled()) {
            return chain.filter(exchange);
        }

        String ipAddr = IpUtils.getIpAddr(request);
        String macAddr = MacUtils.getMacAddr(request);

        // IP 和 MAC 开关都开启：必须在同一条白名单记录中同时匹配 IP 和 MAC
        // 仅开启其中之一：分别查询对应的白名单
        Mono<Boolean> check;
        if (whitelistToggle.isIpEnabled() && whitelistToggle.isMacEnabled()) {
            check = iWhitelist.isWhiteList(ipAddr, macAddr).defaultIfEmpty(false);
        } else if (whitelistToggle.isIpEnabled()) {
            check = iWhitelist.isIpWhiteList(ipAddr).defaultIfEmpty(false);
        } else {
            check = iWhitelist.isMacWhiteList(macAddr).defaultIfEmpty(false);
        }
        return check.flatMap(allowed -> {
            if (allowed) {
                // 白名单通过，继续认证
                return chain.filter(exchange);
            }
            // 白名单未通过，返回 401
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().add("Content-Type", "application/json; charset=UTF-8");
            String message;
            if (whitelistToggle.isIpEnabled() && whitelistToggle.isMacEnabled()) {
                message = "登录失败,当前主机未在白名单！";
            } else if (whitelistToggle.isIpEnabled()) {
                message = "登录失败,当前主机 IP 未在白名单！";
            } else {
                message = "登录失败,当前主机 MAC 未在白名单！";
            }
            String body = "{\"code\":401,\"msg\":\"" + message + "\"}";
            DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        });
    }
}
