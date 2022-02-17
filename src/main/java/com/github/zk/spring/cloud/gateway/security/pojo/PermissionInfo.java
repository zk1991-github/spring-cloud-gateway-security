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

package com.github.zk.spring.cloud.gateway.security.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.List;

/**
 * 权限信息
 *
 * @author zk
 * @date 2021/1/21 10:41
 */
@TableName("t_permission")
@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
public class PermissionInfo implements Serializable {
    private static final long serialVersionUID = 874671003093440548L;

    /** 权限id */
    private Long id;
    /** 地址名称 */
    private String urlName;
    /** 地址 */
    private String url;
    /** 是否公开 0：不公开；1：公开 */
    private Integer open;
    /** 描述 */
    private String description;
    /** 创建时间 */
    private String createTime;
    /** 分页对象 */
    @TableField(exist = false)
    private Page<PermissionInfo> permissionInfoPage;
    /** 角色列表 */
    @TableField(exist = false)
    private List<RoleInfo> roleInfos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlName() {
        return urlName;
    }

    public void setUrlName(String urlName) {
        this.urlName = urlName;
    }

    public Integer getOpen() {
        return open;
    }

    public void setOpen(Integer open) {
        this.open = open;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Page<PermissionInfo> getPermissionInfoPage() {
        return permissionInfoPage;
    }

    public void setPermissionInfoPage(Page<PermissionInfo> permissionInfoPage) {
        this.permissionInfoPage = permissionInfoPage;
    }

    public List<RoleInfo> getRoleInfos() {
        return roleInfos;
    }

    public void setRoleInfos(List<RoleInfo> roleInfos) {
        this.roleInfos = roleInfos;
    }

    @Override
    public String toString() {
        return "PermissionInfo{" +
                "id=" + id +
                ", urlName='" + urlName + '\'' +
                ", url='" + url + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
