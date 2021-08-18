package com.github.zk.spring.cloud.gateway.security.controller;

import com.github.zk.spring.cloud.gateway.security.common.Response;
import com.github.zk.spring.cloud.gateway.security.pojo.UserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 登录 请求控制
 *
 * @author zk
 * @date 2021/7/14 15:39
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @GetMapping("/success")
    public Mono<Response> success() {
        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.error(new IllegalStateException("ReactiveSecurityContext is empty")))
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(UserInfo.class)
                .map(userInfo -> {
                    Response response = Response.getInstance();
                    response.setOk(Response.CodeEnum.SUCCESSED, null, "登录成功！", userInfo);
                    return response;
                });
    }

    @GetMapping("/fail")
    public Response fail() {
        Response response = Response.getInstance();
        response.setError(10000, null, "用户名密码错误");
        return response;
    }

    @GetMapping("/invalid")
    public Response invalid() {
        Response response = Response.getInstance();
        response.setError(10000, null, "未登录或登录超时");
        return response;
    }
}