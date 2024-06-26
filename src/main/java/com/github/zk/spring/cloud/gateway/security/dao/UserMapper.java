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

package com.github.zk.spring.cloud.gateway.security.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.zk.spring.cloud.gateway.security.pojo.UserInfo;
import org.springframework.stereotype.Repository;

/**
 * 用户仓库
 *
 * @author zk
 * @since 1.0
 */
@Repository
public interface UserMapper extends BaseMapper<UserInfo> {

    /**
     * 根据条件，查询用户
     * @param userInfo 用户信息
     * @return 用户信息
     */
    UserInfo selectUser(UserInfo userInfo);
}
