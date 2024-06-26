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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.zk.spring.cloud.gateway.security.dao.RolePermissionMapper;
import com.github.zk.spring.cloud.gateway.security.pojo.PermissionInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.RoleInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.RolePermission;
import com.github.zk.spring.cloud.gateway.security.service.IRolePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色权限关联实现
 *
 * @author zk
 * @since 4.0
 */
@Service
public class RolePermissionImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements IRolePermission {
    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public boolean delRolePermissionByPermissionId(Long permissionId) {
        QueryWrapper<RolePermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("permission_id", permissionId);
        return remove(queryWrapper);
    }

    @Override
    public int delRolePermissionByPermissionIds(List<Long> permissionIds) {
        return rolePermissionMapper.delRolePermissionByPermissions(permissionIds);
    }

    @Override
    public boolean delRolePermissionByRoleId(Long roleId) {
        QueryWrapper<RolePermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        return remove(queryWrapper);
    }

    @Override
    public boolean addBatchPermissionRoles(long permissionId, List<RoleInfo> roleInfos) {
        List<RolePermission> rolePermissions = new ArrayList<>();
        roleInfos.forEach(roleInfo -> {
            // 构建角色和权限关联关系，用于批量插入
            RolePermission rp = new RolePermission();
            rp.setRoleId(roleInfo.getId());
            rp.setPermissionId(permissionId);
            rolePermissions.add(rp);
        });
        return saveBatch(rolePermissions);
    }

    @Override
    public boolean addBatchRolePermissions(long roleId, List<PermissionInfo> permissionInfos) {
        List<RolePermission> rolePermissions = new ArrayList<>();
        permissionInfos.forEach(permissionInfo -> {
            // 构建角色和权限关联关系，用于批量插入
            RolePermission rp = new RolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(permissionInfo.getId());
            rolePermissions.add(rp);
        });
        return saveBatch(rolePermissions);
    }
}
