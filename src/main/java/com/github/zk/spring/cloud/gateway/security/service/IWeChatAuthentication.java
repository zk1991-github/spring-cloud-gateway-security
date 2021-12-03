package com.github.zk.spring.cloud.gateway.security.service;

import com.github.zk.spring.cloud.gateway.security.core.LoginProcessor;
import com.github.zk.spring.cloud.gateway.security.pojo.WeChatUserInfo;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 微信认证接口
 *
 * @author zk
 * @date 2021/11/17 11:17
 */
public interface IWeChatAuthentication {
    /**
     * 微信认证
     * @param weChatCode 微信code
     * @param weChatUserInfo 微信用户信息
     * @return
     */
    Mono<WeChatUserInfo> authenticate(LoginProcessor loginProcessor, String weChatCode, WeChatUserInfo weChatUserInfo, ServerWebExchange exchange);
}
