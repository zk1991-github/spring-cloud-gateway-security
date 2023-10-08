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

import com.github.zk.spring.cloud.gateway.security.pojo.PermissionInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.RoleInfo;
import java.util.List;

/**
 * 角色接口
 *
 * @author zk
 * @date 2022/2/16 9:24
 */
public interface IRole {
    /**
     * 查询所有角色
     * @return 角色列表
     */
    List<RoleInfo> queryAllRoles();

    /**
     * 根据角色绑定权限
     * @param roleInfo 角色信息
     * @return 绑定状态
     */
    boolean bindPermissionsByRole(RoleInfo roleInfo);

    /**
     * 根据角色查询权限
     * @param roleId 角色id
     * @return 权限信息
     */
    List<PermissionInfo> queryPermissionsByRoleId(Long roleId);

}
