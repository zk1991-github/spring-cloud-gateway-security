package com.github.zk.spring.cloud.gateway.security.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.zk.spring.cloud.gateway.security.pojo.UserInfo;
import org.springframework.stereotype.Repository;

/**
 * 用户仓库
 *
 * @author zk
 * @date 2021/2/8 18:23
 */
@Repository
public interface UserMapper extends BaseMapper<UserInfo> {

    UserInfo selectUser(UserInfo userInfo);
}
