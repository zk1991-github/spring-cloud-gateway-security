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

package com.github.zk.spring.cloud.gateway.security.monitor;

import com.github.zk.spring.cloud.gateway.security.dao.RequestMonitorMapper;
import com.github.zk.spring.cloud.gateway.security.pojo.RequestStatisticInfo;
import reactor.core.publisher.Mono;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 请求监控Map实现
 *
 * @author zk
 * @since 4.2.0
 */
public class RequestMonitorMap extends AbstractRequestMonitor {

    /**
     * 成功请求缓存
     */
    private final Map<String, AtomicInteger> successRequestMap = new ConcurrentHashMap<>();
    /**
     * 失败请求缓存
     */
    private final Map<String, AtomicInteger> failRequestMap = new ConcurrentHashMap<>();
    /**
     * 响应时长缓存
     */
    private final Map<String, AtomicInteger> responseDurationMap = new ConcurrentHashMap<>();

    public RequestMonitorMap(RequestMonitorMapper requestMonitorMapper) {
        super(requestMonitorMapper);
    }

    @Override
    public Mono<List<RequestStatisticInfo>> queryRequestStatistic() {
        List<RequestStatisticInfo> list = new ArrayList<>();
        Set<String> keyUnion = getKeyUnion();
        for (String key : keyUnion) {
            RequestStatisticInfo rsi = new RequestStatisticInfo();
            // 成功次数
            long successNum = successRequestMap.get(key).longValue();
            rsi.setSuccessNum(successNum);
            // 失败次数
            long failNum = failRequestMap.get(key).longValue();
            rsi.setFailNum(failNum);
            // 平均响应时间
            long responseDurationNum = responseDurationMap.get(key).longValue();
            if (successNum == 0) {
                rsi.setResponseDurationAvg(0);
            } else {
                rsi.setResponseDurationAvg((int) (responseDurationNum / successNum));
            }
            // 成功率
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            double successRate = (double) successNum / (double) (successNum + failNum) * 100;
            String successRateStr = decimalFormat.format(successRate) + "%";
            rsi.setSuccessRate(successRateStr);
            list.add(rsi);
        }
        return Mono.just(list);
    }

    @Override
    public void requestSuccessStatistic(String urlPath) {
        successRequestMap.computeIfAbsent(urlPath, k -> new AtomicInteger(0)).incrementAndGet();
    }

    @Override
    public void requestFailStatistic(String urlPath) {
        failRequestMap.computeIfAbsent(urlPath, k -> new AtomicInteger(0)).incrementAndGet();
    }

    @Override
    public void requestResponseDurationStatistic(String urlPath, long responseDuration) {
        responseDurationMap.computeIfAbsent(urlPath, k -> new AtomicInteger(0)).addAndGet((int) responseDuration);
    }

    /**
     * 合并 Map 的 key
     *
     * @return 合并的 key
     */
    private Set<String> getKeyUnion() {
        Set<String> successSet = successRequestMap.keySet();
        Set<String> failSet = failRequestMap.keySet();

        Set<String> keyUnion = new HashSet<>();
        keyUnion.addAll(successSet);
        keyUnion.addAll(failSet);

        return keyUnion;
    }
}
