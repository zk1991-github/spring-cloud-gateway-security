/*
 *
 *  * Copyright 2021-2022 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

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
