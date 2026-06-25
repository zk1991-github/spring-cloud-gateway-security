package com.github.zk.spring.cloud.gateway.security.util;

import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * @author zhaokai
 * @since 5.0.0-1
 */
public class MacUtils {

    /**
     * 获取请求头中的 mac 地址
     *
     * @param request 请求
     * @return mac 地址
     */
    public static String getMacAddr(ServerHttpRequest request) {
        return request.getHeaders().getFirst("X-MAC-Address");
    }
}
