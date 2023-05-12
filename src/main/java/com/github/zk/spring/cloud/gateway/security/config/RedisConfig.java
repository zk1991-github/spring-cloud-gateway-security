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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zk.spring.cloud.gateway.security.jackson2.CustomCoreJackson2Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;

/**
 * Redis 配置
 *
 * @author zk
 * @date 2021/2/7 11:13
 */
@Configuration
@EnableRedisWebSession
public class RedisConfig {

//    使用 redis 默认配置方式，内置 Sentinel、Cluster、Standalone三种模式
//    @see org.springframework.boot.autoconfigure.data.redis.LettuceConnectionConfiguration
//    /**
//     * 注入连接工厂 bean
//     * @param properties redis 配置
//     * @return 连接工厂
//     */
//    @Bean
//    public LettuceConnectionFactory connectionFactory(RedisProperties properties) {
//        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
//        String hostName = properties.getHost();
//        int port = properties.getPort();
//        String password = properties.getPassword();
//        redisConfiguration.setPassword(RedisPassword.of(password));
//        redisConfiguration.setHostName(hostName);
//        redisConfiguration.setPort(port);
//        return new LettuceConnectionFactory(redisConfiguration);
//    }

    /**
     * 注入序列化bean
     * @return 序列化对象
     */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new CustomCoreJackson2Module());
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

}
