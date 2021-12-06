package com.github.zk.spring.cloud.gateway.security.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zk.spring.cloud.gateway.security.property.WeChatProperties;
import com.github.zk.spring.cloud.gateway.security.core.LoginProcessor;
import com.github.zk.spring.cloud.gateway.security.core.WeChatUserDetails;
import com.github.zk.spring.cloud.gateway.security.pojo.WeChatDO;
import com.github.zk.spring.cloud.gateway.security.pojo.WeChatUserInfo;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 微信登录认证管理器
 *
 * @author zk
 * @date 2021/11/15 10:35
 */
public class WeChatReactiveAuthenticationManager implements ReactiveAuthenticationManager {
    /**
     * rest请求模板
     */
    private final WebClient webClient = WebClient.builder().build();
    /**
     *
     */
    private final LoginProcessor loginProcessor;
    /**
     * 微信认证url
     */
    private final WeChatProperties weChatProperties;
    /**
     * 微信用户详情
     */
    private final WeChatUserDetails weChatUserDetails;

    private final ServerWebExchange exchange;

    private ServerSecurityContextRepository securityContextRepository = new WebSessionServerSecurityContextRepository();

    public WeChatReactiveAuthenticationManager(LoginProcessor loginProcessor, WeChatProperties weChatProperties,
                                               WeChatUserDetails weChatUserDetails,
                                               ServerWebExchange exchange) {
        this.loginProcessor = loginProcessor;
        this.weChatProperties = weChatProperties;
        this.weChatUserDetails = weChatUserDetails;
        this.exchange = exchange;
    }

    public void setSecurityContextRepository(ServerSecurityContextRepository securityContextRepository) {
        this.securityContextRepository = securityContextRepository;
    }


    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        // 登录认证
        String weChatCode = (String) authentication.getPrincipal();

        return weChatRequest(weChatCode)
                .filter(weChatResult ->
                        weChatResult.getErrcode() == null || weChatResult.getErrcode().equals(0))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BadCredentialsException("微信认证失败!"))))
                .flatMap(weChatResult -> {
                    String openId = weChatResult.getOpenid();
                    return loginProcessor
                            .sessionLimitProcess(openId)
                            .filter(isAllow -> isAllow)
                            .switchIfEmpty(Mono.defer(() -> Mono.error(new SessionAuthenticationException("超过最大登录人数！"))))
                            .then(Mono.defer(() -> {
                                WeChatUserInfo weChatUserInfo = (WeChatUserInfo) weChatUserDetails;
                                weChatUserInfo.setOpenid(openId);
                                return Mono.just(createWeChatAuthenticationToken(weChatUserInfo));
                            }));
                })
                .doOnNext(weChatAuthenticationToken -> {
                    SecurityContextImpl securityContext = new SecurityContextImpl();
                    securityContext.setAuthentication(weChatAuthenticationToken);
                    this.securityContextRepository.save(exchange, securityContext)
                            .then(Mono.defer(() -> Mono.just(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext))))).subscribe();
                });
    }

    private Mono<WeChatDO> weChatRequest(String weChatCode) {
        String formatUrl = String.format(weChatProperties.getUrl(),
                weChatProperties.getAppid(), weChatProperties.getAppsecret(), weChatCode);
        return webClient
                .get()
                .uri(formatUrl)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(String.class))
                .map(s -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        return objectMapper.readValue(s, WeChatDO.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return new WeChatDO();
                })
                .onErrorResume(Exception.class, (ex) -> {
                    System.out.println(ex.getMessage());
                    return Mono.empty();
                });
    }

    /**
     * 创建认证令牌
     *
     * @param weChatUserDetails 微信用户详情
     * @return
     */
    private Authentication createWeChatAuthenticationToken(WeChatUserDetails weChatUserDetails) {
        return new WeChatAuthenticationToken(weChatUserDetails, weChatUserDetails.getAuthorities());
    }
}
