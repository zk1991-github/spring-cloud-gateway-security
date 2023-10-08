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
import com.github.zk.spring.cloud.gateway.security.pojo.RequestMonitorInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 请求监控存储到数据库实现
 *
 * @author zk
 * @date 2023/9/13 15:46
 */
@Service
public class RequestMonitorToDB extends AbstractRequestMonitor {

    @Autowired
    private RequestMonitorMapper requestMonitorMapper;

    public RequestMonitorToDB(ReactiveStringRedisTemplate reactiveStringRedisTemplate) {
        super(reactiveStringRedisTemplate);
    }

    @Override
    public void saveRequestInfo(RequestMonitorInfo requestMonitorInfo) {
        requestMonitorMapper.insert(requestMonitorInfo);
    }
}
