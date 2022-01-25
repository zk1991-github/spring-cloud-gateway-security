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

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.zk.spring.cloud.gateway.security.dao.PermissionMapper;
import com.github.zk.spring.cloud.gateway.security.dao.UserMapper;
import com.github.zk.spring.cloud.gateway.security.pojo.UserInfo;
import com.github.zk.spring.cloud.gateway.security.property.LoginProperties;

/**
 * 用户默认实现
 *
 * @author zk
 * @date 2022/1/19 16:45
 */
public class DefaultUserImpl extends AbstractUserImpl {

    private final UserMapper userMapper;

    public DefaultUserImpl(LoginProperties properties, UserMapper userMapper, PermissionMapper permissionMapper) {
        super(properties, userMapper, permissionMapper);
        this.userMapper = userMapper;
    }


    @Override
    public boolean lockUser(String username) {
        UserInfo userInfo = new UserInfo();
        userInfo.setAccountNonLocked(false);
        UpdateWrapper<UserInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("username", username);
        int update = userMapper.update(userInfo, updateWrapper);
        return update > 0;
    }
}
