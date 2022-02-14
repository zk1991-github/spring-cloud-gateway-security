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

package com.github.zk.spring.cloud.gateway.security.controller;

import com.github.zk.spring.cloud.gateway.security.common.Response;
import com.github.zk.spring.cloud.gateway.security.core.LoginProcessor;
import com.github.zk.spring.cloud.gateway.security.log.LogHolder;
import com.github.zk.spring.cloud.gateway.security.pojo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.WebAttributes;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

/**
 * 登录 请求控制
 *
 * @author zk
 * @date 2021/7/14 15:39
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
                .switchIfEmpty(Mono.error(new IllegalStateException("ReactiveSecurityContext is empty")))
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(UserInfo.class)
                .flatMap(userInfo ->
                        exchange.getSession().flatMap(webSession ->
                                loginProcessor
                                        .webSessionProcess(userInfo.getUsername(), webSession)
                                        .map(loginStatus -> {
                                            Response response = Response.getInstance();
                                            if (loginStatus) {
                                                logHolder.loginLog(exchange, userInfo, 1, "登录成功");
                                                response.setOk(Response.CodeEnum.SUCCESSED, null, "登录成功！", userInfo);
                                            } else {
                                                logHolder.loginLog(exchange, userInfo, 0, "登录失败");
                                                response.setError(Response.CodeEnum.FAIL, null, "登录失败，超过最大登录人数");
                                            }
                                            return response;
                                        })
                        )
                );
    }

    @GetMapping("/fail")
    public Response fail(WebSession session) {
        Response response = Response.getInstance();
        String message = session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        if (ObjectUtils.nullSafeEquals(message, "Invalid Credentials")) {
            message = "密码错误";
        }
        response.setError(Response.CodeEnum.FAIL, null, message);
        //使当前session失效
        session.invalidate().subscribe();
        return response;
    }

    @GetMapping("/invalid")
    public Response invalid() {
        Response response = Response.getInstance();
        response.setError(Response.CodeEnum.INVALID, null, "未登录或登录超时");
        return response;
    }

    @GetMapping("/logout")
    public Response logout() {
        Response response = Response.getInstance();
        response.setOk(Response.CodeEnum.SUCCESSED, null, "登出成功！", null);
        return response;
    }
}