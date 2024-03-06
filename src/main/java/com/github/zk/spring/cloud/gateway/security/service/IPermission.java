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

package com.github.zk.spring.cloud.gateway.security.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.zk.spring.cloud.gateway.security.pojo.PermissionInfo;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 权限接口
 *
 * @author zk
 * @since 4.0
 */
public interface IPermission {

    /**
     * 添加权限
     * @param permissionInfo 权限信息
     * @return 添加数量
     */
    int addPermission(PermissionInfo permissionInfo);

    /**
     * 删除权限
     * @param id 权限id
     * @return 删除数量
     */
    int delPermission(long id);

    /**
     * 批量删除权限
     * @param ids 权限id
     * @return 删除条数
     */
    int delPermissions(List<Long> ids);

    /**
     * 修改权限
     * @param permissionInfo 权限信息
     * @return 修改权限
     */
    int updatePermission(PermissionInfo permissionInfo);

    /**
     * 分页查询权限
     * @param permissionInfo 查询条件
     * @return 权限列表
     */
    Page<PermissionInfo> queryPermission(PermissionInfo permissionInfo);

    /**
     * 根据公开状态查询权限列表
     * @param open 公开状态
     * @return 权限
     */
    List<PermissionInfo> queryPermissionByOpen(int open);

    /**
     * 缓存公开权限
     * @return 公开权限数量
     */
    int cacheOpenPermissions();

    /**
     * 缓存匿名权限
     * @return 匿名权限数量
     */
    int cacheAnonymousPermissions();

    /**
     * 获取缓存中的公开权限
     * @return 权限列表
     */
    Mono<List<PermissionInfo>> getCacheOpenPermission();

    /**
     * 刷新公开权限
     */
    void refreshOpenPermission();

    /**
     * 获取缓存中的匿名权限
     * @return 权限列表
     */
    Mono<List<PermissionInfo>> getCacheAnonymousPermission();

    /**
     * 刷新匿名权限
     */
    void refreshAnonymousPermission();
}
