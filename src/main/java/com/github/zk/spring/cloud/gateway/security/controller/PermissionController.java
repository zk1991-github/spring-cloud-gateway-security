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

package com.github.zk.spring.cloud.gateway.security.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.zk.spring.cloud.gateway.security.common.Response;
import com.github.zk.spring.cloud.gateway.security.pojo.PermissionInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.RoleInfo;
import com.github.zk.spring.cloud.gateway.security.service.IPermission;
import com.github.zk.spring.cloud.gateway.security.service.IRole;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 权限请求控制
 *
 * @author zk
 * @date 2022/2/14 14:40
 */
@RestController
@RequestMapping("/gateway")
public class PermissionController {

    @Autowired
    private IPermission iPermission;
    @Autowired
    private IRole iRole;

    @PostMapping("/addPermission")
    public Response addPermission(@RequestBody PermissionInfo permissionInfo) {
        Response response = Response.getInstance();
        int addPermissionCount = iPermission.addPermission(permissionInfo);
        if (addPermissionCount > 0) {
            response.setOk(Response.CodeEnum.SUCCESSED, "/gateway/addPermission", "添加成功！", addPermissionCount);
        } else {
            response.setError(Response.CodeEnum.FAIL, "/gateway/addPermission", "添加失败!");
        }
        return response;
    }

    @GetMapping("/delPermission")
    public Response delPermission(@RequestParam Long id) {
        Response response = Response.getInstance();
        int del = iPermission.delPermission(id);
        if (del > 0) {
            response.setOk(Response.CodeEnum.SUCCESSED, "/gateway/delPermission", "删除成功！", del);
        } else {
            response.setError(Response.CodeEnum.FAIL, "/gateway/delPermission", "删除失败！");
        }
        return response;
    }

    @PostMapping("/updatePermission")
    public Response updatePermission(@RequestBody PermissionInfo permissionInfo) {
        Response response = Response.getInstance();
        int update = iPermission.updatePermission(permissionInfo);
        if (update > 0) {
            response.setOk(Response.CodeEnum.SUCCESSED, "/gateway/queryPermission", "修改成功！",
                    update);
        } else {
            response.setError(Response.CodeEnum.FAIL, "/gateway/queryPermission", "修改失败！");
        }
        return response;
    }

    @PostMapping("/queryPermission")
    public Response queryPermission(@RequestBody PermissionInfo permissionInfo) {
        Response response = Response.getInstance();
        Page<PermissionInfo> permissionInfoPage = iPermission.queryPermission(permissionInfo);
        if (permissionInfoPage != null) {
            response.setOk(Response.CodeEnum.SUCCESSED, "/gateway/queryPermission", "查询成功！",
                    permissionInfoPage.getRecords(), permissionInfoPage.getTotal());
        } else {
            response.setError(Response.CodeEnum.FAIL, "/gateway/queryPermission", "查询失败！");
        }
        return response;
    }

    @PostMapping("/bindPermissionByRole")
    public Response bindPermissionByRole(@RequestBody RoleInfo roleInfo) {
        Response response = Response.getInstance();
        boolean b = iRole.bindPermissionsByRole(roleInfo);
        if (b) {
            response.setOk(Response.CodeEnum.SUCCESSED, "/gateway/bindPermissionByRole", "绑定成功！", b);
        } else {
            response.setError(Response.CodeEnum.FAIL, "/gateway/bindPermissionByRole", "绑定失败！");
        }
        return response;
    }

    @PostMapping("/queryAllRoles")
    public Response queryAllRoles() {
        Response response = Response.getInstance();
        List<RoleInfo> roleInfos = iRole.queryAllRoles();
        if (roleInfos != null) {
            response.setOk(Response.CodeEnum.SUCCESSED, "/gateway/queryAllRoles", "查询成功！", roleInfos);
        } else {
            response.setError(Response.CodeEnum.FAIL, "/gateway/queryAllRoles", "查询失败！");
        }
        return response;
    }

    @GetMapping("/queryPermissionByRoleId")
    public Response queryPermissionByRoleId(@RequestParam Long roleId) {
        Response response = Response.getInstance();
        PermissionInfo permissionInfo = iPermission.queryPermissionByRoleId(roleId);
        if (permissionInfo != null) {
            response.setOk(Response.CodeEnum.SUCCESSED, "/gateway/queryPermissionByRoleId", "查新成功！", permissionInfo);
        } else {
            response.setError(Response.CodeEnum.FAIL, "/gateway/queryPermissionByRoleId", "查询失败！");
        }
        return response;
    }
}
