package com.github.zk.spring.cloud.gateway.security.service.impl;

import com.github.zk.spring.cloud.gateway.security.property.WeChatProperties;
import com.github.zk.spring.cloud.gateway.security.core.LoginProcessor;
import com.github.zk.spring.cloud.gateway.security.authentication.WeChatAuthenticationToken;
import com.github.zk.spring.cloud.gateway.security.authentication.WeChatReactiveAuthenticationManager;
import com.github.zk.spring.cloud.gateway.security.pojo.WeChatUserInfo;
import com.github.zk.spring.cloud.gateway.security.service.IWeChatAuthentication;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 微信认证实现
 *
 * @author zk
 * @date 2021/11/17 13:07
 */
@Service
public class WeChatAuthenticationImpl implements IWeChatAuthentication {

    private final WeChatProperties weChatProperties;

    public WeChatAuthenticationImpl(WeChatProperties weChatProperties) {
        this.weChatProperties = weChatProperties;
    }
    @Override
    public Mono<WeChatUserInfo> authenticate(LoginProcessor loginProcessor, String weChatCode, WeChatUserInfo weChatUserInfo, ServerWebExchange exchange) {
        WeChatAuthenticationToken weChatAuthenticationToken = new WeChatAuthenticationToken(weChatCode);
        WeChatReactiveAuthenticationManager weChatReactiveAuthenticationManager =
                new WeChatReactiveAuthenticationManager(loginProcessor, weChatProperties, weChatUserInfo, exchange);
        return weChatReactiveAuthenticationManager
                .authenticate(weChatAuthenticationToken)
                .map(Authentication::getPrincipal)
                .cast(WeChatUserInfo.class);
    }
}
