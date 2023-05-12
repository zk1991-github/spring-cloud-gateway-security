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

package com.github.zk.spring.cloud.gateway.security.log;

import com.github.zk.spring.cloud.gateway.security.pojo.LogInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.UserInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.WeChatUserInfo;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

/**
 * 日志持有类
 *
 * @author zk
 * @date 2022/1/6 9:24
 */
public class LogHolder {

    /**
     * 日志实现方式
     */
    private Log log = new ControlLog();

    /**
     * 日志记录开关
     */
    private boolean enabled;

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 登录日志
     * @param exchange 请求
     * @param user 用户信息
     * @param status 状态
     * @param msg 消息
     */
    public void loginLog(ServerWebExchange exchange, Object user, int status, String msg) {
        if (!this.enabled) {
            return;
        }
        String userId = "";
        String username = "";
        if (user instanceof UserInfo) {
            UserInfo userInfo = (UserInfo) user;
            userId = String.valueOf(userInfo.getId());
            username = userInfo.getUsername();
        } else {
            WeChatUserInfo weChatUserInfo = (WeChatUserInfo) user;
            userId = weChatUserInfo.getOpenid();
            username = weChatUserInfo.getNickName();
        }
        // 获取 ip 地址
        String ip = getIpAddr(exchange.getRequest());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String time = LocalDateTime.now().format(dateTimeFormatter);
        LogInfo logInfo = new LogInfo(userId, username, ip, status, msg, time);
        log.loginLog(logInfo);
    }

    /**
     * 获取真实 IP 地址
     *
     * @param request 请求对象
     * @return ip地址
     */
    private String getIpAddr(ServerHttpRequest request) {
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
