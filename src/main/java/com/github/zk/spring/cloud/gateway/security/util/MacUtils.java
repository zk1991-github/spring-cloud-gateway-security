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

    /**
     * 归一化 MAC 地址格式：去除分隔符（- 或 :）并转为大写，用于忽略连接符差异的匹配
     * <p>例如：{@code 6C:1F:F7:05:93:84} 与 {@code 6C-1F-F7-05-93-84} 归一化后均为 {@code 6C1FF7059384}
     *
     * @param macAddr MAC 地址
     * @return 归一化后的 MAC 地址，入参为 null 时返回 null
     */
    public static String normalizeMac(String macAddr) {
        if (macAddr == null) {
            return null;
        }
        return macAddr.replace("-", "").replace(":", "").toUpperCase();
    }
}
