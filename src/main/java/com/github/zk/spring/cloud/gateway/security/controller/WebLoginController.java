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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zk.spring.cloud.gateway.security.common.CodeEnum;
import com.github.zk.spring.cloud.gateway.security.common.Response;
import com.github.zk.spring.cloud.gateway.security.core.LoginProcessor;
import com.github.zk.spring.cloud.gateway.security.log.LogHolder;
import com.github.zk.spring.cloud.gateway.security.pojo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.WebAttributes;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * 登录 请求控制
 *
 * @author zk
 * @since 1.0
 */
@RestController
@RequestMapping("/login")
public class WebLoginController {

    @Autowired
    private LoginProcessor loginProcessor;

    @Autowired
    private LogHolder logHolder;

    @GetMapping("/success")
    public Mono<Response> success(ServerWebExchange exchange) {
        return ReactiveSecurityContextHolder.getContext()
                // session 中无数据时抛出异常
                .switchIfEmpty(Mono.error(new IllegalStateException("ReactiveSecurityContext is empty")))
                // 获取认证信息
                .map(SecurityContext::getAuthentication)
                // 获取用户信息
                .map(Authentication::getPrincipal)
                // 转换用户对象
                .cast(UserInfo.class)
                .flatMap(userInfo ->
                        exchange.getSession().flatMap(webSession ->
                                // 登录处理
                                loginProcessor
                                        .webSessionProcess(userInfo.getUsername(), webSession)
                                        // 登录状态处理
                                        .map(loginStatus -> {
                                            if (loginStatus) {
                                                // 记录日志
                                                logHolder.loginLog(exchange, userInfo, 1, "登录成功");
                                                return Response.setOk(userInfo);
                                            } else {
                                                // 记录日志
                                                logHolder.loginLog(exchange, userInfo, 0, "登录失败");
                                                return Response.setError(CodeEnum.FAIL);
                                            }
                                        })
                        )
                );
    }

    @GetMapping("/failSession")
    public Response failSession(WebSession session) {
        String message = session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        //使当前session失效
        session.invalidate().subscribe();
        // message为空时，请求工具没有存储session， 因此跳转后session被重新创建
        if (ObjectUtils.isEmpty(message)) {
            return Response.setError("请求工具未能存储Cookies，请配置后重试！");
        }

        if (ObjectUtils.nullSafeEquals(message, "Invalid Credentials")) {
            // TODO 考虑是否增加登录失败日志， 应考虑日志过多问题，使用存在的用户进行记录
            return Response.setError(CodeEnum.PASSWORD_ERROR);
        }
        return Response.setError(message);
    }

    @GetMapping("/fail")
    public Response fail(@RequestParam("exception") String exception) {
        if (ObjectUtils.nullSafeEquals(exception, "Invalid Credentials")) {
            // TODO 考虑是否增加登录失败日志， 应考虑日志过多问题，使用存在的用户进行记录
            return Response.setError(CodeEnum.PASSWORD_ERROR);
        }
        return Response.setError(CodeEnum.getByMsg(exception));
    }

    @GetMapping("/invalid")
    public Mono<Void> invalid(WebSession session, ServerWebExchange exchange) {
        //使当前session失效
        exchange.getResponse().setStatusCode(HttpStatus.FOUND);
        exchange.getResponse().getHeaders().setLocation(URI.create("/web/dist/index.html"));
        return session.invalidate().then(exchange.getResponse().setComplete());
    }

    @GetMapping("/logout")
    public Mono<Void> logout(WebSession session, ServerWebExchange exchange) {
        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        //使当前session失效
        serverHttpResponse.setStatusCode(HttpStatus.OK);
        serverHttpResponse.getHeaders().set("Content-Type", "application/json");
        // 组织返回结构
        Response response = Response.setOk();
        String body = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            body = objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return serverHttpResponse
                .writeWith(Mono.just(serverHttpResponse.bufferFactory()
                        .wrap(body.getBytes(StandardCharsets.UTF_8))))
                .then(session.invalidate());
    }
}