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

package com.github.zk.spring.cloud.gateway.security.authentication;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationFailureHandler;
import reactor.core.publisher.Mono;

/**
 * 自定义登录失败处理器
 *
 * @author zk
 * @date 2021/9/7 11:33
 */
public class WebRedirectServerAuthenticationFailureHandler extends RedirectServerAuthenticationFailureHandler {


    /**
     * Creates an instance
     *
     * @param location the location to redirect to (i.e. "/login?failed")
     */
    public WebRedirectServerAuthenticationFailureHandler(String location) {
        super(location);
    }

    /**
     * 认证失败
     *
     *     非跳转写法
     *     @Override
     *     public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
     *         Response response = Response.getInstance();
     *         ServerHttpResponse serverHttpResponse = webFilterExchange.getExchange().getResponse();
     *         serverHttpResponse.setStatusCode(HttpStatus.OK);
     *         serverHttpResponse.getHeaders().set("Content-Type", "application/json");
     *         response.setError(9000, null, exception.getMessage());
     *         ObjectMapper objectMapper = new ObjectMapper();
     *         String body = "";
     *         try {
     *             body = objectMapper.writeValueAsString(response);
     *
     *         } catch (JsonProcessingException e) {
     *             e.printStackTrace();
     *         }
     *         DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
     *         return serverHttpResponse.writeWith(Mono.just(dataBuffer));
     *     }
     * @param webFilterExchange web请求
     * @param exception 异常
     * @return
     */
    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        return webFilterExchange.getExchange().getSession().doOnNext(webSession -> {
            webSession.getAttributes().put(WebAttributes.AUTHENTICATION_EXCEPTION, exception.getMessage());
        }).then(super.onAuthenticationFailure(webFilterExchange, exception));
    }


}
