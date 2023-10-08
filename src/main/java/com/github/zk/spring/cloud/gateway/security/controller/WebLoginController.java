/*
 *
 *  * Copyright 2021-2023 the original author or authors.
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
import org.springframework.web.bind.annotation.RequestParam;
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
                                            Response response = Response.getInstance();
                                            if (loginStatus) {
                                                // 记录日志
                                                logHolder.loginLog(exchange, userInfo, 1, "登录成功");
                                                response.setOk(Response.CodeEnum.SUCCESSED, null, "登录成功！", userInfo);
                                            } else {
                                                // 记录日志
                                                logHolder.loginLog(exchange, userInfo, 0, "登录失败");
                                                response.setError(Response.CodeEnum.FAIL, null, "登录失败，Session存储失败！");
                                            }
                                            return response;
                                        })
                        )
                );
    }

    @GetMapping("/failSession")
    public Response failSession(WebSession session) {
        Response response = Response.getInstance();
        String message = session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        // message为空时，请求工具没有存储session， 因此跳转后session被重新创建
        if (ObjectUtils.isEmpty(message)) {
            response.setError(Response.CodeEnum.FAIL, null,
                    "请求工具未能存储Cookies，请配置后重试！");
            return response;
        }

        if (ObjectUtils.nullSafeEquals(message, "Invalid Credentials")) {
            message = "密码错误";
            // TODO 考虑是否增加登录失败日志， 应考虑日志过多问题，使用存在的用户进行记录
        }
        response.setError(Response.CodeEnum.FAIL, null, message);
        //使当前session失效
        session.invalidate().subscribe();
        return response;
    }

    @GetMapping("/fail")
    public Response fail(@RequestParam("exception") String exception) {
        Response response = Response.getInstance();
        if (ObjectUtils.nullSafeEquals(exception, "Invalid Credentials")) {
            exception = "密码错误";
            // TODO 考虑是否增加登录失败日志， 应考虑日志过多问题，使用存在的用户进行记录
        }
        response.setError(Response.CodeEnum.FAIL, null, exception);
        return response;
    }

    @GetMapping("/invalid")
    public Response invalid(WebSession session) {
        Response response = Response.getInstance();
        response.setError(Response.CodeEnum.INVALID, null, "未登录或登录超时");
        //使当前session失效
        session.invalidate().subscribe();
        return response;
    }

    @GetMapping("/logout")
    public Response logout() {
        Response response = Response.getInstance();
        response.setOk(Response.CodeEnum.SUCCESSED, null, "登出成功！", null);
        return response;
    }
}