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

package com.github.zk.spring.cloud.gateway.security.service;

import com.github.zk.spring.cloud.gateway.security.pojo.GroupPermissionDTO;
import com.github.zk.spring.cloud.gateway.security.pojo.PermissionGroup;

import java.util.List;

/**
 * 权限分组接口
 *
 * @author zhaokai
 * @since 4.3.6
 */
public interface IGroupPermission {

    /**
     * 移动分组
     *
     * @param groupPermissionDTO 权限对象
     * @return 是否成功状态
     */
    boolean movePermission(GroupPermissionDTO groupPermissionDTO);

    /**
     * 添加权限组
     *
     * @param groupPermission 分组权限对象
     * @return 分组 id
     */
    Long addGroupPermission(PermissionGroup groupPermission);

    /**
     * 根据权限id，批量修改权限组
     *
     * @param permissionIds 权限 id 列表
     * @param groupId 组 id
     * @return 是否修改成功
     */
    boolean updatePermissionGroupByPermissionIds(List<Long> permissionIds, Long groupId);
}
