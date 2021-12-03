package com.github.zk.spring.cloud.gateway.security.config;

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
    private String url;
    private String appid;
    private String appsecret;

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
}
