package com.github.zk.spring.cloud.gateway.security.log;

import com.github.zk.spring.cloud.gateway.security.pojo.LogInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 控制台显示日志
 *
 * @author zk
 * @date 2022/1/5 17:35
 */
public class ControlLog implements Log {

    private final Logger logger = LoggerFactory.getLogger(Log.class);

    @Override
    public void loginLog(LogInfo logInfo) {
        logger.info("用户id【{}】，用户名【{}】，ip【{}】，登录状态【{}】，登录信息【{}】，时间【{}】",
                logInfo.getId(), logInfo.getUsername(),
                logInfo.getIp(), logInfo.getStatus(),
                logInfo.getMsg(), logInfo.getTime());
    }
}
