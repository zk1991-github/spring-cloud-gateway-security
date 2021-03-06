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
 * ?????????????????????
 * ???????????????????????????????????????
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
                .flatMap(auth -> {
                    ServerWebExchange exchange = authorizationContext.getExchange();
                    String realRequestPath = getRealRequestPath(exchange);
                    return openPermissionMatch(auth, realRequestPath);
                })
                .map(authenticationHolder -> {
                    if (authenticationHolder.getAuthorizationDecision().isGranted()) {
                        return new AuthorizationDecision(true);
                    }
                    ServerWebExchange exchange = authorizationContext.getExchange();
                    String realRequestPath = getRealRequestPath(exchange);
                    logger.info("???????????????{}", realRequestPath);
//            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
//            for (GrantedAuthority authority : authorities) {
//                String authorityAuthority = authority.getAuthority();
//                String path = request.getURI().getPath();
//                boolean match = ANT_PATH_MATCHER.match(authorityAuthority, path);
//                if (match) {
//                    return new AuthorizationDecision(true);
//                }
//            }
                    Object principal = authenticationHolder.getAuthentication().getPrincipal();
                    if (principal instanceof UserInfo) {
                        UserInfo userInfo = (UserInfo) principal;
                        return userInfoAuthorization(userInfo, exchange, realRequestPath);
                    } else if (principal instanceof WeChatUserInfo) {
                        WeChatUserInfo weChatUserInfo = (WeChatUserInfo) principal;
                        return weChatUserInfoAuthorization(weChatUserInfo, exchange, realRequestPath);
                    } else {
                        return new AuthorizationDecision(false);
                    }
                })
                .defaultIfEmpty(new AuthorizationDecision(false));
    }

    /**
     * ??????????????????
     *
     * @param requestPath ????????????
     * @return ??????????????????
     */
    private Mono<AuthenticationHolder> openPermissionMatch(Authentication authentication, String requestPath) {
        return iPermission.getCacheOpenPermission().map(permissionInfos -> {
            AuthenticationHolder authenticationHolder = new AuthenticationHolder();
            authenticationHolder.setAuthentication(authentication);
            for (PermissionInfo permissionInfo : permissionInfos) {
                boolean matchUrl = ANT_PATH_MATCHER.match(permissionInfo.getUrl(), requestPath);
                if (matchUrl) {
                    logger.info("???????????????{}", requestPath);
                    authenticationHolder.setAuthorizationDecision(new AuthorizationDecision(true));
                    return authenticationHolder;
                }
            }
            authenticationHolder.setAuthorizationDecision(new AuthorizationDecision(false));
            return authenticationHolder;
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
     * web??????
     *
     * @param userInfo        ????????????
     * @param exchange        web??????
     * @param requestPath     ????????????
     * @return ????????????
     */
    private AuthorizationDecision userInfoAuthorization(UserInfo userInfo, ServerWebExchange exchange,
                                                        String requestPath) {

        logger.debug("????????????{}???,?????????{}???", userInfo.getUsername(), userInfo.getRoles());
        //?????????url????????????
        for (RoleInfo role : userInfo.getRoles()) {
            for (PermissionInfo permissionInfo : role.getPermissionInfos()) {
                boolean match = ANT_PATH_MATCHER.match(permissionInfo.getUrl(), requestPath);
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
     * ????????????
     *
     * @param weChatUserInfo  ??????????????????
     * @param exchange        ??????
     * @param requestPath     ????????????
     * @return ????????????
     */
    private AuthorizationDecision weChatUserInfoAuthorization(WeChatUserInfo weChatUserInfo, ServerWebExchange exchange,
                                                              String requestPath) {
        String encodedNickName = "";
        try {
            //????????????????????????????????????
            encodedNickName = URLEncoder.encode(weChatUserInfo.getNickName(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        logger.debug("?????????{}???,?????????{}???", weChatUserInfo.getNickName(), weChatUserInfo.getRoles());
        //?????????url????????????
        for (RoleInfo role : weChatUserInfo.getRoles()) {
            for (PermissionInfo permissionInfo : role.getPermissionInfos()) {
                boolean match = ANT_PATH_MATCHER.match(permissionInfo.getUrl(), requestPath);
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

    private String getRealRequestPath(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        //?????????uri
        String requestPath = request.getURI().getPath();
        //??????????????????
        StringBuilder realRequestPath = new StringBuilder();
        outer:
        for (RouteDefinition route : gatewayProperties.getRoutes()) {
            for (PredicateDefinition predicate : route.getPredicates()) {
                //??????Path??????
                if (ObjectUtils.nullSafeEquals(predicate.getName(), "Path")) {
                    String matcher = predicate.getArgs().get("matcher");
                    if (ObjectUtils.isEmpty(matcher)) {
                        continue;
                    }
                    if (ANT_PATH_MATCHER.match(matcher, requestPath)) {
                        //??????StripPrefix??????
                        for (FilterDefinition filter : route.getFilters()) {
                            if (ObjectUtils.nullSafeEquals(filter.getName(), "StripPrefix")) {
                                String skip = filter.getArgs().get("parts");
                                String[] paths = requestPath.split("/");
                                for (int i = 0; i < paths.length; i++) {
                                    if (i > Integer.parseInt(skip)) {
                                        realRequestPath.append("/").append(paths[i]);
                                    }
                                }
                                //??????????????????????????????????????????
                                break outer;
                            }
                        }
                    }
                }
            }
        }
        realRequestPath = new StringBuilder((realRequestPath.length() == 0) ? requestPath : realRequestPath.toString());
        return realRequestPath.toString();
    }
}