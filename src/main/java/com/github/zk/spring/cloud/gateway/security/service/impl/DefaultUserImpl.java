package com.github.zk.spring.cloud.gateway.security.service.impl;

import com.github.zk.spring.cloud.gateway.security.dao.PermissionMapper;
import com.github.zk.spring.cloud.gateway.security.dao.UserMapper;
import com.github.zk.spring.cloud.gateway.security.pojo.PermissionInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.UserInfo;
import com.github.zk.spring.cloud.gateway.security.property.LoginProperties;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;

/**
 * 用户接口实现
 *
 * @author zk
 * @date 2021/1/15 14:36
 */
public abstract class DefaultUserImpl implements ReactiveUserDetailsService {

    @Autowired
    private LoginProperties properties;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        UserInfo userInfo = null;
        if (ObjectUtils.nullSafeEquals(properties.getUser().getUsername(), username)) {
            //超级管理员
            userInfo = properties.getUser();
            userInfo.getRoles().get(0).setPermissionInfos(findAllPermissions());

            return Mono.just(userInfo);
        }
        userInfo = customFindByUsername(username);
        if (userInfo == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return Mono.justOrEmpty(userInfo);
    }

    private List<PermissionInfo> findAllPermissions() {
        List<PermissionInfo> permissionInfos = permissionMapper.selectList(null);
        return permissionInfosProcess(0, permissionInfos);
    }

    /**
     * 权限处理 （递归组织结构）
     * @param pid 父id
     * @param permissionInfos 权限信息
     * @return
     */
    private List<PermissionInfo> permissionInfosProcess(long pid, List<PermissionInfo> permissionInfos) {
        List<PermissionInfo> newPermissionInfos = new ArrayList<>();
        Iterator<PermissionInfo> iterator = permissionInfos.iterator();
        while (iterator.hasNext()) {
            PermissionInfo permissionInfo = iterator.next();
            if (pid == permissionInfo.getPid()) {
                // 添加父权限
                newPermissionInfos.add(permissionInfo);
                // 剔出本对象
                iterator.remove();
                // 查找子权限
                List<PermissionInfo> childPermissionInfos =
                        permissionInfosProcess(permissionInfo.getId(), permissionInfos);
                // 设置子权限
                permissionInfo.setPermissionInfos(childPermissionInfos);
            }
        }
        return newPermissionInfos;
    }

    /**
     * 子类可覆盖
     * @param username 用户名
     * @return
     */
    protected UserInfo customFindByUsername(String username) {
        UserInfo user = new UserInfo();
        user.setUsername(username);
        return userMapper.selectUser(user);
    }
}

