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

import com.github.zk.spring.cloud.gateway.security.property.SecurityProperties;
import com.github.zk.spring.cloud.gateway.security.service.IWhitelist;
import com.github.zk.spring.cloud.gateway.security.util.IpUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 白名单实现类
 *
 * @author zhaokai
 * @since 5.1.0
 */
@Service
public class WhitelistImpl implements IWhitelist {

    private final SecurityProperties securityProperties;

    public WhitelistImpl(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    public Mono<Boolean> isWhiteList(String ip) {
        return Mono.just(ipMatch(ip));
    }

    private boolean ipMatch(String ip) {
        SecurityProperties.Whitelist whitelist = securityProperties.getWhitelist();
        if (ip != null && !ip.isEmpty() && !whitelist.getIps().isEmpty()) {
            for (String pattern : whitelist.getIps()) {
                if (IpUtils.isIpInRange(ip, pattern)) {
                    return true;
                }
            }
        }
        return false;
    }
}
