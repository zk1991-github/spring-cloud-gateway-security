package com.github.zk.spring.cloud.gateway.security.log;

import com.github.zk.spring.cloud.gateway.security.pojo.LogInfo;

/**
 * 日志接口
 *
 * @author zk
 * @date 2022/1/5 14:03
 */
public interface Log {

    /**
     * web 登录日志
     * @param logInfo 日志信息
     */
    void loginLog(LogInfo logInfo);
}
