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

package com.github.zk.spring.cloud.gateway.security.pojo;

/**
 * 微信传参
 *
 * @author zk
 * @date 2021/11/17 13:21
 */
public class WeChatDTO {
    /**
     * 微信code
     */
    private String weChatCode;
    /**
     * 微信用户信息
     */
    private WeChatUserInfo weChatUserInfo;

    public String getWeChatCode() {
        return weChatCode;
    }

    public void setWeChatCode(String weChatCode) {
        this.weChatCode = weChatCode;
    }

    public WeChatUserInfo getWeChatUserInfo() {
        return weChatUserInfo;
    }

    public void setWeChatUserInfo(WeChatUserInfo weChatUserInfo) {
        this.weChatUserInfo = weChatUserInfo;
    }
}
