package com.github.zk.spring.cloud.gateway.security.controller;

import com.github.zk.spring.cloud.gateway.security.common.Response;
import com.github.zk.spring.cloud.gateway.security.core.LoginProcessor;
import com.github.zk.spring.cloud.gateway.security.pojo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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
                                                response.setOk(Response.CodeEnum.SUCCESSED, null, "登录成功！", userInfo);
                                            } else {
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
        AuthenticationException authenticationException = session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        String message = authenticationException.getMessage();
        if (ObjectUtils.nullSafeEquals(message, "Invalid Credentials")) {
            message = "密码错误";
        }
        response.setError(10000, null, message);
        //使当前session失效
        session.invalidate().subscribe();
        return response;
    }

    @GetMapping("/invalid")
    public Response invalid() {
        Response response = Response.getInstance();
        response.setError(10000, null, "未登录或登录超时");
        return response;
    }
}