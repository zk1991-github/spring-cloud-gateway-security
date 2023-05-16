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

package com.github.zk.spring.cloud.gateway.security.util;

import java.util.Objects;
import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * IP 相关工具类
 *
 * @author zk
 * @date 2023/5/16 13:48
 */
public class IpUtils {
    /**
     * 获取真实 IP 地址
     *
     * @param request 请求对象
     * @return ip地址
     */
    public static String getIpAddr(ServerHttpRequest request) {
        //Nginx 使用 x-forwarded-for 请求头存放真实 ip 地址
        String ip = request.getHeaders().getFirst("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            // Apache Http 代理使用 Proxy-Client-IP 请求头存放真实 ip 地址
            ip = request.getHeaders().getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            // WebLogic 代理使用 WL-Proxy-Client-IP 请求头存放真实 ip 地址
            ip = request.getHeaders().getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            // 无代理时，直接获取远程地址
            ip = Objects.requireNonNull(request.getRemoteAddress()).getHostName();
        }
        return ip;
    }
}
