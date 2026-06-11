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

package com.github.zk.spring.cloud.gateway.security.util;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.ObjectUtils;

import java.util.Objects;

/**
 * IP 相关工具类
 *
 * @author zk
 * @since 4.1.3
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
        if (ObjectUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            // Apache Http 代理使用 Proxy-Client-IP 请求头存放真实 ip 地址
            ip = request.getHeaders().getFirst("Proxy-Client-IP");
        }
        if (ObjectUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            // WebLogic 代理使用 WL-Proxy-Client-IP 请求头存放真实 ip 地址
            ip = request.getHeaders().getFirst("WL-Proxy-Client-IP");
        }
        if (ObjectUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            // 无代理时，直接获取远程地址
            ip = Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress();
        }
        return ip;
    }

    public static boolean isIpInRange(String ip, String pattern) {
        if (ip == null || pattern == null) {
            return false;
        }
        ip = normalizeIp(ip);
        pattern = normalizeIp(pattern.trim());
        int dashIndex = pattern.indexOf('-');
        if (dashIndex > 0) {
            long ipLong = ipToLong(ip);
            long start = ipToLong(pattern.substring(0, dashIndex).trim());
            long end = ipToLong(pattern.substring(dashIndex + 1).trim());
            return ipLong >= start && ipLong <= end;
        }
        return ip.equals(pattern);
    }

    private static String normalizeIp(String ip) {
        if ("localhost".equalsIgnoreCase(ip)) {
            return "127.0.0.1";
        }
        if ("::1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            return "127.0.0.1";
        }
        return ip;
    }

    private static long ipToLong(String ip) {
        String[] parts = ip.split("\\.");
        long result = 0;
        for (String part : parts) {
            result = (result << 8) | Integer.parseInt(part);
        }
        return result;
    }
}
