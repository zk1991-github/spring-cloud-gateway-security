package com.github.zk.spring.cloud.gateway.security.handler;

import com.github.zk.spring.cloud.gateway.security.pojo.PermissionInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.RoleInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.UserInfo;
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

    private Logger logger = LoggerFactory.getLogger(CustomReactiveAuthorizationManager.class);

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    private GatewayProperties gatewayProperties;

    public CustomReactiveAuthorizationManager(GatewayProperties gatewayProperties) {
        this.gatewayProperties = gatewayProperties;
    }

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
        return authentication.map(auth -> {
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

//            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
//            for (GrantedAuthority authority : authorities) {
//                String authorityAuthority = authority.getAuthority();
//                String path = request.getURI().getPath();
//                boolean match = ANT_PATH_MATCHER.match(authorityAuthority, path);
//                if (match) {
//                    return new AuthorizationDecision(true);
//                }
//            }
            UserInfo userInfo = (UserInfo) auth.getPrincipal();
            System.out.printf("用户名【%s】,角色【%s】\n", userInfo.getUsername(), userInfo.getRoles());
            //角色的url权限过滤
            for (RoleInfo role : userInfo.getRoles()) {
                for (PermissionInfo permissionInfo : role.getPermissionInfos()) {
                    boolean match = ANT_PATH_MATCHER.match(permissionInfo.getUrl(),
                            realRequestPath.isEmpty() ? requestPath : realRequestPath);
                    if (match) {
                        ServerHttpRequest newRequest = request.mutate()
                                .header("username", userInfo.getUsername())
                                .header("userId", String.valueOf(userInfo.getId()))
                                .build();
                        exchange.mutate().request(newRequest).build();
                        return new AuthorizationDecision(true);
                    }
                }
            }
            return new AuthorizationDecision(false);
        }).defaultIfEmpty(new AuthorizationDecision(false));
    }

    @Override
    public Mono<Void> verify(Mono<Authentication> authentication, AuthorizationContext object) {
        return check(authentication, object)
                .filter(AuthorizationDecision::isGranted)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new AccessDeniedException("Access Denied"))))
                .flatMap((decision) -> Mono.empty());
    }
}
