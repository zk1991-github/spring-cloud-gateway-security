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

package com.github.zk.spring.cloud.gateway.security.pojo;

/**
 * 白名单开关状态
 *
 * @author zhaokai
 * @since 5.2.0
 */
public class WhitelistToggleInfo {

    /**
     * IP 白名单开关
     */
    private Boolean ipEnabled;

    /**
     * MAC 白名单开关
     */
    private Boolean macEnabled;

    public Boolean getIpEnabled() {
        return ipEnabled;
    }

    public void setIpEnabled(Boolean ipEnabled) {
        this.ipEnabled = ipEnabled;
    }

    public Boolean getMacEnabled() {
        return macEnabled;
    }

    public void setMacEnabled(Boolean macEnabled) {
        this.macEnabled = macEnabled;
    }
}
