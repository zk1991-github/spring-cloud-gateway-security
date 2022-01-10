package com.github.zk.spring.cloud.gateway.security.log;

import com.github.zk.spring.cloud.gateway.security.dao.LogMapper;
import com.github.zk.spring.cloud.gateway.security.pojo.LogInfo;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 日志仓储存储实现
 *
 * @author zk
 * @date 2022/1/6 15:01
 */
public class RepositoryLog implements Log {

    @Autowired
    private LogMapper logMapper;

    @Override
    public void loginLog(LogInfo logInfo) {
        logMapper.insert(logInfo);
    }
}
