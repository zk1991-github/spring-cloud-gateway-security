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

package com.github.zk.spring.cloud.gateway.security.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.zk.spring.cloud.gateway.security.pojo.PermissionInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.RoleInfo;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色仓储
 *
 * @author zk
 * @since 2.0
 */
@Repository
public interface RoleMapper extends BaseMapper<RoleInfo> {

    /**
     * 根据id 查询角色
     * @param ids 角色id
     * @return 角色列表
     */
    List<RoleInfo> selectRolesByIds(Long[] ids);

    /**
     * 根据角色查询权限
     * @param id 角色id
     * @param open 公开状态
     * @return 权限信息
     */
    List<PermissionInfo> selectPermissionsByRoleId(@Param("id") Long id, @Param("open") Integer open);
}
