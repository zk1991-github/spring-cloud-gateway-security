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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.zk.spring.cloud.gateway.security.dao.PermissionMapper;
import com.github.zk.spring.cloud.gateway.security.pojo.PermissionInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.RoleInfo;
import com.github.zk.spring.cloud.gateway.security.service.IPermission;
import com.github.zk.spring.cloud.gateway.security.service.IRolePermission;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

/**
 * 权限实现类
 *
 * @author zk
 * @date 2022/2/14 11:06
 */
@Service
public class PermissionImpl implements IPermission {

    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private IRolePermission iRolePermission;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addPermission(PermissionInfo permissionInfo) {
        int insert = permissionMapper.insert(permissionInfo);
        Long permissionId = permissionInfo.getId();
        List<RoleInfo> roleInfos = permissionInfo.getRoleInfos();
        //绑定权限
        boolean b = iRolePermission.addBatchPermissionRoles(permissionId, roleInfos);
        if (b) {
            return insert;
        }
        return 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delPermission(long id) {
        int del = permissionMapper.deleteById(id);
        boolean b = iRolePermission.delRolePermissionByPermissionId(id);
        if (b) {
            return del;
        }
        return 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updatePermission(PermissionInfo permissionInfo) {
        int update = permissionMapper.updateById(permissionInfo);
        List<RoleInfo> roleInfos = permissionInfo.getRoleInfos();
        if (roleInfos != null) {
            Long permissionId = permissionInfo.getId();
            //删除原有权限
            boolean del = iRolePermission.delRolePermissionByPermissionId(permissionId);
            // 绑定新权限
            boolean add = iRolePermission.addBatchPermissionRoles(permissionId, roleInfos);
            if (del && add) {
                return update;
            }
            return 0;
        }
        return update;
    }

    @Override
    public Page<PermissionInfo> queryPermission(PermissionInfo permissionInfo) {
        QueryWrapper<PermissionInfo> queryWrapper = new QueryWrapper<>();
        String urlName = permissionInfo.getUrlName();
        String url = permissionInfo.getUrl();
        if (!ObjectUtils.isEmpty(urlName)) {
            queryWrapper.like("url_name", urlName);
        }
        if (!ObjectUtils.isEmpty(url)) {
            queryWrapper.like("url", url);
        }
        queryWrapper.orderByDesc("create_time");
        return permissionMapper.selectPage(permissionInfo.getPermissionInfoPage(), queryWrapper);
    }

    @Override
    public PermissionInfo queryPermissionByRoleId(Long roleId) {
        return permissionMapper.selectPermissionByRoleId(roleId);
    }

    @Override
    public List<PermissionInfo> queryPermissionByOpen(int open) {
        QueryWrapper<PermissionInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("open", open);
        return permissionMapper.selectList(queryWrapper);
    }
}
