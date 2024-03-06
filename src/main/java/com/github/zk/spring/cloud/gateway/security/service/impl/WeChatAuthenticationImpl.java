/*
 *
 *  * Copyright 2021-2024 the original author or authors.
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

package com.github.zk.spring.cloud.gateway.security.service.impl;

import com.github.zk.spring.cloud.gateway.security.authentication.WeChatAuthenticationToken;
import com.github.zk.spring.cloud.gateway.security.authentication.WeChatReactiveAuthenticationManager;
import com.github.zk.spring.cloud.gateway.security.core.LoginProcessor;
import com.github.zk.spring.cloud.gateway.security.dao.RoleMapper;
import com.github.zk.spring.cloud.gateway.security.pojo.RoleInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.WeChatUserInfo;
import com.github.zk.spring.cloud.gateway.security.property.WeChatProperties;
import com.github.zk.spring.cloud.gateway.security.service.IWeChatAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 微信认证实现
 *
 * @author zk
 * @since 3.0
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
