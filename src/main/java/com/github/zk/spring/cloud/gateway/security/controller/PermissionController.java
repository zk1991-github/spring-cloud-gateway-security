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
import com.github.zk.spring.cloud.gateway.security.core.GatewaySecurityCache;
import com.github.zk.spring.cloud.gateway.security.pojo.GroupPermissionDTO;
import com.github.zk.spring.cloud.gateway.security.pojo.PermissionGroup;
import com.github.zk.spring.cloud.gateway.security.pojo.PermissionInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.RoleInfo;
import com.github.zk.spring.cloud.gateway.security.service.IGroupPermission;
import com.github.zk.spring.cloud.gateway.security.service.IPermission;
import com.github.zk.spring.cloud.gateway.security.service.IRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

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
    private IGroupPermission iGroupPermission;
    @Autowired
    private IRole iRole;
    @Autowired
    private GatewaySecurityCache gatewaySecurityCache;

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

    @PostMapping("/groupPermission")
    public Response groupPermission(@RequestBody GroupPermissionDTO groupPermissionDTO) {
        boolean b = iGroupPermission.groupPermission(groupPermissionDTO);
        if (b) {
            return Response.setOk();
        }
        return Response.setError();
    }

    @GetMapping("/moveOutGroup")
    public Response moveOutGroup(@RequestParam("id") Long id, @RequestParam("groupId") Long groupId) {
        boolean b = iPermission.moveOutGroup(id, groupId);
        if (b) {
            return Response.setOk();
        } else {
            return Response.setError();
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

    @GetMapping("/queryPermissionGroupPage")
    public Response queryPermissionGroupPage(@RequestParam("keywords") String keywords, Page<PermissionInfo> page) {
        Page<PermissionGroup> permissionInfoPage = iPermission.queryGroupPermissionsPage(keywords, page);
        if (permissionInfoPage != null) {
            return Response.setOk(permissionInfoPage);
        } else {
            return Response.setError(CodeEnum.QUERY_FAIL);
        }
    }

    /**
     * 分页查询私有权限
     *
     * @param keywords 关键字
     * @param page 分页对象
     * @return 响应体
     * @since 4.3.4
     */
    @GetMapping("/queryPrivatePermission")
    public Response queryPrivatePermission(@RequestParam("keywords") String keywords, Page<PermissionInfo> page) {
        //私有权限
        int open = 0;
        Page<PermissionInfo> permissionInfoPage = iPermission.queryPagePermissionByOpen(open, keywords, page);
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

    /**
     * 清空所有用户会话
     * @param webSession 用户会话
     * @return 无返回
     * @since 4.3.4
     */
    @GetMapping("/clearAllSessions")
    public Mono<Void> clearAllSessions(WebSession webSession) {
        return webSession.invalidate().then(gatewaySecurityCache.removeAllSessions().then());
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
