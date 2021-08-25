package com.github.zk.spring.cloud.gateway.security.util;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;

/**
 * 密码生成器 工具类
 *
 * @author zk
 * @date 2021/2/24 9:30
 */
public class PasswordGeneratorUtils {

    public static String encodePassword(String password) {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(password);
    }

    public static void main(String[] args) {
        System.out.println(PasswordGeneratorUtils.encodePassword("123456"));
    }
}
