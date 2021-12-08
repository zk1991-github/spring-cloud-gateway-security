package com.github.zk.spring.cloud.gateway.security.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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

    private Long id;
    private Long pid;
    private String urlName;
    private String url;
    /** 数据库不包含此字段 */
    @TableField(exist = false)
    private List<PermissionInfo> permissionInfos;

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

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getUrlName() {
        return urlName;
    }

    public void setUrlName(String urlName) {
        this.urlName = urlName;
    }

    public List<PermissionInfo> getPermissionInfos() {
        return permissionInfos;
    }

    public void setPermissionInfos(List<PermissionInfo> permissionInfos) {
        this.permissionInfos = permissionInfos;
    }

    @Override
    public String toString() {
        return "PermissionInfo{" +
                "id=" + id +
                ", pid=" + pid +
                ", urlName='" + urlName + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
