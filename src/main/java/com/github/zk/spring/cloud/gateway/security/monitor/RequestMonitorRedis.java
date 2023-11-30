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

package com.github.zk.spring.cloud.gateway.security.monitor;

import com.github.zk.spring.cloud.gateway.security.dao.RequestMonitorMapper;
import com.github.zk.spring.cloud.gateway.security.pojo.RequestStatisticInfo;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 请求监控 Redis 实现
 *
 * @author zk
 * @date 2023/11/24 16:22
 */
public class RequestMonitorRedis extends AbstractRequestMonitor {

    private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    public static final String SUCCESS_KEY = "requestSuccess";
    public static final String FAIL_KEY = "requestFail";
    public static final String RESPONSE_DURATION = "responseDuration";

    public RequestMonitorRedis(RequestMonitorMapper requestMonitorMapper,
                               ReactiveStringRedisTemplate reactiveStringRedisTemplate) {
        super(requestMonitorMapper);
        this.reactiveStringRedisTemplate = reactiveStringRedisTemplate;
    }

    @Override
    public Mono<List<RequestStatisticInfo>> queryRequestStatistic() {
        ReactiveHashOperations<String, String, String> hashOperations = reactiveStringRedisTemplate.opsForHash();
        // 成功请求 Flux
        Flux<Map.Entry<String, String>> successFlux =
                hashOperations.entries(SUCCESS_KEY);
        // 失败请求 Flux
        Flux<Map.Entry<String, String>> failFlux =
                hashOperations.entries(FAIL_KEY);
        // 请求的响应时长 Flux
        Flux<Map.Entry<String, String>> responseDurationFlux =
                hashOperations.entries(RESPONSE_DURATION);

        // 取3个 Flux 中 Map key 的并集
        return Flux.concat(successFlux, failFlux, responseDurationFlux)
                .map(Map.Entry::getKey)
                .distinct()
                .flatMap(urlPath -> {
                    Mono<String> successMono = hashOperations.get(SUCCESS_KEY, urlPath).defaultIfEmpty("0");
                    Mono<String> failMono = hashOperations.get(FAIL_KEY, urlPath).defaultIfEmpty("0");
                    Mono<String> responseDurationMono =
                            hashOperations.get(RESPONSE_DURATION, urlPath).defaultIfEmpty("0");
                    // 合并3个 Mono 数据，将数据合并到 RequestStatisticInfo 对象中
                    return Mono.zip(successMono, failMono, responseDurationMono)
                            .map(tuple -> {
                                // 请求成功次数
                                long successNum = Long.parseLong(tuple.getT1());
                                // 请求失败次数
                                long failNum = Long.parseLong(tuple.getT2());
                                // 成功率
                                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                                double successRate = (double) successNum / (double) (successNum + failNum) * 100;
                                String successRateStr = decimalFormat.format(successRate) + "%";
                                // 平均响应时间
                                int responseDurationAvg;
                                if (successNum == 0) {
                                    responseDurationAvg = 0;
                                } else {
                                    responseDurationAvg = (int) (Long.parseLong(tuple.getT3()) / successNum);
                                }
                                return new RequestStatisticInfo(urlPath, successNum,
                                        Long.parseLong(tuple.getT2()), successRateStr, responseDurationAvg);
                            });
                }).collectList();
    }

    /**
     * 统计请求成功次数
     *
     * @param urlPath url路径
     */
    @Override
    public void requestSuccessStatistic(String urlPath) {
        reactiveStringRedisTemplate.opsForHash()
                .increment(SUCCESS_KEY, urlPath, 1L)
                .subscribe();
    }

    /**
     * 统计请求失败次数
     *
     * @param urlPath url路径
     */
    @Override
    public void requestFailStatistic(String urlPath) {
        reactiveStringRedisTemplate.opsForHash()
                .increment(FAIL_KEY, urlPath, 1L)
                .subscribe();
    }

    /**
     * 统计请求成功响应时长
     *
     * @param urlPath url路径
     */
    @Override
    public void requestResponseDurationStatistic(String urlPath, long responseDuration) {
        reactiveStringRedisTemplate.opsForHash()
                .increment(RESPONSE_DURATION, urlPath, responseDuration)
                .subscribe();
    }
}
