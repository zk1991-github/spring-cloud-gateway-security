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

package com.github.zk.spring.cloud.gateway.security.controller;

import com.github.zk.spring.cloud.gateway.security.common.Response;
import com.github.zk.spring.cloud.gateway.security.pojo.UpdatePasswordDTO;
import com.github.zk.spring.cloud.gateway.security.service.IUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.session.data.redis.ReactiveRedisSessionRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * 用户 请求控制
 *
 * @author zk
 * @date 2021/8/18 10:22
 */
@RestController
@RequestMapping("/gateway")
public class UserController {

    @Autowired
    private ReactiveStringRedisTemplate redisTemplate;

    @Autowired
    private IUser iUser;

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
                .map(user -> {
                    Response response = Response.getInstance();
                    response.setOk(Response.CodeEnum.SUCCESSED, "/gateway/getUser", "查询成功！", user);
                    return response;
                });
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
                .map(user -> {
                    Response response = Response.getInstance();
                    response.setOk(Response.CodeEnum.SUCCESSED, "/gateway/queryUser", "查询成功！", user);
                    return response;
                });
    }

    /**
     * 在线用户数
     *
     * @return 在线用户数量
     */
    @GetMapping("/getOnlineNums")
    public Mono<Response> getOnlineNums() {
        ScanOptions options = ScanOptions
                .scanOptions()
                .match(ReactiveRedisSessionRepository.DEFAULT_NAMESPACE + ":*")
                .build();
        return redisTemplate.scan(options).count().map(aLong -> {
            Response response = Response.getInstance();
            response.setOk(Response.CodeEnum.SUCCESSED, "/gateway/getOnlineNums", "查询成功！", aLong);
            return response;
        });
    }

    /**
     * 修改密码
     *
     * @param updatePasswordDTO 修改密码参数对象
     * @return 修改成功
     */
    @PostMapping("updatePassword")
    public Mono<Response> updatePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO) {
        Response response = Response.getInstance();
        boolean b = iUser.updatePassword(updatePasswordDTO.getUsername(),
                updatePasswordDTO.getOldPassword(),
                updatePasswordDTO.getNewPassword());
        if (b) {
            response.setOk(Response.CodeEnum.SUCCESSED, "/gateway/updatePassword", "密码修改成功！", b);
        } else {
            response.setError(Response.CodeEnum.FAIL, "/gateway/updatePassword", "密码修改失败！");
        }
        return Mono.just(response);
    }

}
