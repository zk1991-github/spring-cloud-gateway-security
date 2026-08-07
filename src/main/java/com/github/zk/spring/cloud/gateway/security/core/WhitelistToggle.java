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

package com.github.zk.spring.cloud.gateway.security.core;

/**
 * 白名单运行时开关缓存。
 * 供 {@link com.github.zk.spring.cloud.gateway.security.filter.IpWhitelistWebFilter} 在请求时快速读取，
 * 状态由 {@link com.github.zk.spring.cloud.gateway.security.controller.WhitelistController} 在读写 DB 后同步更新。
 *
 * @author zhaokai
 * @since 5.2.0
 */
public class WhitelistToggle {

    /**
     * IP 白名单是否开启
     */
    private volatile boolean ipEnabled = false;

    /**
     * MAC 白名单是否开启
     */
    private volatile boolean macEnabled = false;

    public boolean isIpEnabled() {
        return ipEnabled;
    }

    public void setIpEnabled(boolean ipEnabled) {
        this.ipEnabled = ipEnabled;
    }

    public boolean isMacEnabled() {
        return macEnabled;
    }

    public void setMacEnabled(boolean macEnabled) {
        this.macEnabled = macEnabled;
    }

    /**
     * IP 和 MAC 白名单是否都关闭
     */
    public boolean isAllDisabled() {
        return !ipEnabled && !macEnabled;
    }
}
