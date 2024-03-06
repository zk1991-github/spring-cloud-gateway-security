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

import com.github.zk.spring.cloud.gateway.security.dao.RoleMapper;
import com.github.zk.spring.cloud.gateway.security.enums.IntfTypeEnum;
import com.github.zk.spring.cloud.gateway.security.pojo.PermissionInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.RoleInfo;
import com.github.zk.spring.cloud.gateway.security.service.IRole;
import com.github.zk.spring.cloud.gateway.security.service.IRolePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 角色实现类
 *
 * @author zk
 * @since 4.0
 */
@Service
public class RoleImpl implements IRole {
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private IRolePermission iRolePermission;

    @Override
    public List<RoleInfo> queryAllRoles() {
        return roleMapper.selectList(null);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean bindPermissionsByRole(RoleInfo roleInfo) {
        Long roleId = roleInfo.getId();
        // 获取角色需要绑定的权限列表信息
        List<PermissionInfo> permissionInfos = roleInfo.getPermissionInfos();
        // 根据角色，删除权限
        boolean del = iRolePermission.delRolePermissionByRoleId(roleId);
        if (!ObjectUtils.isEmpty(permissionInfos)) {
            // 批量添加角色的权限信息
            return iRolePermission.addBatchRolePermissions(roleId, permissionInfos);
        }
        return del;
    }

    @Override
    public List<PermissionInfo> queryPermissionsByRoleId(Long roleId) {
        return roleMapper.selectPermissionsByRoleId(roleId, IntfTypeEnum.PRIVATE_PERMISSION.getIndex());
    }
}
