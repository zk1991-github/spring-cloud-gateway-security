/*
 *
 *  * Copyright 2021-2023 the original author or authors.
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

package com.github.zk.spring.cloud.gateway.security.service;

import com.github.zk.spring.cloud.gateway.security.pojo.UserInfo;
import java.time.Duration;

/**
 * 用户接口
 *
 * @author zk
 * @date 2021/1/15 14:35
 */
public interface IUser {

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    UserInfo customFindByUsername(String username);
    /**
     * 锁定用户
     * @param username 用户名
     * @return 是否锁定成功
     */
    boolean lockUser(String username);

    /**
     * 解锁用户
     *
     * @param username 用户名
     * @param time 解锁时间
     * @return 是否解锁成功
     */
    void unLockUser(String username, Duration time);

    /**
     * 修改密码
     * @param username 用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改状态
     */
    boolean updatePassword(String username, String oldPassword, String newPassword);

}
