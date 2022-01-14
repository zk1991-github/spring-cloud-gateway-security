/*
 *
 *  * Copyright 2021-2022 the original author or authors.
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

import com.github.zk.spring.cloud.gateway.security.core.LoginProcessor;
import com.github.zk.spring.cloud.gateway.security.pojo.WeChatUserInfo;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 微信认证接口
 *
 * @author zk
 * @date 2021/11/17 11:17
 */
public interface IWeChatAuthentication {
    /**
     * 微信认证
     * @param weChatCode 微信code
     * @param weChatUserInfo 微信用户信息
     * @return 微信用户对象
     */
    Mono<WeChatUserInfo> authenticate(LoginProcessor loginProcessor, String weChatCode, WeChatUserInfo weChatUserInfo, ServerWebExchange exchange);
}
