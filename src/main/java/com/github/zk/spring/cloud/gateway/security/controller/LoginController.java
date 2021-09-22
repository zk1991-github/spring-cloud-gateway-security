package com.github.zk.spring.cloud.gateway.security.controller;

import com.github.zk.spring.cloud.gateway.security.common.Response;
import com.github.zk.spring.cloud.gateway.security.pojo.UserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录 请求控制
 *
 * @author zk
 * @date 2021/7/14 15:39
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    private final Map<String, WebSession> USER_SESSIONS = new HashMap<>();

    @GetMapping("/success")
    public Mono<Response> success(ServerWebExchange exchange) {
        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.error(new IllegalStateException("ReactiveSecurityContext is empty")))
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(UserInfo.class)
                .map(userInfo -> {
                    Response response = Response.getInstance();
                    response.setOk(Response.CodeEnum.SUCCESSED, null, "登录成功！", userInfo);
                    exchange.getSession().doOnNext(webSession -> {
                        WebSession session = USER_SESSIONS.get(userInfo.getUsername());
                        // 注意，同一浏览器登录同一账号时，userSessions内的WebSession会改变。
                        // 因此需要判断历史和当前sessionId是否一致，如果一致，表示同一浏览器登录，框架自动失效上次session
                        if (session != null && !ObjectUtils.nullSafeEquals(session.getId(), webSession.getId()) && !session.isExpired()) {
                            session.invalidate();
                        }
                        USER_SESSIONS.put(userInfo.getUsername(), webSession);
                    }).subscribe();
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