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

package com.github.zk.spring.cloud.gateway.security.handler;

import com.github.zk.spring.cloud.gateway.security.log.LogHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.WebSessionServerLogoutHandler;
import reactor.core.publisher.Mono;

/**
 * 用户登出处理器
 *
 * @author zk
 * @since 1.0
 */
public class LogoutHandler extends WebSessionServerLogoutHandler {

    private final LogHolder logHolder;

    public LogoutHandler(LogHolder logHolder) {
        this.logHolder = logHolder;
    }
    @Override
    public Mono<Void> logout(WebFilterExchange exchange, Authentication authentication) {
        if (authentication != null) {
            logHolder.loginLog(
                    exchange.getExchange(),
                    authentication.getPrincipal(),
                    1,
                    "登出成功"
            );
        }
        return super.logout(exchange, authentication);
    }
}
