package com.github.zk.spring.cloud.gateway.security.pojo;

/**
 * 微信返回实体
 *
 * @author zk
 * @date 2021/11/16 16:39
 */
public class WeChatDO {
    /** 微信openid */
    private String openid;
    /** 会话key */
    private String sessionKey;
    /** 错误码 */
    private Integer errcode;
    /** 错误信息 */
    private String errmsg;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public Integer getErrcode() {
        return errcode;
    }

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }
}
