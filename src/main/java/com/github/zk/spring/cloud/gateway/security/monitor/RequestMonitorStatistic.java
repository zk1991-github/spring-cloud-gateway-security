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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 请求监控统计
 *
 * @author zk
 * @date 2023/9/14 15:02
 */
@Component
public class RequestMonitorStatistic {

    public static final String SUCCESS_KEY = "requestSuccess";
    public static final String FAIL_KEY = "requestFail";
    public static final String RESPONSE_DURATION = "responseDuration";

    @Autowired
    private ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    /**
     * 统计请求成功次数
     *
     * @param urlPath url路径
     */
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
    public void requestResponseDurationStatistic(String urlPath, long responseDuration) {
        reactiveStringRedisTemplate.opsForHash()
                .increment(RESPONSE_DURATION, urlPath, responseDuration)
                .subscribe();
    }
}
