package com.github.zk.spring.cloud.gateway.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zk.spring.cloud.gateway.security.jackson2.CustomCoreJackson2Module;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
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


    @Bean
    public LettuceConnectionFactory connectionFactory(RedisProperties properties) {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        String hostName = properties.getHost();
        int port = properties.getPort();
        String password = properties.getPassword();
        redisConfiguration.setPassword(RedisPassword.of(password));
        redisConfiguration.setHostName(hostName);
        redisConfiguration.setPort(port);
        return new LettuceConnectionFactory(redisConfiguration);
    }

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new CustomCoreJackson2Module());
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

}
