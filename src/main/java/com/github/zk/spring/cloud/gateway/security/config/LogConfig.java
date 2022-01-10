package com.github.zk.spring.cloud.gateway.security.config;

import com.github.zk.spring.cloud.gateway.security.log.Log;
import com.github.zk.spring.cloud.gateway.security.log.LogHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 日志配置
 *
 * @author zk
 * @date 2022/1/6 13:24
 */
@Configuration
public class LogConfig {

    @Autowired(required = false)
    private Log log;

    /**
     * 日志记录开关
     */
    @Value("${log.enabled:false}")
    private boolean enabled;
    /**
     * 注入日志持有 bean
     * @return 日志持有 bean
     */
    @Bean
    public LogHolder logHolder() {
        LogHolder logHolder = new LogHolder();
        logHolder.setEnabled(enabled);
        if (log != null) {
            logHolder.setLog(log);
        }
        return logHolder;
    }

    /**
     * 注入日志实现 bean
     * @return 日志实现
     */
//    @Bean
//    public static Log log() {
//        return new RepositoryLog();
//    }
}
