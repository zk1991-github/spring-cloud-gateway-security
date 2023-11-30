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

package com.github.zk.spring.cloud.gateway.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.zk.spring.cloud.gateway.security.core.GatewaySecurityCache;
import com.github.zk.spring.cloud.gateway.security.dao.PermissionMapper;
import com.github.zk.spring.cloud.gateway.security.enums.IntfTypeEnum;
import com.github.zk.spring.cloud.gateway.security.pojo.PermissionInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.RoleInfo;
import com.github.zk.spring.cloud.gateway.security.service.IPermission;
import com.github.zk.spring.cloud.gateway.security.service.IRolePermission;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;

/**
 * 权限实现类
 *
 * @author zk
 * @date 2022/2/14 11:06
 */
@Service
public class PermissionImpl implements IPermission {
    /**
     * 公开权限 key
     */
    private final String PUBLIC_PERMISSION_KEY = "publicPermission";
    /**
     * 匿名权限 key
     */
    private final String ANONYMOUS_PERMISSION_KEY = "anonymousPermission";

    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private IRolePermission iRolePermission;
    @Autowired
    private GatewaySecurityCache gatewaySecurityCache;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addPermission(PermissionInfo permissionInfo) {
        // 新增权限是可编辑的
        permissionInfo.setFixed(0);
        List<RoleInfo> roleInfos = permissionInfo.getRoleInfos();
        // 如果为私有权限，并且角色为空时，传入参数有误，不插入权限直接返回失败
        if (permissionInfo.getOpen() == IntfTypeEnum.PRIVATE_PERMISSION.getIndex() &&
                ObjectUtils.isEmpty(roleInfos)) {
            return 0;
        }
        int insert = permissionMapper.insert(permissionInfo);
        Long permissionId = permissionInfo.getId();
        // 公开权限只添加权限表
        if (permissionInfo.getOpen() == IntfTypeEnum.PUBLIC_PERMISSION.getIndex()) {
            // 刷新公开权限数据
            refreshOpenPermission();
            return insert;
        }
        // 匿名权限只添加权限表
        if (permissionInfo.getOpen() == IntfTypeEnum.ANONYMOUS_PERMISSION.getIndex()) {
            // 刷新匿名权限
            refreshAnonymousPermission();
            return insert;
        }
        // 角色绑定权限
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
        boolean delRolePermission = iRolePermission.delRolePermissionByPermissionId(id);
        // 角色关系删除失败表示此权限为公开或匿名权限，需要刷新公开和匿名权限
        if (!delRolePermission) {
            refreshOpenPermission();
            refreshAnonymousPermission();
        }
        return del;
    }

    @Override
    public int delPermissions(List<Long> ids) {
        int delNum = iRolePermission.delRolePermissionByPermissionIds(ids);
        int del = permissionMapper.deleteBatchIds(ids);
        // 角色关系删除数量与权限id数量不相等时，表示存在公开或匿名权限，需要刷新公开和匿名权限
        if (delNum != ids.size()) {
            refreshOpenPermission();
            refreshAnonymousPermission();
        }
        return del;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updatePermission(PermissionInfo permissionInfo) {
        List<RoleInfo> roleInfos = permissionInfo.getRoleInfos();
        Long permissionId = permissionInfo.getId();
        if (permissionInfo.getOpen() == IntfTypeEnum.PRIVATE_PERMISSION.getIndex() &&
                ObjectUtils.isEmpty(roleInfos)) {
            return 0;
        }
        int update = permissionMapper.updateById(permissionInfo);
        // 接口公开时，删除绑定此接口的角色关系
        if (permissionInfo.getOpen() == IntfTypeEnum.PUBLIC_PERMISSION.getIndex()) {
            iRolePermission.delRolePermissionByPermissionId(permissionId);
            // 刷新公开权限
            refreshOpenPermission();
            return update;
        }
        // 接口匿名时，删除绑定此接口的角色关系
        if (permissionInfo.getOpen() == IntfTypeEnum.ANONYMOUS_PERMISSION.getIndex()) {
            iRolePermission.delRolePermissionByPermissionId(permissionId);
            // 刷新匿名权限
            refreshAnonymousPermission();
            return update;
        }

        //删除原有权限
        iRolePermission.delRolePermissionByPermissionId(permissionId);
        // 绑定新权限
        boolean add = iRolePermission.addBatchPermissionRoles(permissionId, roleInfos);
        if (add) {
            // 刷新公开和私有权限
            refreshOpenPermission();
            refreshAnonymousPermission();
            return update;
        }
        return 0;
    }

    @Override
    public Page<PermissionInfo> queryPermission(PermissionInfo permissionInfo) {
        String keywords = permissionInfo.getKeywords();
        QueryWrapper<PermissionInfo> queryWrapper = new QueryWrapper<>();
        if (!ObjectUtils.isEmpty(keywords)) {
            queryWrapper.like("url_name", keywords)
                    .or().like("url", keywords)
                    .or().like("gd.dict_name", keywords);
        }
        queryWrapper.orderByDesc("create_time");
        return permissionMapper.selectPage(permissionInfo.getPermissionInfoPage(), queryWrapper);
    }

    @Override
    public List<PermissionInfo> queryPermissionByOpen(int open) {
        QueryWrapper<PermissionInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("open", open);
        return permissionMapper.selectList(queryWrapper);
    }

    @Override
    public int cacheOpenPermissions() {
        // 查询公开接口权限列表
        List<PermissionInfo> permissionInfos = queryPermissionByOpen(IntfTypeEnum.PUBLIC_PERMISSION.getIndex());
        gatewaySecurityCache.cachePermissions(PUBLIC_PERMISSION_KEY, permissionInfos).subscribe();
        return permissionInfos.size();
    }

    @Override
    public int cacheAnonymousPermissions() {
        // 查询匿名接口权限列表
        List<PermissionInfo> permissionInfos = queryPermissionByOpen(IntfTypeEnum.ANONYMOUS_PERMISSION.getIndex());
        gatewaySecurityCache.cachePermissions(ANONYMOUS_PERMISSION_KEY, permissionInfos).subscribe();
        return permissionInfos.size();
    }

    @Override
    public Mono<List<PermissionInfo>> getCacheOpenPermission() {
        return gatewaySecurityCache.getCachePermission(PUBLIC_PERMISSION_KEY);
    }

    @Override
    public void refreshOpenPermission() {
        cacheOpenPermissions();
    }

    @Override
    public Mono<List<PermissionInfo>> getCacheAnonymousPermission() {
        return gatewaySecurityCache.getCachePermission(ANONYMOUS_PERMISSION_KEY);
    }

    @Override
    public void refreshAnonymousPermission() {
        cacheAnonymousPermissions();
    }
}
