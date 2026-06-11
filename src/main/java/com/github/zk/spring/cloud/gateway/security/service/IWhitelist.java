/*
 *
 *  * Copyright 2021-2026 the original author or authors.
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

package com.github.zk.spring.cloud.gateway.security.service;

import reactor.core.publisher.Mono;

/**
 * 白名单接口
 *
 * @author zhaokai
 * @since 5.1.0
 */
public interface IWhitelist {
    /**
     * 是否白名单
     *
     * @param ip ip地址
     * @return 是否为白名单
     */
    Mono<Boolean> isWhiteList(String ip);
}
