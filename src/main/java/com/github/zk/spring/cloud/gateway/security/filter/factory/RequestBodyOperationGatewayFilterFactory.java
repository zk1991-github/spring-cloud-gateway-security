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

package com.github.zk.spring.cloud.gateway.security.filter.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * 修改请求体
 *
 * @author zk
 * @since 3.6
 */
public class RequestBodyOperationGatewayFilterFactory extends AbstractGatewayFilterFactory<RequestBodyOperationGatewayFilterFactory.Config> {

    /**
     * 前端传入密码的 key
     */
    private String passwordKey = "password";

    /**
     * 设置前端输入的密码 key
     *
     * @param passwordKey 密码 key
     */
    public void setPasswordKey(String passwordKey) {
        this.passwordKey = passwordKey;
    }

    public RequestBodyOperationGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (request.getMethod() != HttpMethod.POST) {
                return chain.filter(exchange);
            }
            return operationExchange(exchange, chain);
        };
    }

    private Mono<Void> operationExchange(ServerWebExchange exchange, GatewayFilterChain chain) {
        // mediaType
        MediaType mediaType = exchange.getRequest().getHeaders().getContentType();
        // 判断类型不为json直接转发下游节点
        if (!MediaType.APPLICATION_JSON.isCompatibleWith(mediaType)) {
            return chain.filter(exchange);
        }
        // read & modify body
        ServerRequest serverRequest = ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());
        Mono<String> modifiedBody = serverRequest.bodyToMono(String.class)
                .map(this::modifyBody);
        BodyInserter<Mono<String>, ReactiveHttpOutputMessage> bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(exchange.getRequest().getHeaders());
        headers.remove(HttpHeaders.CONTENT_LENGTH);
        CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
        return bodyInserter.insert(outputMessage, new BodyInserterContext())
                .then(Mono.defer(() -> {
                    ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(
                            exchange.getRequest()) {
                        @Override
                        public HttpHeaders getHeaders() {
                            long contentLength = headers.getContentLength();
                            HttpHeaders httpHeaders = new HttpHeaders();
                            httpHeaders.putAll(super.getHeaders());
                            if (contentLength > 0) {
                                httpHeaders.setContentLength(contentLength);
                            } else {
                                httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                            }
                            return httpHeaders;
                        }

                        @Override
                        public Flux<DataBuffer> getBody() {
                            return outputMessage.getBody();
                        }
                    };
                    return chain.filter(exchange.mutate().request(decorator).build());
                }));
    }

    /**
     * 修改请求体
     *
     * @param body 请求体
     * @return 修改后的请求体
     */
    private String modifyBody(String body) {
        ObjectMapper objectMapper = new ObjectMapper();
        Object bodyObject;
        Map<String, String> bodyMap;
        String modifiedBody = "";
        try {
            // 有些头信息是json，传参不为json格式，兼容这种情况
            bodyObject = objectMapper.readValue(body, Object.class);
            if (bodyObject instanceof Map) {
                // 如果是json格式，转换为Map
                bodyMap = (Map<String, String>) bodyObject;
                // 如果不包含密码，则直接返回
                if (!bodyMap.containsKey(passwordKey)) {
                    return body;
                }
                String presentedPassword = bodyMap.get(passwordKey);
                String encodedPassword = PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(presentedPassword);
                bodyMap.put(passwordKey, encodedPassword);
                modifiedBody = objectMapper.writeValueAsString(bodyMap);
            } else {
                return body;
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return modifiedBody;
    }



    static class Config {
    }
}