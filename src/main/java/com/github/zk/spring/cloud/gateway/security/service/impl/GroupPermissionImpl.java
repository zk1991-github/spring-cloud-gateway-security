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

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.zk.spring.cloud.gateway.security.dao.GroupPermissionMapper;
import com.github.zk.spring.cloud.gateway.security.dao.PermissionMapper;
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
    public boolean groupPermission(GroupPermissionDTO gpDTO) {
        Long groupId;
        //当组 id 为空时，需要创建新分组
        if (ObjectUtils.isEmpty(gpDTO.getGroupId())) {
            PermissionGroup gp = new PermissionGroup();
            gp.setGroupName(gpDTO.getGroupName());
            groupId = addGroupPermission(gp);
        } else {
            groupId = gpDTO.getGroupId();
        }
        return updatePermissionGroupByPermissionIds(gpDTO.getPermissionIds(), groupId);
    }

    @Override
    public Long addGroupPermission(PermissionGroup groupPermission) {
        int insert = groupPermissionMapper.insert(groupPermission);
        return insert > 0 ? groupPermission.getId() : null;
    }

    @Override
    public boolean updatePermissionGroupByPermissionIds(List<Long> permissionIds, long groupId) {
        UpdateWrapper<PermissionInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("group_id", groupId)
                .in("id", permissionIds);
        return permissionMapper.update(updateWrapper) > 0;
    }
}
