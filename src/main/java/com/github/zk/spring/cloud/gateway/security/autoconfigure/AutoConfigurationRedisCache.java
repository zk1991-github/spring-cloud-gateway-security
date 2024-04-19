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
import com.github.zk.spring.cloud.gateway.security.core.GatewaySecurityCacheRedis;
import com.github.zk.spring.cloud.gateway.security.dao.RequestMonitorMapper;
import com.github.zk.spring.cloud.gateway.security.monitor.RequestMonitor;
import com.github.zk.spring.cloud.gateway.security.monitor.RequestMonitorRedis;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

/**
 * Redis 缓存自动配置
 *
 * @author zk
 * @since 4.3.0
 */
@Configuration
@ConditionalOnClass(ReactiveRedisTemplate.class)
public class AutoConfigurationRedisCache {

    @Bean
    public GatewaySecurityCache gatewaySecurityCacheRedis(ReactiveStringRedisTemplate reactiveStringRedisTemplate,
                                                          ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate,
                                                          ReactiveRedisConnectionFactory connectionFactory) {
        ReactiveRedisConnection reactiveRedisConnection = connectionFactory.getReactiveConnection();
        return new GatewaySecurityCacheRedis(reactiveStringRedisTemplate, reactiveRedisTemplate, reactiveRedisConnection);
    }

    @Bean
    public RequestMonitor requestMonitorRedis(RequestMonitorMapper requestMonitorMapper,
                                              ReactiveStringRedisTemplate reactiveStringRedisTemplate) {
        return new RequestMonitorRedis(requestMonitorMapper, reactiveStringRedisTemplate);
    }

}
