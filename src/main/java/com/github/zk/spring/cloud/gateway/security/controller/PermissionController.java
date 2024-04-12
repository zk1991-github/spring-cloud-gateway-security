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

package com.github.zk.spring.cloud.gateway.security.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.zk.spring.cloud.gateway.security.common.CodeEnum;
import com.github.zk.spring.cloud.gateway.security.common.Response;
import com.github.zk.spring.cloud.gateway.security.pojo.PermissionInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.RoleInfo;
import com.github.zk.spring.cloud.gateway.security.service.IPermission;
import com.github.zk.spring.cloud.gateway.security.service.IRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限请求控制
 *
 * @author zk
 * @since 4.0
 */
@RestController
@RequestMapping("/gateway")
public class PermissionController {

    @Autowired
    private IPermission iPermission;
    @Autowired
    private IRole iRole;

    @PostMapping("/addPermission")
    public Response addPermission(@RequestBody @Validated(PermissionInfo.AddPermission.class) PermissionInfo permissionInfo) {
        int addPermissionCount = iPermission.addPermission(permissionInfo);
        if (addPermissionCount > 0) {
            return Response.setOk(addPermissionCount);
        } else {
            return Response.setError(CodeEnum.SAVE_FAIL);
        }
    }

    @GetMapping("/delPermission")
    public Response delPermission(@RequestParam Long id) {
        int del = iPermission.delPermission(id);
        if (del > 0) {
            return Response.setOk();
        } else {
            return Response.setError(CodeEnum.REMOVE_FAIL);
        }
    }

    @GetMapping("delPermissions")
    public Response delPermissions(@RequestParam List<Long> ids) {
        int del = iPermission.delPermissions(ids);
        if (del > 0) {
            return Response.setOk();
        } else {
            return Response.setError(CodeEnum.REMOVE_FAIL);
        }
    }

    @PostMapping("/updatePermission")
    public Response updatePermission(@RequestBody @Validated(PermissionInfo.UpdatePermission.class) PermissionInfo permissionInfo) {
        int update = iPermission.updatePermission(permissionInfo);
        if (update > 0) {
            return Response.setOk();
        } else {
            return Response.setError(CodeEnum.UPDATE_FAIL);
        }
    }

    @GetMapping("/queryPermission")
    public Response queryPermission(@RequestParam("keywords") String keywords, Page<PermissionInfo> page) {
        Page<PermissionInfo> permissionInfoPage = iPermission.queryPermission(keywords, page);
        if (permissionInfoPage != null) {
            return Response.setOk(permissionInfoPage);
        } else {
            return Response.setError(CodeEnum.QUERY_FAIL);
        }
    }

    @GetMapping("/queryPrivatePermission")
    public Response queryPrivatePermission(Page<PermissionInfo> page) {
        //私有权限
        int open = 0;
        Page<PermissionInfo> permissionInfoPage = iPermission.queryPagePermissionByOpen(open, page);
        if (permissionInfoPage != null) {
            return Response.setOk(permissionInfoPage);
        } else {
            return Response.setError(CodeEnum.QUERY_FAIL);
        }
    }

    @PostMapping("/bindPermissionByRole")
    public Response bindPermissionByRole(@RequestBody RoleInfo roleInfo) {
        boolean b = iRole.bindPermissionsByRole(roleInfo);
        if (b) {
            return Response.setOk();
        } else {
            return Response.setError(CodeEnum.BIND_FAIL);
        }
    }

    @GetMapping("/queryAllRoles")
    public Response queryAllRoles() {
        List<RoleInfo> roleInfos = iRole.queryAllRoles();
        if (roleInfos != null) {
            return Response.setOk(roleInfos);
        } else {
            return Response.setError(CodeEnum.QUERY_FAIL);
        }
    }

    @GetMapping("/queryPermissionsByRoleId")
    public Response queryPermissionsByRoleId(@RequestParam Long roleId) {
        List<PermissionInfo> permissionInfo = iRole.queryPermissionsByRoleId(roleId);
        if (permissionInfo != null) {
            return Response.setOk(permissionInfo);
        } else {
            return Response.setError(CodeEnum.QUERY_FAIL);
        }
    }
}
