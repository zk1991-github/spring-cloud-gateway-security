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

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.zk.spring.cloud.gateway.security.dao.UserMapper;
import com.github.zk.spring.cloud.gateway.security.pojo.UserInfo;
import com.github.zk.spring.cloud.gateway.security.property.LoginProperties;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 用户默认实现
 *
 * @author zk
 * @since 1.1
 */
public class DefaultUserImpl extends AbstractUserImpl {

    private final UserMapper userMapper;

    /**
     * 实例化密码编码器
     */
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public DefaultUserImpl(LoginProperties properties, UserMapper userMapper) {
        super(properties, userMapper);
        this.userMapper = userMapper;
    }


    @Override
    public boolean lockUser(String username) {
        UserInfo userInfo = new UserInfo();
        userInfo.setAccountNonLocked(false);
        UpdateWrapper<UserInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("username", username);
        // 根据用户名锁定用户
        int update = userMapper.update(userInfo, updateWrapper);
        return update > 0;
    }

    @Override
    public void unLockUser(String username, Duration time) {
        ScheduledThreadPoolExecutor executor =
                new ScheduledThreadPoolExecutor(1, new DefaultThreadFactory(DefaultUserImpl.class));
        // 延迟执行任务
        executor.schedule(() -> {
            UserInfo userInfo = new UserInfo();
            userInfo.setAccountNonLocked(true);
            UpdateWrapper<UserInfo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("username", username);
            // 根据用户名锁定用户
            userMapper.update(userInfo, updateWrapper);
            // 关闭线程池
            executor.shutdown();
        }, time.getSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public boolean updatePassword(String username, String oldPassword, String newPassword) {
        UserInfo userInfo = customFindByUsername(username);
        if (userInfo == null) {
            throw new UsernameNotFoundException("用户不存在!");
        }
        // 旧密码匹配
        boolean matches = passwordEncoder.matches(oldPassword, userInfo.getPassword());
        if (matches) {
            String encodedPassword = passwordEncoder.encode(newPassword);
            UpdateWrapper<UserInfo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("password", encodedPassword);
            updateWrapper.eq("username", username);
            int update = userMapper.update(null, updateWrapper);
            return update > 0;

        }
        return false;
    }
}
