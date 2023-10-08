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

package com.github.zk.spring.cloud.gateway.security.util;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;

/**
 * 密码生成器 工具类
 *
 * @author zk
 * @date 2021/2/24 9:30
 */
public class PasswordGeneratorUtils {

    /**
     * 密码加密
     * @param password 待加密密码
     * @return 加密后的密码
     */
    public static String encodePassword(String password) {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(password);
    }

    public static void main(String[] args) {
        System.out.println(PasswordGeneratorUtils.encodePassword("123456"));
    }
}
