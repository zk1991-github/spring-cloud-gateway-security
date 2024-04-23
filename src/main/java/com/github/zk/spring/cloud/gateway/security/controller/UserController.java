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
import com.github.zk.spring.cloud.gateway.security.core.LoginProcessor;
import com.github.zk.spring.cloud.gateway.security.pojo.UpdatePasswordDTO;
import com.github.zk.spring.cloud.gateway.security.service.IUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * 用户 请求控制
 *
 * @author zk
 * @since 1.1
 */
@RestController
@RequestMapping("/gateway")
public class UserController {

    @Autowired
    private IUser iUser;
    @Autowired
    private LoginProcessor loginProcessor;

    /**
     * get请求方式获取用户
     *
     * @return 用户信息
     */
    @GetMapping("/getUser")
    public Mono<Response> getUser() {
        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.error(new IllegalStateException("ReactiveSecurityContext is empty")))
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .map(Response::setOk);
    }

    /**
     * post 请求方式获取用户
     *
     * @return 用户信息
     */
    @PostMapping("/queryUser")
    public Mono<Response> queryUser() {
        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.error(new IllegalStateException("ReactiveSecurityContext is empty")))
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .map(Response::setOk);
    }

    /**
     * 在线用户数
     *
     * @return 在线用户数量
     */
    @GetMapping("/getOnlineNums")
    public Mono<Response> getOnlineNums() {
        return loginProcessor.onlineNum().map(Response::setOk);
    }

    /**
     * 修改密码
     *
     * @param updatePasswordDTO 修改密码参数对象
     * @return 修改成功
     */
    @PostMapping("/updatePassword")
    public Mono<Response> updatePassword(@RequestBody @Validated UpdatePasswordDTO updatePasswordDTO) {
        boolean b = iUser.updatePassword(updatePasswordDTO.getUsername(),
                updatePasswordDTO.getOldPassword(),
                updatePasswordDTO.getNewPassword());
        if (b) {
            return Mono.just(Response.setOk());
        } else {
            return Mono.just(Response.setError(CodeEnum.UPDATE_FAIL));
        }
    }

}
