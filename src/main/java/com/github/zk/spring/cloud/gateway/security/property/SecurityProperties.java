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

package com.github.zk.spring.cloud.gateway.security.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaokai
 * @since 4.3.6
 */
@Configuration
@ConfigurationProperties("spring.security")
public class SecurityProperties {
    /**
     * 代理地址，与前端代理地址一致
     */
    private String proxyUrl;

    /**
     * 静态资源地址
     */
    private String[] antpatterns;

    /**
     * csrf拦截,默认开启
     */
    private Boolean csrfEnable = true;

    /**
     * 源 ip 转发，默认关闭
     */
    private Boolean sourceIpEnable = false;

    /**
     * 白名单认证，默认关闭
     */
    private Boolean whitelistEnable = false;

    /**
     * 白名单配置
     */
    private Whitelist whitelist = new Whitelist();

    public String getProxyUrl() {
        return proxyUrl;
    }

    public void setProxyUrl(String proxyUrl) {
        this.proxyUrl = proxyUrl;
    }

    public String[] getAntpatterns() {
        return antpatterns;
    }

    public void setAntpatterns(String[] antpatterns) {
        this.antpatterns = antpatterns;
    }

    public Boolean getCsrfEnable() {
        return csrfEnable;
    }

    public void setCsrfEnable(Boolean csrfEnable) {
        this.csrfEnable = csrfEnable;
    }

    public Boolean getSourceIpEnable() {
        return sourceIpEnable;
    }

    public void setSourceIpEnable(Boolean sourceIpEnable) {
        this.sourceIpEnable = sourceIpEnable;
    }

    public Boolean getWhitelistEnable() {
        return whitelistEnable;
    }

    public void setWhitelistEnable(Boolean whitelistEnable) {
        this.whitelistEnable = whitelistEnable;
    }

    public Whitelist getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(Whitelist whitelist) {
        this.whitelist = whitelist;
    }

    public static class Whitelist {
        private List<String> ips = new ArrayList<>();

        public List<String> getIps() {
            return ips;
        }

        public void setIps(List<String> ips) {
            this.ips = ips;
        }

    }
}
