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
import com.github.zk.spring.cloud.gateway.security.dao.GroupPermissionMapper;
import com.github.zk.spring.cloud.gateway.security.dao.PermissionMapper;
import com.github.zk.spring.cloud.gateway.security.enums.PermissionMoveType;
import com.github.zk.spring.cloud.gateway.security.pojo.GroupPermissionDTO;
import com.github.zk.spring.cloud.gateway.security.pojo.PermissionGroup;
import com.github.zk.spring.cloud.gateway.security.pojo.PermissionInfo;
import com.github.zk.spring.cloud.gateway.security.service.IGroupPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 权限分组实现
 *
 * @author zhaokai
 * @since 4.3.6
 */
@Service
public class GroupPermissionImpl implements IGroupPermission {

    @Autowired
    private GroupPermissionMapper groupPermissionMapper;
    @Autowired
    private PermissionMapper permissionMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean movePermission(GroupPermissionDTO gpDTO) {
        PermissionMoveType permissionMoveType = validMoveType(gpDTO.getSrcGroupId(), gpDTO.getTargetGroupId());
        Long groupId;
        long permissionCount;
        switch (permissionMoveType) {
            // 没有做移动，直接返回 true
            case MOVE_NO:
                return true;
            // 移入组内，组 id 为传入的目标组 id
            case MOVE_IN:
                groupId = gpDTO.getTargetGroupId();
                break;
            // 移出分组
            case MOVE_OUT:
                permissionCount = permissionCount(gpDTO.getSrcGroupId());
                if (permissionCount <= 2) {
                    return movePermissionAndDelGroup(gpDTO.getSrcGroupId());
                }
                groupId = 0L;
                break;
            case MOVE_OUT_IN:
                permissionCount = permissionCount(gpDTO.getSrcGroupId());
                if (permissionCount <= 2) {
                    return movePermissionAndDelGroup(gpDTO.getSrcGroupId());
                }
                groupId = gpDTO.getTargetGroupId();
                break;
            // 创建新分组
            case MOVE_NEW_GROUP:
                PermissionGroup permissionGroup = new PermissionGroup();
                permissionGroup.setGroupName(gpDTO.getGroupName());
                groupId = addGroupPermission(permissionGroup);
                break;
            default:
                return false;
        }
        return updatePermissionGroupByPermissionIds(gpDTO.getPermissionIds(), groupId);
    }

    /**
     * 验证移动类型
     *
     * @param srcGroupId    源分组 id
     * @param targetGroupId 目标分组 id
     * @return 移动类型
     */
    private PermissionMoveType validMoveType(Long srcGroupId, Long targetGroupId) {
        if (ObjectUtils.isEmpty(targetGroupId)) {
            return PermissionMoveType.MOVE_NEW_GROUP;
        }
        if (targetGroupId == 0) {
            return PermissionMoveType.MOVE_OUT;
        }
        if (ObjectUtils.nullSafeEquals(srcGroupId, targetGroupId)) {
            return PermissionMoveType.MOVE_NO;
        }
        if (srcGroupId != 0) {
            return PermissionMoveType.MOVE_OUT_IN;
        }
        return PermissionMoveType.MOVE_IN;
    }

    /**
     * 查询当前分组下存在多少权限
     *
     * @param groupId 分组 id
     * @return 权限个数
     */
    private long permissionCount(Long groupId) {
        QueryWrapper<PermissionInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", groupId);
        return permissionMapper.selectCount(queryWrapper);
    }

    /**
     * 移出权限并删除分组
     *
     * @param groupId 组 id
     * @return 是否成功状态
     */
    private boolean movePermissionAndDelGroup(Long groupId) {
        UpdateWrapper<PermissionInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("group_id", 0L).eq("group_id", groupId);
        int update = permissionMapper.update(updateWrapper);
        //删除分组
        int del = groupPermissionMapper.deleteById(groupId);
        return update > 0 && del > 0;
    }

    @Override
    public Long addGroupPermission(PermissionGroup groupPermission) {
        int insert = groupPermissionMapper.insert(groupPermission);
        return insert > 0 ? groupPermission.getId() : null;
    }

    @Override
    public boolean updatePermissionGroupByPermissionIds(List<Long> permissionIds, Long groupId) {
        UpdateWrapper<PermissionInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("group_id", groupId)
                .in("id", permissionIds);
        return permissionMapper.update(updateWrapper) > 0;
    }
}
