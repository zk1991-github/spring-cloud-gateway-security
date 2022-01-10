package com.github.zk.spring.cloud.gateway.security.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.zk.spring.cloud.gateway.security.pojo.LogInfo;
import org.springframework.stereotype.Repository;

/**
 * 日志仓储
 *
 * @author zk
 * @date 2022/1/6 15:04
 */
@Repository
public interface LogMapper extends BaseMapper<LogInfo> {
}
