package com.github.zk.spring.cloud.gateway.security.controller;

import com.github.zk.spring.cloud.gateway.security.common.Response;
import com.github.zk.spring.cloud.gateway.security.pojo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.session.InMemoryWebSessionStore;
import reactor.core.publisher.Mono;

/**
 * 用户 请求控制
 *
 * @author zk
 * @date 2021/8/18 10:22
 */
@RestController
@RequestMapping("/gateway")
public class UserController {

    @Autowired
    private InMemoryWebSessionStore sessionStore;

    @GetMapping("/getUser")
    public Mono<Response> getUser() {
        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.error(new IllegalStateException("ReactiveSecurityContext is empty")))
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(UserInfo.class)
                .map(userInfo -> {
                    Response response = Response.getInstance();
                    response.setOk(Response.CodeEnum.SUCCESSED, null, "查询成功！", userInfo);
                    return response;
                });
    }

    @PostMapping("queryUser")
    public Mono<Response> queryUser() {
        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.error(new IllegalStateException("ReactiveSecurityContext is empty")))
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(UserInfo.class)
                .map(userInfo -> {
                    Response response = Response.getInstance();
                    response.setOk(Response.CodeEnum.SUCCESSED, null, "查询成功！", userInfo);
                    return response;
                });
    }

    /**
     * 在线用户数
     * @return
     */
    @GetMapping("/getOnlineNums")
    public Mono<Response> getOnlineNums() {
        Response response = Response.getInstance();
        int count = sessionStore.getSessions().size();
        response.setOk(Response.CodeEnum.SUCCESSED, null, "查询成功！", count);
        return Mono.justOrEmpty(response);
    }

}
