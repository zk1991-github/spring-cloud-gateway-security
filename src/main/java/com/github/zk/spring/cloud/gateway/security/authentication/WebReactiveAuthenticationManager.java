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

package com.github.zk.spring.cloud.gateway.security.authentication;

import com.github.zk.spring.cloud.gateway.security.core.LoginProcessor;
import com.github.zk.spring.cloud.gateway.security.service.impl.DefaultUserImpl;
import org.springframework.core.log.LogMessage;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

/**
 * 自定义认证管理器
 *
 * @author zk
 * @date 2021/9/3 10:48
 */
public class WebReactiveAuthenticationManager extends UserDetailsRepositoryReactiveAuthenticationManager {

    /**
     * 定义用户实现服务
     */
    private final DefaultUserImpl userDetailsService;
    /**
     * 定义登录处理器
     */
    private final LoginProcessor loginProcessor;

    public WebReactiveAuthenticationManager(DefaultUserImpl userDetailsService,
                                            LoginProcessor loginProcessor) {
        super(userDetailsService);
        this.userDetailsService = userDetailsService;
        this.loginProcessor = loginProcessor;
    }

    /**
     * 获取用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Override
    public Mono<UserDetails> retrieveUser(String username) {
        //判断是否超过允许最大登录人数
        return loginProcessor
                .sessionLimitProcess(username)
                .filter(isAllow -> isAllow)
                .then(super.retrieveUser(username));
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String username = authentication.getName();
        return super.authenticate(authentication)
                // 认证成功，删除用户锁定计数
                .doOnNext(auth -> loginProcessor.removeLockRecord(username).subscribe())
                .doOnError(BadCredentialsException.class, (ex) -> {
                    // 打印认证失败日志
                    logger.info(LogMessage.format("%s Authentication failed: %s", username, ex.getMessage()));
                    // 处理是否要锁定用户
                    loginProcessor.isLockUser(username).doOnNext(aBoolean -> {
                        if (aBoolean) {
                            userDetailsService.lockUser(username);
                        }
                    }).subscribe();
                });
    }

}
