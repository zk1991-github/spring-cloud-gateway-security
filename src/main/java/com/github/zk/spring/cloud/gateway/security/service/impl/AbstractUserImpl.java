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

package com.github.zk.spring.cloud.gateway.security.service.impl;

import com.github.zk.spring.cloud.gateway.security.dao.PermissionMapper;
import com.github.zk.spring.cloud.gateway.security.dao.UserMapper;
import com.github.zk.spring.cloud.gateway.security.pojo.PermissionInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.UserInfo;
import com.github.zk.spring.cloud.gateway.security.property.LoginProperties;
import com.github.zk.spring.cloud.gateway.security.service.IUser;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;

/**
 * 用户接口实现
 *
 * @author zk
 * @date 2021/1/15 14:36
 */
public abstract class AbstractUserImpl implements IUser, ReactiveUserDetailsService {

    private final LoginProperties properties;
    private final UserMapper userMapper;
    private final PermissionMapper permissionMapper;

    public AbstractUserImpl(LoginProperties properties, UserMapper userMapper, PermissionMapper permissionMapper) {
        this.properties = properties;
        this.userMapper = userMapper;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        UserInfo userInfo = null;
        if (ObjectUtils.nullSafeEquals(properties.getUser().getUsername(), username)) {
            //超级管理员
            userInfo = properties.getUser();
            userInfo.getRoles().get(0).setPermissionInfos(findAllPermissions());

            return Mono.just(userInfo);
        }
        userInfo = customFindByUsername(username);
        if (userInfo == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return Mono.justOrEmpty(userInfo);
    }

    /**
     * 查询所有权限
     * @return 权限列表
     */
    @Override
    public List<PermissionInfo> findAllPermissions() {
        List<PermissionInfo> permissionInfos = permissionMapper.selectList(null);
        return permissionInfosProcess(0, permissionInfos);
    }

    /**
     * 权限处理 （递归组织结构）
     * @param pid 父id
     * @param permissionInfos 权限信息
     * @return 权限列表
     */
    private List<PermissionInfo> permissionInfosProcess(long pid, List<PermissionInfo> permissionInfos) {
        List<PermissionInfo> newPermissionInfos = new ArrayList<>();
        Iterator<PermissionInfo> iterator = permissionInfos.iterator();
        while (iterator.hasNext()) {
            PermissionInfo permissionInfo = iterator.next();
            if (pid == permissionInfo.getPid()) {
                // 添加父权限
                newPermissionInfos.add(permissionInfo);
                // 剔出本对象
                iterator.remove();
                // 查找子权限
                List<PermissionInfo> childPermissionInfos =
                        permissionInfosProcess(permissionInfo.getId(), new ArrayList<>(permissionInfos));
                // 设置子权限
                permissionInfo.setPermissionInfos(childPermissionInfos);
            }
        }
        return newPermissionInfos;
    }

    /**
     * 子类可覆盖
     * @param username 用户名
     * @return 用户对象
     */
    protected UserInfo customFindByUsername(String username) {
        UserInfo user = new UserInfo();
        user.setUsername(username);
        return userMapper.selectUser(user);
    }
}

