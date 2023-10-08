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

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.zk.spring.cloud.gateway.security.pojo.PermissionInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.RoleInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.RolePermission;
import java.util.List;

/**
 * 角色权限关联接口
 *
 * @author zk
 * @date 2022/2/15 14:54
 */
public interface IRolePermission extends IService<RolePermission> {
    /**
     * 根据权限id，删除角色权限关系
     * @param permissionId 权限id
     * @return 删除状态
     */
    boolean delRolePermissionByPermissionId(Long permissionId);

    /**
     * 根据权限id，批量删除角色权限关系
     * @param permissionIds 权限id
     * @return 删除状态
     */
    int delRolePermissionByPermissionIds(List<Long> permissionIds);

    /**
     * 根据角色id，删除角色权限关系
     * @param roleId 角色id
     * @return 删除状态
     */
    boolean delRolePermissionByRoleId(Long roleId);

    /**
     * 根据权限绑定角色
     * @param permissionId 权限id
     * @param roleInfos 角色列表
     * @return 绑定状态
     */
    boolean addBatchPermissionRoles(long permissionId, List<RoleInfo> roleInfos);

    /**
     * 根据角色绑定权限
     * @param roleId 角色id
     * @param permissionInfos 权限列表
     * @return 绑定状态
     */
    boolean addBatchRolePermissions(long roleId, List<PermissionInfo> permissionInfos);
}
