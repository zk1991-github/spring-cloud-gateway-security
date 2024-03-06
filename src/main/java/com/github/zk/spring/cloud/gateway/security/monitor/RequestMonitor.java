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

import com.github.zk.spring.cloud.gateway.security.pojo.RequestMonitorInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.RequestStatisticInfo;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 请求监控接口
 *
 * @author zk
 * @since 4.2.0
 */
public interface RequestMonitor {
    /**
     * 存储请求信息
     *
     * @param requestMonitorInfo 请求信息
     */
    void saveRequestInfo(RequestMonitorInfo requestMonitorInfo);

    /**
     * 查询统计结果
     *
     * @return 统计结果
     */
    Mono<List<RequestStatisticInfo>> queryRequestStatistic();

    /**
     * 统计请求成功次数
     *
     * @param urlPath url路径
     */
    void requestSuccessStatistic(String urlPath);

    /**
     * 统计请求失败次数
     *
     * @param urlPath url路径
     */
    void requestFailStatistic(String urlPath);

    /**
     * 统计请求成功响应时长
     *
     * @param urlPath url路径
     */
    void requestResponseDurationStatistic(String urlPath, long responseDuration);
}
