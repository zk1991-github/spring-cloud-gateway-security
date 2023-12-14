/*
 *
 *  * Copyright 2021-2023 the original author or authors.
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
import com.github.zk.spring.cloud.gateway.security.core.GatewaySecurityCacheMap;
import com.github.zk.spring.cloud.gateway.security.dao.RequestMonitorMapper;
import com.github.zk.spring.cloud.gateway.security.monitor.RequestMonitor;
import com.github.zk.spring.cloud.gateway.security.monitor.RequestMonitorMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.session.DefaultWebSessionManager;
import org.springframework.web.server.session.InMemoryWebSessionStore;
import org.springframework.web.server.session.WebSessionManager;

/**
 * Map 缓存自动配置
 *
 * @author zk
 * @date 2023/11/15 9:57
 */
@Configuration
@ConditionalOnMissingBean(AutoConfigurationRedisCache.class)
public class AutoConfigurationMapCache {

    @Bean
    public GatewaySecurityCache gatewaySecurityCacheMap(WebSessionManager webSessionManager) {
        DefaultWebSessionManager defaultWebSessionManager = (DefaultWebSessionManager) webSessionManager;
        InMemoryWebSessionStore webSessionStore = (InMemoryWebSessionStore) defaultWebSessionManager.getSessionStore();
        return new GatewaySecurityCacheMap(webSessionStore);
    }

    @Bean
    public RequestMonitor requestMonitorMap(RequestMonitorMapper requestMonitorMapper) {
        return new RequestMonitorMap(requestMonitorMapper);
    }

}
