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

package com.github.zk.spring.cloud.gateway.security.controller;

import com.github.zk.spring.cloud.gateway.security.common.Response;
import com.github.zk.spring.cloud.gateway.security.util.PasswordGeneratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRepository;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 生成器 请求控制
 *
 * @author zk
 * @since 4.3.2
 */
@RestController
@RequestMapping("/gateway")
public class GeneratorController {

    @Autowired
    private ServerCsrfTokenRepository serverCsrfTokenRepository;
    @GetMapping("/passwordGenerator")
    public Response passwordGenerator(@RequestParam("password") String password) {
        if (ObjectUtils.isEmpty(password)) {
            return Response.setError();
        } else {
            String encodePassword = PasswordGeneratorUtils.encodePassword(password);
            return Response.setOk(encodePassword);
        }
    }

    @GetMapping("/csrfTokenGenerator")
    public Mono<Response> csrfTokenGenerator(ServerWebExchange exchange) {
        return serverCsrfTokenRepository.generateToken(exchange)
                .delayUntil((token) -> serverCsrfTokenRepository.saveToken(exchange, token))
                .cache().map(csrfToken -> Response.setOk());
    }
}
