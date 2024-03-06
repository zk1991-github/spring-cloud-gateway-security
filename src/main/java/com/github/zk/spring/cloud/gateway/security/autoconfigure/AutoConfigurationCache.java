/*
 *
 *  * Copyright 2021-2024 the original author or authors.
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

package com.github.zk.spring.cloud.gateway.security.autoconfigure;

import com.github.zk.spring.cloud.gateway.security.core.GatewaySecurityCache;
import com.github.zk.spring.cloud.gateway.security.core.LoginProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.session.DefaultWebSessionManager;
import org.springframework.web.server.session.WebSessionManager;
import org.springframework.web.server.session.WebSessionStore;

import java.time.Duration;

/**
 * 缓存自动配置
 *
 * @author zk
 * @since 4.3.0
 */
@Configuration
public class AutoConfigurationCache {

    @Value("${spring.cloud.gateway.session.maxSessions: -1}")
    private int maxSessions;
    @Value("${spring.cloud.gateway.session.lockRecord:#{null }}")
    private Integer lockRecord;
    @Value("${spring.cloud.gateway.session.lockedTime:PT10S}")
    private Duration lockedTime;

    /**
     * 注入登录处理器 bean
     *
     * @param gatewaySecurityCache 网关鉴权缓存器
     * @param webSessionManager    会话管理
     * @return 登录处理器 bean
     */
    @Bean
    public LoginProcessor loginProcessor(GatewaySecurityCache gatewaySecurityCache,
                                         WebSessionManager webSessionManager) {
        DefaultWebSessionManager defaultWebSessionManager = (DefaultWebSessionManager) webSessionManager;
        WebSessionStore sessionStore = defaultWebSessionManager.getSessionStore();
        LoginProcessor loginProcessor = new LoginProcessor(gatewaySecurityCache, sessionStore);
        loginProcessor.setMaxSessions(maxSessions);
        if (lockRecord != null) {
            loginProcessor.setAllowPasswordErrorRecord(lockRecord);
        }
        loginProcessor.setLockedTime(lockedTime);
        return loginProcessor;
    }
}
