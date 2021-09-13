package com.github.zk.spring.cloud.gateway.security.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.List;

/**
 * 角色信息实体类
 *
 * @author zk
 * @date 2021/1/21 10:32
 */
@TableName("t_role")
@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
public class RoleInfo implements Serializable {
    private static final long serialVersionUID = -6703773100368931284L;

    private Long id;
    private String roleName;
    private List<PermissionInfo> permissionInfos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<PermissionInfo> getPermissionInfos() {
        return permissionInfos;
    }

    public void setPermissionInfos(List<PermissionInfo> permissionInfos) {
        this.permissionInfos = permissionInfos;
    }

    @Override
    public String toString() {
        return "RoleInfo{" +
                "id=" + id +
                ", roleName='" + roleName + '\'' +
                ", permissionInfos=" + permissionInfos +
                '}';
    }
}
