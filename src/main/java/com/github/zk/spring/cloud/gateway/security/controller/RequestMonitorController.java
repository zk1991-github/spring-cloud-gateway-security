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

import com.github.zk.spring.cloud.gateway.security.common.CodeEnum;
import com.github.zk.spring.cloud.gateway.security.common.Response;
import com.github.zk.spring.cloud.gateway.security.monitor.RequestMonitor;
import com.github.zk.spring.cloud.gateway.security.pojo.RequestStatisticInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 请求监控 控制
 *
 * @author zk
 * @since 4.2.0
 */
@RestController
@RequestMapping("/gateway")
public class RequestMonitorController {

    @Autowired
    private RequestMonitor requestMonitor;

    @GetMapping("/queryRequestStatistic")
    public Mono<Response> queryRequestStatistic() {
        Mono<List<RequestStatisticInfo>> monoList = requestMonitor.queryRequestStatistic();
        return monoList.map(requestStatisticInfos -> {
            if (requestStatisticInfos != null) {
                return Response.setOk(requestStatisticInfos);
            } else {
                return Response.setError(CodeEnum.FAIL);
            }
        });

    }
}
