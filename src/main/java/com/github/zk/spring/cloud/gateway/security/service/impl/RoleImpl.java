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

import com.github.zk.spring.cloud.gateway.security.dao.RoleMapper;
import com.github.zk.spring.cloud.gateway.security.enums.IntfTypeEnum;
import com.github.zk.spring.cloud.gateway.security.pojo.PermissionInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.RoleInfo;
import com.github.zk.spring.cloud.gateway.security.service.IRole;
import com.github.zk.spring.cloud.gateway.security.service.IRolePermission;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 角色实现类
 *
 * @author zk
 * @date 2022/2/16 11:25
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
        List<PermissionInfo> permissionInfos = roleInfo.getPermissionInfos();
        boolean del = iRolePermission.delRolePermissionByRoleId(roleId);
        boolean add = iRolePermission.addBatchRolePermissions(roleId, permissionInfos);
        return del && add;
    }

    @Override
    public List<PermissionInfo> queryPermissionsByRoleId(Long roleId) {
        return roleMapper.selectPermissionsByRoleId(roleId, IntfTypeEnum.PRIVATE_PERMISSION.getIndex());
    }
}
