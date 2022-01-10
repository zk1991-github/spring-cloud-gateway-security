package com.github.zk.spring.cloud.gateway.security.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.zk.spring.cloud.gateway.security.pojo.RoleInfo;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * 角色仓储
 *
 * @author zk
 * @date 2021/12/7 10:45
 */
@Repository
public interface RoleMapper extends BaseMapper<RoleInfo> {

    /**
     * 根据id 查询角色
     * @param ids 角色id
     * @return 角色列表
     */
    List<RoleInfo> selectRolesByIds(Long[] ids);
}
