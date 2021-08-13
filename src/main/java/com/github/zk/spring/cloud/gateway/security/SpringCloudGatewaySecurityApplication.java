package com.github.zk.spring.cloud.gateway.security;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.github.zk.spring.cloud.gateway.security.dao")
public class SpringCloudGatewaySecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudGatewaySecurityApplication.class, args);
    }

}
