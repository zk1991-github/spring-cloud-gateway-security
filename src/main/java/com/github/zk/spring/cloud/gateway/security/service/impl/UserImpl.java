package com.github.zk.spring.cloud.gateway.security.service.impl;

import com.github.zk.spring.cloud.gateway.security.dao.UserMapper;
import com.github.zk.spring.cloud.gateway.security.pojo.UserInfo;
import com.github.zk.spring.cloud.gateway.security.property.LoginProperties;
import com.github.zk.spring.cloud.gateway.security.service.IUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;

/**
 * 用户接口实现
 *
 * @author zk
 * @date 2021/1/15 14:36
 */
@Service
public class UserImpl implements IUser, ReactiveUserDetailsService {

    @Autowired
    private LoginProperties properties;
    @Autowired
    private UserMapper userMapper;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        UserInfo userInfo = null;
        if (ObjectUtils.nullSafeEquals(properties.getUser().getUsername(), username)) {
            //超级管理员
            userInfo = properties.getUser();

            return Mono.just(userInfo);
        }
        UserInfo user = new UserInfo();
        user.setUsername(username);
        userInfo = userMapper.selectUser(user);
        if (userInfo == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return Mono.justOrEmpty(userInfo);
    }
}
