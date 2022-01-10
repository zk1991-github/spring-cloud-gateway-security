package com.github.zk.spring.cloud.gateway.security.service.impl;

import com.github.zk.spring.cloud.gateway.security.authentication.WeChatAuthenticationToken;
import com.github.zk.spring.cloud.gateway.security.authentication.WeChatReactiveAuthenticationManager;
import com.github.zk.spring.cloud.gateway.security.core.LoginProcessor;
import com.github.zk.spring.cloud.gateway.security.dao.RoleMapper;
import com.github.zk.spring.cloud.gateway.security.pojo.RoleInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.WeChatUserInfo;
import com.github.zk.spring.cloud.gateway.security.property.WeChatProperties;
import com.github.zk.spring.cloud.gateway.security.service.IWeChatAuthentication;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private RoleMapper roleMapper;

    private final WeChatProperties weChatProperties;

    public WeChatAuthenticationImpl(WeChatProperties weChatProperties) {
        this.weChatProperties = weChatProperties;
    }
    @Override
    public Mono<WeChatUserInfo> authenticate(LoginProcessor loginProcessor, String weChatCode,
                                             WeChatUserInfo weChatUserInfo, ServerWebExchange exchange) {
        // 实例化待验证token
        WeChatAuthenticationToken weChatAuthenticationToken = new WeChatAuthenticationToken(weChatCode);
        // 查询用户角色
        List<RoleInfo> roles = findByRoleId(weChatProperties.getRoleIds());
        weChatUserInfo.setRoles(roles);
        // 实例化认证管理器
        WeChatReactiveAuthenticationManager weChatReactiveAuthenticationManager =
                new WeChatReactiveAuthenticationManager(loginProcessor, weChatProperties, weChatUserInfo, exchange);
        return weChatReactiveAuthenticationManager
                .authenticate(weChatAuthenticationToken)
                .map(Authentication::getPrincipal)
                .cast(WeChatUserInfo.class);
    }

    /**
     * 根据角色id，查询角色列表
     * @param roleIds 角色id
     * @return 角色列表
     */
    private List<RoleInfo> findByRoleId(Long[] roleIds) {
        return roleMapper.selectRolesByIds(roleIds);
    }

}
