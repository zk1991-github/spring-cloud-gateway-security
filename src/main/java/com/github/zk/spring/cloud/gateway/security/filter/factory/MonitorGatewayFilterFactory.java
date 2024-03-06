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

import com.github.zk.spring.cloud.gateway.security.monitor.RequestMonitor;
import com.github.zk.spring.cloud.gateway.security.pojo.RequestMonitorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 请求监控拦截器
 *
 * @author zk
 * @since 4.2.0
 */
public class MonitorGatewayFilterFactory extends AbstractGatewayFilterFactory<MonitorGatewayFilterFactory.Config> {

    private final Logger logger = LoggerFactory.getLogger(MonitorGatewayFilterFactory.class);

    private final RequestMonitor requestMonitor;

    public MonitorGatewayFilterFactory(RequestMonitor requestMonitor) {
        super(Config.class);
        this.requestMonitor = requestMonitor;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // 请求开始时间
            long startTime = System.currentTimeMillis();

            String path = exchange.getRequest().getURI().getPath();
            RequestMonitorInfo requestMonitorInfo = new RequestMonitorInfo();
            requestMonitorInfo.setUrlPath(path);
            // 获取当前时间
            LocalDateTime localDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formatDateTime = formatter.format(localDateTime);
            requestMonitorInfo.setRequestTime(formatDateTime);
            return chain.filter(exchange)
                    .doOnSuccess(aVoid -> {
                        // 计算响应时间
                        long endTime = System.currentTimeMillis();
                        long responseDuration = endTime - startTime;
                        // 存储请求日志
                        requestMonitorInfo.setStatus(HttpStatus.OK.value());
                        requestMonitorInfo.setResponseDuration(responseDuration);
                        requestMonitorInfo.setExceptionDesc("服务正常");
                        requestMonitor.saveRequestInfo(requestMonitorInfo);
                        // 存储请求成功次数
                        requestMonitor.requestSuccessStatistic(path);
                        // 存储响应时间
                        requestMonitor.requestResponseDurationStatistic(path, responseDuration);
                    }).onErrorResume(ex -> {
                        // 异常处理逻辑
                        long endTime = System.currentTimeMillis();
                        long responseDuration = endTime - startTime;

                        // 设置响应时间
                        requestMonitorInfo.setResponseDuration(responseDuration);
                        // 存储请求失败次数
                        requestMonitor.requestFailStatistic(path);
                        // 返回一个自定义的响应
                        return handleError(exchange, ex, requestMonitorInfo);
                    });
        };
    }

    /**
     * 异常处理
     *
     * @param exchange 请求
     * @param ex 异常
     * @param requestMonitorInfo 请求监控实体
     * @return Void
     */
    private Mono<Void> handleError(ServerWebExchange exchange, Throwable ex,
                                   RequestMonitorInfo requestMonitorInfo) {
        HttpStatus status;
        String body;
        if (ex instanceof WebExchangeBindException) {
            // 如果是表单绑定异常，返回错误信息给上游请求
            status = HttpStatus.BAD_REQUEST;
            body = "无效的请求数据: " + ex.getMessage();
        } else {
            // 其他异常，返回默认的错误信息给上游请求
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            body = "目标服务异常，请稍后重试.";
        }
        // 打印日志
        logger.error(body);
        // 存储日志
        requestMonitorInfo.setStatus(status.value());
        requestMonitorInfo.setExceptionDesc(body);
        requestMonitor.saveRequestInfo(requestMonitorInfo);
        return writeErrorResponse(exchange, status, body);
    }

    /**
     * 写入失败响应
     *
     * @param exchange 请求
     * @param status 状态码
     * @param body 失败描述信息
     * @return Void
     */
    private Mono<Void> writeErrorResponse(ServerWebExchange exchange, HttpStatus status, String body) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Flux.just(buffer));
    }

    static class Config {
    }
}
