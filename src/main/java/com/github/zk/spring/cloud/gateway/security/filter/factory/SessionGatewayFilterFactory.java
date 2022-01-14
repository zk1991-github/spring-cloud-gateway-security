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

package com.github.zk.spring.cloud.gateway.security.filter.factory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;

import java.time.Duration;

/**
 * Session 设置拦截
 *
 * @author zk
 * @date 2021/8/16 11:04
 */
public class SessionGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    @Value("${spring.cloud.gateway.session.timeout}")
    private long timeout;

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) ->
                exchange.getSession()
                        //修改session时间
                        .doOnNext(webSession -> webSession.setMaxIdleTime(Duration.ofMinutes(timeout)))
                        .then(chain.filter(exchange));

    }
}
