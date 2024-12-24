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
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.zk.spring.cloud.gateway.security.core.GatewaySecurityCache;
import com.github.zk.spring.cloud.gateway.security.dao.GroupPermissionMapper;
import com.github.zk.spring.cloud.gateway.security.dao.PermissionMapper;
import com.github.zk.spring.cloud.gateway.security.enums.IntfTypeEnum;
import com.github.zk.spring.cloud.gateway.security.pojo.PermissionGroup;
import com.github.zk.spring.cloud.gateway.security.pojo.PermissionInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.RoleInfo;
import com.github.zk.spring.cloud.gateway.security.service.IPermission;
import com.github.zk.spring.cloud.gateway.security.service.IRolePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限实现类
 *
 * @author zk
 * @since 4.0
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
    private GroupPermissionMapper groupPermissionMapper;
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
            // 刷新公开和匿名权限
            refreshOpenPermission();
            refreshAnonymousPermission();
            return update;
        }
        return 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean moveOutGroup(Long id, Long groupId) {
        //查询当前分组下存在多少权限
        QueryWrapper<PermissionInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", groupId);
        Long permissionCount = permissionMapper.selectCount(queryWrapper);
        //当存在权限在2个以上时，只移出当前权限；
        //当权限在2个（包含）以内时，将2个权限一起修改状态，同时删除分组信息
        boolean status;
        if (permissionCount > 2) {
            PermissionInfo pi = new PermissionInfo();
            pi.setId(id);
            pi.setGroupId(0L);
            status = permissionMapper.updateById(pi) > 0;
        } else {
            //修改2个权限分组
            UpdateWrapper<PermissionInfo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("group_id", 0L).eq("group_id", groupId);
            int update = permissionMapper.update(updateWrapper);
            //删除分组
            int del = groupPermissionMapper.deleteById(groupId);
            status = update > 0 && del > 0;
        }
        return status;
    }

    @Override
    public Page<PermissionInfo> queryPermission(String keywords, Page<PermissionInfo> page) {
        QueryWrapper<PermissionInfo> queryWrapper = new QueryWrapper<>();
        if (!ObjectUtils.isEmpty(keywords)) {
            queryWrapper.like("url_name", keywords)
                    .or().like("url", keywords)
                    .or().like("gd.dict_name", keywords);
        }
        queryWrapper.orderByDesc("create_time");
        return permissionMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Page<PermissionGroup> queryGroupPermissionsPage(String keywords, Page<PermissionInfo> page) {
        QueryWrapper<PermissionInfo> queryWrapper = new QueryWrapper<>();
        if (!ObjectUtils.isEmpty(keywords)) {
            queryWrapper.like("url_name", keywords)
                    .or().like("url", keywords)
                    .or().like("gd.dict_name", keywords);
        }
        // 分页查询权限
        Page<PermissionInfo> permissionInfoPage = permissionMapper.selectGroupPermissionsPage(page, queryWrapper);
        // 获取权限以及权限组
        List<PermissionInfo> records = permissionInfoPage.getRecords();
        List<Long> groupIds = filterGroupIds(records);
        List<PermissionInfo> permissionGroups = null;
        if (!groupIds.isEmpty()) {
            permissionGroups = permissionMapper.selectPermissionsByGroupIds(groupIds, queryWrapper);
        }
        // 构建权限分组集合
        List<PermissionGroup> newRecords = buildResult(records, permissionGroups);
        // 构建新分页对象
        Page<PermissionGroup> newPage = new Page<>();
        newPage.setCurrent(page.getCurrent());
        newPage.setSize(page.getSize());
        newPage.setTotal(page.getTotal());
        newPage.setRecords(newRecords);
        return newPage;
    }

    /**
     * 过滤存在组的 id
     *
     * @param records 权限列表
     * @return 组 id 列表
     */
    private List<Long> filterGroupIds(List<PermissionInfo> records) {
        return records.stream()
                .map(PermissionInfo::getGroupId)
                .filter(groupId -> groupId != 0)
                .collect(Collectors.toList());
    }

    /**
     * 构建新分组权限集合
     *
     * @param srcList 源权限集合
     * @param permissionGroups 带分组的权限集合
     * @return 新的分组集合
     */
    private List<PermissionGroup> buildResult(List<PermissionInfo> srcList,
                                              List<PermissionInfo> permissionGroups) {
        List<PermissionGroup> list = new ArrayList<>();
        for (PermissionInfo permissionInfo : srcList) {
            if (permissionInfo.getGroupId() == 0) {
                List<PermissionInfo> noGroupPermission = new ArrayList<>();
                noGroupPermission.add(permissionInfo);
                PermissionGroup pg = new PermissionGroup();
                pg.setId(0L);
                pg.setGroupName("未分组");
                pg.setPermissionInfos(noGroupPermission);
                list.add(pg);
            } else {
                List<PermissionInfo> groupPermissions = new ArrayList<>();
                PermissionGroup pg = new PermissionGroup();
                pg.setId(permissionInfo.getGroupId());
                pg.setGroupName(permissionInfo.getGroupName());
                for (PermissionInfo permissionGroup : permissionGroups) {
                    if (ObjectUtils.nullSafeEquals(permissionGroup.getGroupId(), permissionInfo.getGroupId())) {
                        groupPermissions.add(permissionGroup);
                    }
                }
                pg.setPermissionInfos(groupPermissions);
                list.add(pg);
            }
        }
        return list;
    }

    @Override
    public Page<PermissionInfo> queryPagePermissionByOpen(int open, String keywords, Page<PermissionInfo> page) {
        QueryWrapper<PermissionInfo> queryWrapper = new QueryWrapper<>();
        if (!ObjectUtils.isEmpty(keywords)) {
            queryWrapper.like("url_name", keywords)
                    .or().like("url", keywords)
                    .or().like("gd.dict_name", keywords);
        }
        queryWrapper.eq("open", open);
        queryWrapper.orderByDesc("create_time");
        return permissionMapper.selectPage(page, queryWrapper);
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
