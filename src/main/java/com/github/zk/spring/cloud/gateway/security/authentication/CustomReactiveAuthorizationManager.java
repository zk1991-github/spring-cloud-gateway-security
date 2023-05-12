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

    /**
     * 实例化 url 匹配器
     */
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    /**
     * 定义网关配置
     */
    private final GatewayProperties gatewayProperties;

    /**
     * 定义权限接口
     */
    private final IPermission iPermission;

    public CustomReactiveAuthorizationManager(GatewayProperties gatewayProperties, IPermission iPermission) {
        this.gatewayProperties = gatewayProperties;
        this.iPermission = iPermission;
    }

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
        return authentication
                .flatMap(auth -> {
                    // 所有角色的公开权限匹配
                    ServerWebExchange exchange = authorizationContext.getExchange();
                    String realRequestPath = getRealRequestPath(exchange);
                    return openPermissionMatch(auth, realRequestPath);
                })
                .map(authenticationHolder -> {
                    // 公开权限认证通过，直接返回认证通过
                    if (authenticationHolder.getAuthorizationDecision().isGranted()) {
                        return new AuthorizationDecision(true);
                    }
                    // 非公开权限验证
                    ServerWebExchange exchange = authorizationContext.getExchange();
                    String realRequestPath = getRealRequestPath(exchange);
                    logger.info("转发地址：{}", realRequestPath);
//            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
//            for (GrantedAuthority authority : authorities) {
//                String authorityAuthority = authority.getAuthority();
//                String path = request.getURI().getPath();
//                boolean match = ANT_PATH_MATCHER.match(authorityAuthority, path);
//                if (match) {
//                    return new AuthorizationDecision(true);
//                }
//            }
                    // 获取用户信息
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
                // 未登录时默认无权限
                .defaultIfEmpty(new AuthorizationDecision(false));
    }

    /**
     * 公开权限匹配
     *
     * @param requestPath 请求地址
     * @return 权限认证结果
     */
    private Mono<AuthenticationHolder> openPermissionMatch(Authentication authentication, String requestPath) {
        return iPermission.getCacheOpenPermission().map(permissionInfos -> {
            // 实例化认证持有对象
            AuthenticationHolder authenticationHolder = new AuthenticationHolder();
            // 存储认证后的信息（用户对象等）
            authenticationHolder.setAuthentication(authentication);
            // 请求地址匹配公开权限
            for (PermissionInfo permissionInfo : permissionInfos) {
                boolean matchUrl = ANT_PATH_MATCHER.match(permissionInfo.getUrl(), requestPath);
                if (matchUrl) {
                    logger.info("转发地址：{}", requestPath);
                    authenticationHolder.setAuthorizationDecision(new AuthorizationDecision(true));
                    return authenticationHolder;
                }
            }
            // 请求地址非公开权限，设置无公开权限状态
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
     * web鉴权
     *
     * @param userInfo    用户信息
     * @param exchange    web请求
     * @param requestPath 请求地址
     * @return 权限信息
     */
    private AuthorizationDecision userInfoAuthorization(UserInfo userInfo, ServerWebExchange exchange,
                                                        String requestPath) {

        logger.debug("用户名【{}】,角色【{}】", userInfo.getUsername(), userInfo.getRoles());
        //角色的url权限过滤
        for (RoleInfo role : userInfo.getRoles()) {
            // 遍历角色下的权限
            for (PermissionInfo permissionInfo : role.getPermissionInfos()) {
                // 请求地址匹配权限地址
                boolean match = ANT_PATH_MATCHER.match(permissionInfo.getUrl(), requestPath);
                if (match) {
                    // 设置下游服务传递的头信息
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
     * @param weChatUserInfo 微信用户信息
     * @param exchange       请求
     * @param requestPath    请求地址
     * @return 权限信息
     */
    private AuthorizationDecision weChatUserInfoAuthorization(WeChatUserInfo weChatUserInfo, ServerWebExchange exchange,
                                                              String requestPath) {
        // 定义编码昵称
        String encodedNickName = "";
        try {
            //为处理中文昵称，需要编码
            encodedNickName = URLEncoder.encode(weChatUserInfo.getNickName(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        logger.debug("昵称【{}】,角色【{}】", weChatUserInfo.getNickName(), weChatUserInfo.getRoles());
        //角色的url权限过滤
        for (RoleInfo role : weChatUserInfo.getRoles()) {
            // 遍历角色下的权限
            for (PermissionInfo permissionInfo : role.getPermissionInfos()) {
                // 请求地址匹配权限地址
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

    /**
     * 获取真实请求地址
     *
     * @param exchange 请求对象
     * @return 真实的请求地址
     */
    private String getRealRequestPath(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        // 请求的uri
        String requestPath = request.getURI().getPath();
        // 实例化真正请求地址字符串对象
        StringBuilder realRequestPath = new StringBuilder();
        outer:
        // 遍历网关配置的路由
        for (RouteDefinition route : gatewayProperties.getRoutes()) {
            // 遍历路由的断言
            for (PredicateDefinition predicate : route.getPredicates()) {
                // 查找Path配置
                if (ObjectUtils.nullSafeEquals(predicate.getName(), "Path")) {
                    // 获取 matcher 参数值
                    String matcher = predicate.getArgs().get("matcher");
                    // 判断 matcher 为空，继续遍历
                    if (ObjectUtils.isEmpty(matcher)) {
                        continue;
                    }
                    // 请求匹配 matcher 地址
                    if (ANT_PATH_MATCHER.match(matcher, requestPath)) {
                        //查找StripPrefix配置， 查看跳过几级路径
                        for (FilterDefinition filter : route.getFilters()) {
                            if (ObjectUtils.nullSafeEquals(filter.getName(), "StripPrefix")) {
                                // 获取跳过路径值
                                String skip = filter.getArgs().get("parts");
                                String[] paths = requestPath.split("/");
                                for (int i = 0; i < paths.length; i++) {
                                    // 只保留跳过后的路径为真实路径
                                    if (i > Integer.parseInt(skip)) {
                                        realRequestPath.append("/").append(paths[i]);
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
        // 当未跳过路径时，请求地址为转发地址
        realRequestPath = new StringBuilder((realRequestPath.length() == 0) ? requestPath : realRequestPath.toString());
        return realRequestPath.toString();
    }
}