package com.github.zk.spring.cloud.gateway.security.controller;

import com.github.zk.spring.cloud.gateway.security.common.Response;
import com.github.zk.spring.cloud.gateway.security.pojo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.session.data.redis.ReactiveRedisSessionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录 请求控制
 *
 * @author zk
 * @date 2021/7/14 15:39
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private ReactiveStringRedisTemplate reactiveStringRedisTemplate;
    @Autowired
    private ReactiveRedisSessionRepository sessionRepository;

    public final static Map<String, WebSession> USER_SESSIONS = new ConcurrentHashMap<>();

    @Value("${spring.cloud.gateway.session.maxSessions}")
    private int maxSessions;

    @GetMapping("/success")
    public Mono<Response> success(ServerWebExchange exchange) {
        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.error(new IllegalStateException("ReactiveSecurityContext is empty")))
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(UserInfo.class)
                .flatMap(userInfo ->
                        exchange.getSession().flatMap(webSession ->
                            webSessionProcess(userInfo, webSession)
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

    /**
     * 处理 webSession，判断是否超过允许最大登录人数
     * 注意：已经登录过的用户重复登录时，不受限制
     * @param userInfo
     * @param webSession
     * @return
     */
    private Mono<Boolean> webSessionProcess(UserInfo userInfo, WebSession webSession) {
        return getSessionId(userInfo)
                .flatMap(sessionId -> changeSession(userInfo, sessionId, webSession.getId()))
                .switchIfEmpty(Mono.defer(() -> saveSession(userInfo, webSession.getId())));
    }

    /**
     * 在线用户数
     * @return
     */
    public Mono<Long> onlineNum() {
        ScanOptions options = ScanOptions
                .scanOptions()
                .match(ReactiveRedisSessionRepository.DEFAULT_NAMESPACE + ":*")
                .build();
        return reactiveStringRedisTemplate.scan(options).count();
    }

    public Mono<String> getSessionId(UserInfo userInfo) {
        return reactiveStringRedisTemplate
                .opsForHash()
                .get("sessions", userInfo.getUsername())
                .switchIfEmpty(Mono.empty())
                .cast(String.class);
    }

    /**
     * 改变登录会话，删除老会话，保存新会话(异地踢出)
     * @param userInfo
     * @param oldSessionId
     * @param newSessionId
     */
    public Mono<Boolean> changeSession(UserInfo userInfo, String oldSessionId, String newSessionId) {
        Mono<Void> delSession = sessionRepository.deleteById(oldSessionId);
        Mono<Boolean> removeSession = removeSession(userInfo);
        Mono<Boolean> saveSession = saveSession(userInfo, newSessionId);
        return delSession.then(removeSession).then(saveSession);
    }

//    public Mono<Boolean> changeSession(UserInfo userInfo, String oldSessionId, String newSessionId) {
//        sessionRepository.deleteById(oldSessionId).subscribe();
//        return saveSession(userInfo, newSessionId).map(aBoolean -> {
//                    return aBoolean;
//        });
//    }

    /**
     * 保存会话
     * @param userInfo
     * @param sessionId
     */
    public Mono<Boolean> saveSession(UserInfo userInfo, String sessionId) {
        return reactiveStringRedisTemplate
                .opsForHash()
                .put("sessions", userInfo.getUsername(), sessionId);
    }

    public Mono<Boolean> removeSession(UserInfo userInfo) {
        return reactiveStringRedisTemplate
                .opsForHash()
                .remove("sessions", userInfo.getUsername()).map(delNum -> delNum > 0);
    }

    public static void main(String[] args) {
        Mono.just("a")
                .flatMap(s ->
                        notEmpty()
                )
                .switchIfEmpty(
                        Mono.defer(() -> empty())
                )
                .subscribe(System.out::println);
    }

    public static Mono<Boolean> empty() {
        System.out.println("empty()");
        return Mono.just(false);
    }

    public static Mono<Boolean> notEmpty() {
        System.out.println("notEmpty()");
        return Mono.just(true);
    }
}