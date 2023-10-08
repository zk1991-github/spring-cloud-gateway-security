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

import com.github.zk.spring.cloud.gateway.security.pojo.RequestStatisticInfo;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 请求统计抽象类
 *
 * @author zk
 * @date 2023/9/15 10:11
 */
public abstract class AbstractRequestMonitor implements RequestMonitor {

    private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    public static final String SUCCESS_KEY = "requestSuccess";
    public static final String FAIL_KEY = "requestFail";
    public static final String RESPONSE_DURATION = "responseDuration";

    public AbstractRequestMonitor(ReactiveStringRedisTemplate reactiveStringRedisTemplate) {
        this.reactiveStringRedisTemplate = reactiveStringRedisTemplate;
    }

    @Override
    public Mono<List<RequestStatisticInfo>> queryRequestStatistic() {
        ReactiveHashOperations<String, String, String> hashOperations = reactiveStringRedisTemplate.opsForHash();
        Flux<Map.Entry<String, String>> successFlux =
                hashOperations.entries(RequestMonitorStatistic.SUCCESS_KEY);

        return successFlux.flatMap(successEntry -> {
            String urlPath = successEntry.getKey();
            long successNum = Long.parseLong(successEntry.getValue());

            Mono<String> failMono = hashOperations.get(RequestMonitorStatistic.FAIL_KEY, urlPath);
            Mono<String> responseDurationMono =
                    hashOperations.get(RequestMonitorStatistic.RESPONSE_DURATION, urlPath);

            return Mono.zip(failMono, responseDurationMono)
                    .map(tuple -> {
                        // 请求失败次数
                        long failNum = Long.parseLong(tuple.getT1());
                        // 成功率
                        DecimalFormat decimalFormat = new DecimalFormat("0.00");
                        double successRate = (double) successNum / (double) (successNum + failNum) * 100;
                        String successRateStr = decimalFormat.format(successRate) + "%";
                        // 平均响应时间
                        int responseDurationAvg = (int) (Long.parseLong(tuple.getT2()) / successNum);
                        return new RequestStatisticInfo(urlPath, successNum,
                                Long.parseLong(tuple.getT1()), successRateStr, responseDurationAvg);
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
