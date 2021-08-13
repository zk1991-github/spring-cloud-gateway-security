package com.github.zk.spring.cloud.gateway.security.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.zk.spring.cloud.gateway.security.pojo.PermissionInfo;
import org.springframework.stereotype.Repository;

/**
 * 权限仓储
 *
 * @author zk
 * @date 2021/2/25 15:37
 */
@Repository
public interface PermissionMapper extends BaseMapper<PermissionInfo> {
}
