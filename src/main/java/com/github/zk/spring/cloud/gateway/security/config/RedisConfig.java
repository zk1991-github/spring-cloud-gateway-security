package com.github.zk.spring.cloud.gateway.security.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
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

//    @Bean
//    public ReactiveRedisTemplate reactiveRedisTemplate(LettuceConnectionFactory connectionFactory) {
//        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
//        RedisSerializationContext<String, String> serializationContext = RedisSerializationContext
//                .newSerializationContext().key(jackson2JsonRedisSerializer).value(jackson2JsonRedisSerializer).hashKey(jackson2JsonRedisSerializer)
//                .hashValue(jackson2JsonRedisSerializer).build();
//        return new ReactiveRedisTemplate(connectionFactory, serializationContext);
//    }

//    @Bean("springSessionDefaultRedisSerializer")
//    public RedisSerializer defaultRedisSerialzer() {
//        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
//        return jackson2JsonRedisSerializer;
//    }

//    @Bean
//    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new CoreJackson2Module());
//        return new GenericJackson2JsonRedisSerializer(objectMapper);
//    }

}
