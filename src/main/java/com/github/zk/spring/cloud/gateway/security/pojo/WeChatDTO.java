package com.github.zk.spring.cloud.gateway.security.pojo;

/**
 * 微信传参
 *
 * @author zk
 * @date 2021/11/17 13:21
 */
public class WeChatDTO {
    /**
     * 微信code
     */
    private String weChatCode;
    /**
     * 微信用户信息
     */
    private WeChatUserInfo weChatUserInfo;

    public String getWeChatCode() {
        return weChatCode;
    }

    public void setWeChatCode(String weChatCode) {
        this.weChatCode = weChatCode;
    }

    public WeChatUserInfo getWeChatUserInfo() {
        return weChatUserInfo;
    }

    public void setWeChatUserInfo(WeChatUserInfo weChatUserInfo) {
        this.weChatUserInfo = weChatUserInfo;
    }
}
