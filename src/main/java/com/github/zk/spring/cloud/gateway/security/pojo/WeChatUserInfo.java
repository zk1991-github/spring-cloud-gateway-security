package com.github.zk.spring.cloud.gateway.security.pojo;

import com.github.zk.spring.cloud.gateway.security.core.WeChatUserDetails;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;

/**
 * 微信传参实体
 *
 * @author zk
 * @date 2021/11/16 16:53
 */
public class WeChatUserInfo implements WeChatUserDetails {
    public WeChatUserInfo() {
    }

    public WeChatUserInfo(String openid, String nickName, Integer gender, String avatarUrl, String country, String province, String city) {
        this.openid = openid;
        this.nickName = nickName;
        this.gender = gender;
        this.avatarUrl = avatarUrl;
        this.country = country;
        this.province = province;
        this.city = city;
    }

    /** 微信唯一id */
    private String openid;

    /** 微信昵称 */
    private String nickName;

    /** 性别 0：未知、1：男、2：女 */
    private Integer gender;

    /** 头像url */
    private String avatarUrl;

    /** 国家 */
    private String country;

    /** 省份 */
    private String province;

    /** 城市 */
    private String city;

    private List<RoleInfo> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<RoleInfo> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleInfo> roles) {
        this.roles = roles;
    }
}
