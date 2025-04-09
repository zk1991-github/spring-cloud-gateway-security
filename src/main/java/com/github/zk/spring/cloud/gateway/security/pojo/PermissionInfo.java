/*
 *
 *  * Copyright 2021-2025 the original author or authors.
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

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;

/**
 * 权限信息
 *
 * @author zk
 * @since 1.0
 */
@TableName("gateway_permission")
@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
public class PermissionInfo implements Serializable {

    public interface AddPermission {
    }

    public interface UpdatePermission {
    }

    private static final long serialVersionUID = 874671003093440548L;

    /**
     * 权限id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 组 id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long groupId;

    @TableField(exist = false)
    private String groupName;
    /**
     * 地址名称
     */
    @NotEmpty(groups = {AddPermission.class, UpdatePermission.class})
    private String urlName;
    /**
     * 地址
     */
    @NotEmpty(groups = {AddPermission.class, UpdatePermission.class})
    private String url;
    /**
     * 是否公开 0：不公开；1：公开；2：匿名
     */
    @NotNull(groups = {AddPermission.class, UpdatePermission.class})
    private Integer open;
    /**
     * 描述
     */
    private String description;
    /**
     * 是否固定
     */
    private Integer fixed;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    /**
     * 角色列表
     */
    @TableField(exist = false)
    private List<RoleInfo> roleInfos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    public Integer getFixed() {
        return fixed;
    }

    public void setFixed(Integer fixed) {
        this.fixed = fixed;
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
