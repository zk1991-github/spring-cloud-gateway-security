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

package com.github.zk.spring.cloud.gateway.security.service.impl;

import com.github.zk.spring.cloud.gateway.security.pojo.WhitelistInfo;
import com.github.zk.spring.cloud.gateway.security.service.IWhitelist;
import com.github.zk.spring.cloud.gateway.security.service.IWhitelistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 白名单实现类
 *
 * @author zhaokai
 * @since 5.0.0-1
 */
@Service
public class WhitelistImpl implements IWhitelist {

    @Autowired
    private IWhitelistService whitelistService;

    @Override
    public Mono<Boolean> isWhiteList(String ip, String macAddr) {
        WhitelistInfo w = whitelistService.queryByIpAndMac(ip, macAddr);
        return Mono.just(w != null);
    }

    @Override
    public Mono<Boolean> isIpWhiteList(String ip) {
        WhitelistInfo w = whitelistService.queryByIpOnly(ip);
        return Mono.just(w != null);
    }

    @Override
    public Mono<Boolean> isMacWhiteList(String macAddr) {
        WhitelistInfo w = whitelistService.queryByMacOnly(macAddr);
        return Mono.just(w != null);
    }
}
