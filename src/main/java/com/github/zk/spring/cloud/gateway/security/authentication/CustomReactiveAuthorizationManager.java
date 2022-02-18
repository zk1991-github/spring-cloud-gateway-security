/*
 *
 *  * Copyright 2021-2022 the original author or authors.
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

package com.github.zk.spring.cloud.gateway.security.authentication;

import com.github.zk.spring.cloud.gateway.security.pojo.PermissionInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.RoleInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.UserInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.WeChatUserInfo;
import com.github.zk.spring.cloud.gateway.security.service.IPermission;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 授权管理处理器
 * 处理每一个请求是否具备权限
 *
 * @author zk
 * @date 2021/7/13 14:38
 */
public class CustomReactiveAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final Logger logger = LoggerFactory.getLogger(CustomReactiveAuthorizationManager.class);

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    private final GatewayProperties gatewayProperties;

    private final IPermission iPermission;

    public CustomReactiveAuthorizationManager(GatewayProperties gatewayProperties, IPermission iPermission) {
        this.gatewayProperties = gatewayProperties;
        this.iPermission = iPermission;
    }

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
        return authentication
                .map(auth -> {
                    ServerWebExchange exchange = authorizationContext.getExchange();
                    ServerHttpRequest request = exchange.getRequest();
                    //请求的uri
                    String requestPath = request.getURI().getPath();
                    logger.info("访问地址：{}", requestPath);
                    //真正请求地址
                    String realRequestPath = "";
                    outer:
                    for (RouteDefinition route : gatewayProperties.getRoutes()) {
                        for (PredicateDefinition predicate : route.getPredicates()) {
                            //查找Path配置
                            if (ObjectUtils.nullSafeEquals(predicate.getName(), "Path")) {
                                String matcher = predicate.getArgs().get("matcher");
                                if (ObjectUtils.isEmpty(matcher)) {
                                    continue;
                                }
                                if (ANT_PATH_MATCHER.match(matcher, requestPath)) {
                                    //查找StripPrefix配置
                                    for (FilterDefinition filter : route.getFilters()) {
                                        if (ObjectUtils.nullSafeEquals(filter.getName(), "StripPrefix")) {
                                            String skip = filter.getArgs().get("parts");
                                            String[] paths = requestPath.split("/");
                                            for (int i = 0; i < paths.length; i++) {
                                                if (i > Integer.parseInt(skip)) {
                                                    realRequestPath += "/" + paths[i];
                                                }
                                            }
                                            //找到相应参数后，跳出到最外层
                                            break outer;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    logger.info("转发地址：{}", realRequestPath.isEmpty() ? requestPath : realRequestPath);
//            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
//            for (GrantedAuthority authority : authorities) {
//                String authorityAuthority = authority.getAuthority();
//                String path = request.getURI().getPath();
//                boolean match = ANT_PATH_MATCHER.match(authorityAuthority, path);
//                if (match) {
//                    return new AuthorizationDecision(true);
//                }
//            }
                    Object principal = auth.getPrincipal();
                    if (principal instanceof UserInfo) {
                        UserInfo userInfo = (UserInfo) principal;
                        return userInfoAuthorization(userInfo, exchange, requestPath, realRequestPath);
                    } else if (principal instanceof WeChatUserInfo) {
                        WeChatUserInfo weChatUserInfo = (WeChatUserInfo) principal;
                        return weChatUserInfoAuthorization(weChatUserInfo, exchange, requestPath, realRequestPath);
                    } else {
                        return new AuthorizationDecision(false);
                    }
                })
                .flatMap(authorizationDecision -> {
                    if (authorizationDecision.isGranted()) {
                        return Mono.just(new AuthorizationDecision(true));
                    }
                    ServerWebExchange exchange = authorizationContext.getExchange();
                    ServerHttpRequest request = exchange.getRequest();
                    //请求的uri
                    String requestPath = request.getURI().getPath();
                    logger.info("访问地址：{}", requestPath);
                    //真正请求地址
                    String realRequestPath = "";
                    outer:
                    for (RouteDefinition route : gatewayProperties.getRoutes()) {
                        for (PredicateDefinition predicate : route.getPredicates()) {
                            //查找Path配置
                            if (ObjectUtils.nullSafeEquals(predicate.getName(), "Path")) {
                                String matcher = predicate.getArgs().get("matcher");
                                if (ObjectUtils.isEmpty(matcher)) {
                                    continue;
                                }
                                if (ANT_PATH_MATCHER.match(matcher, requestPath)) {
                                    //查找StripPrefix配置
                                    for (FilterDefinition filter : route.getFilters()) {
                                        if (ObjectUtils.nullSafeEquals(filter.getName(), "StripPrefix")) {
                                            String skip = filter.getArgs().get("parts");
                                            String[] paths = requestPath.split("/");
                                            for (int i = 0; i < paths.length; i++) {
                                                if (i > Integer.parseInt(skip)) {
                                                    realRequestPath += "/" + paths[i];
                                                }
                                            }
                                            //找到相应参数后，跳出到最外层
                                            break outer;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    logger.info("转发地址：{}", realRequestPath.isEmpty() ? requestPath : realRequestPath);
                    return openPermissionMatch(requestPath, realRequestPath);
                })
                .defaultIfEmpty(new AuthorizationDecision(false));
    }

    private Mono<AuthorizationDecision> openPermissionMatch(String requestPath, String realRequestPath) {
        return iPermission.getCacheOpenPermission().map(permissionInfos -> {
            for (PermissionInfo permissionInfo : permissionInfos) {
                boolean matchUrl = ANT_PATH_MATCHER.match(permissionInfo.getUrl(),
                        realRequestPath.isEmpty() ? requestPath : realRequestPath);
                if (matchUrl) {
                    return new AuthorizationDecision(true);
                }
            }
            return new AuthorizationDecision(false);
        });
    }

    @Override
    public Mono<Void> verify(Mono<Authentication> authentication, AuthorizationContext object) {
        return check(authentication, object)
                .filter(AuthorizationDecision::isGranted)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new AccessDeniedException("Access Denied"))))
                .flatMap((decision) -> Mono.empty());
    }

    /**
     * web鉴权
     *
     * @param userInfo        用户信息
     * @param exchange        web请求
     * @param requestPath     请求地址
     * @param realRequestPath 真实请求地址 （去掉转发地址后）
     * @return 权限信息
     */
    private AuthorizationDecision userInfoAuthorization(UserInfo userInfo, ServerWebExchange exchange,
                                                        String requestPath,
                                                        String realRequestPath) {

        logger.info("用户名【{}】,角色【{}】", userInfo.getUsername(), userInfo.getRoles());
        //角色的url权限过滤
        for (RoleInfo role : userInfo.getRoles()) {
            for (PermissionInfo permissionInfo : role.getPermissionInfos()) {
                boolean match = ANT_PATH_MATCHER.match(permissionInfo.getUrl(),
                        realRequestPath.isEmpty() ? requestPath : realRequestPath);
                if (match) {
                    ServerHttpRequest newRequest = exchange.getRequest().mutate()
                            .header("username", userInfo.getUsername())
                            .header("userId", String.valueOf(userInfo.getId()))
                            .build();
                    exchange.mutate().request(newRequest).build();
                    return new AuthorizationDecision(true);
                }
            }
        }
        return new AuthorizationDecision(false);
    }

    /**
     * 微信鉴权
     *
     * @param weChatUserInfo  微信用户信息
     * @param exchange        请求
     * @param requestPath     请求地址
     * @param realRequestPath 真实请求地址
     * @return 权限信息
     */
    private AuthorizationDecision weChatUserInfoAuthorization(WeChatUserInfo weChatUserInfo, ServerWebExchange exchange,
                                                              String requestPath,
                                                              String realRequestPath) {
        String encodedNickName = "";
        try {
            //为处理中文昵称，需要编码
            encodedNickName = URLEncoder.encode(weChatUserInfo.getNickName(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        logger.info("昵称【{}】,角色【{}】", weChatUserInfo.getNickName(), weChatUserInfo.getRoles());
        //角色的url权限过滤
        for (RoleInfo role : weChatUserInfo.getRoles()) {
            for (PermissionInfo permissionInfo : role.getPermissionInfos()) {
                boolean match = ANT_PATH_MATCHER.match(permissionInfo.getUrl(),
                        realRequestPath.isEmpty() ? requestPath : realRequestPath);
                if (match) {
                    ServerHttpRequest newRequest = exchange.getRequest().mutate()
                            .header("username", encodedNickName)
                            .header("userId", String.valueOf(weChatUserInfo.getOpenid()))
                            .build();
                    exchange.mutate().request(newRequest).build();
                    return new AuthorizationDecision(true);
                }
            }
        }
        return new AuthorizationDecision(false);
    }
}