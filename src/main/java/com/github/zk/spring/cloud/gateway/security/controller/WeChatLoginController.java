package com.github.zk.spring.cloud.gateway.security.controller;

import com.github.zk.spring.cloud.gateway.security.authentication.exception.SessionStoreAuthenticationException;
import com.github.zk.spring.cloud.gateway.security.common.Response;
import com.github.zk.spring.cloud.gateway.security.core.LoginProcessor;
import com.github.zk.spring.cloud.gateway.security.pojo.WeChatDTO;
import com.github.zk.spring.cloud.gateway.security.service.IWeChatAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.security.core.AuthenticationException;
import org.springframework.session.data.redis.ReactiveRedisSessionRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 微信登录 请求控制
 *
 * @author zk
 * @date 2021/11/30 14:47
 */
@RestController
@RequestMapping("/login")
public class WeChatLoginController extends LoginProcessor {

    @Autowired
    private IWeChatAuthentication iWeChatAuthentication;
    @Autowired
    private LoginProcessor loginProcessor;

    public final static String WECHAT_LOGIN_URL = "/weChatLogin";

    public WeChatLoginController(ReactiveStringRedisTemplate reactiveStringRedisTemplate, ReactiveRedisSessionRepository sessionRepository) {
        super(reactiveStringRedisTemplate, sessionRepository);
    }

    @PostMapping(WECHAT_LOGIN_URL)
    public Mono<Response> weChatLogin(@RequestBody WeChatDTO weChatParam, ServerWebExchange exchange) {
        return iWeChatAuthentication
                .authenticate(loginProcessor, weChatParam.getWeChatCode(), weChatParam.getWeChatUserInfo(), exchange)
                .flatMap(weChatUserInfo ->
                            exchange.getSession().flatMap(webSession ->
                            webSessionProcess(weChatUserInfo.getOpenid(), webSession))
                            .map(flag -> {
                                Response response = Response.getInstance();
                                if (flag) {
                                    response.setOk(Response.CodeEnum.SUCCESSED, null, "登录成功！", weChatUserInfo);
                                } else {
                                    throw new SessionStoreAuthenticationException("会话存储失败");
                                }
                                return response;
                            })
                )
                .onErrorResume(AuthenticationException.class, (ex) -> {
                    Response response = Response.getInstance();
                    response.setError(Response.CodeEnum.FAIL, null, ex.getMessage());
                    return Mono.just(response);
                });
    }
}
