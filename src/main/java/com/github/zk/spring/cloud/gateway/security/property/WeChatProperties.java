package com.github.zk.spring.cloud.gateway.security.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微信配置类
 *
 * @author zk
 * @date 2021/11/17 9:40
 */
@Configuration
@ConfigurationProperties(prefix = "wechat")
public class WeChatProperties {
    /** 微信请求地址  */
    private String url;
    /** 应用id */
    private String appid;
    /** 应用权限 */
    private String appsecret;
    /** 角色id数组 */
    private Long[] roleIds;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAppsecret() {
        return appsecret;
    }

    public void setAppsecret(String appsecret) {
        this.appsecret = appsecret;
    }

    public Long[] getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Long[] roleIds) {
        this.roleIds = roleIds;
    }
}
