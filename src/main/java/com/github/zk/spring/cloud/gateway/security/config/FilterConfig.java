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

import com.github.zk.spring.cloud.gateway.security.filter.factory.RequestBodyOperationGatewayFilterFactory;
import com.github.zk.spring.cloud.gateway.security.filter.factory.SessionGatewayFilterFactory;
import com.github.zk.spring.cloud.gateway.security.filter.factory.TokenCheckGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 拦截配置
 * @author zk
 * @date 2019/6/5 9:56
 */
@Configuration
public class FilterConfig {

    /**
     * 注入token拦截 bean
     * @return token拦截工厂
     */
    @Bean
    public TokenCheckGatewayFilterFactory getTokenCheck() {
        return new TokenCheckGatewayFilterFactory();
    }

    /**
     * 注入session拦截 bean
     * @return session拦截工厂
     */
    @Bean
    public SessionGatewayFilterFactory sessionGatewayFilterFactory() {
        return new SessionGatewayFilterFactory();
    }

    /**
     * 修改请求体拦截 bean
     * @return 修改请求体工厂
     */
    @Bean
    public RequestBodyOperationGatewayFilterFactory requestBodyOperationFilterFactory() {
        return new RequestBodyOperationGatewayFilterFactory();
    }
}
