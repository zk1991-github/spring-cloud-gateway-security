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

import com.github.zk.spring.cloud.gateway.security.dao.UserMapper;
import com.github.zk.spring.cloud.gateway.security.pojo.UserInfo;
import com.github.zk.spring.cloud.gateway.security.property.LoginProperties;
import com.github.zk.spring.cloud.gateway.security.service.IUser;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;

/**
 * 用户接口实现
 *
 * @author zk
 * @since 3.6
 */
public abstract class AbstractUserImpl implements IUser, ReactiveUserDetailsService {

    /**
     * 定义登录配置
     */
    private final LoginProperties properties;
    /**
     * 用户 Mapper
     */
    private final UserMapper userMapper;

    public AbstractUserImpl(LoginProperties properties, UserMapper userMapper) {
        this.properties = properties;
        this.userMapper = userMapper;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        UserInfo userInfo = null;
        // 如果是配置的超级管理员，直接返回用户信息，不用从数据库中查找
        if (ObjectUtils.nullSafeEquals(properties.getUser().getUsername(), username)) {
            //超级管理员
            userInfo = properties.getUser();
            return Mono.just(userInfo);
        }
        // 从数据库中查找用户信息
        userInfo = customFindByUsername(username);
        if (userInfo == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return Mono.justOrEmpty(userInfo);
    }

    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return 用户对象
     */
    @Override
    public UserInfo customFindByUsername(String username) {
        UserInfo user = new UserInfo();
        user.setUsername(username);
        return userMapper.selectUser(user);
    }
}

