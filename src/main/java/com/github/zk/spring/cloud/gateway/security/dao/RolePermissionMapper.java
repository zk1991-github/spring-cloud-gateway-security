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

package com.github.zk.spring.cloud.gateway.security.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.zk.spring.cloud.gateway.security.pojo.RolePermission;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * 角色权限关联仓储
 *
 * @author zk
 * @date 2022/2/15 14:58
 */
@Repository
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
    /**
     * 根据权限id，批量删除角色权限关系
     * @param permissionIds 权限id
     * @return 删除条数
     */
    int delRolePermissionByPermissions(List<Long> permissionIds);
}
