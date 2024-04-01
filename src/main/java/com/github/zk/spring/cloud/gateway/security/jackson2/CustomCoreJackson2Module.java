/*
 *
 *  * Copyright 2021-2024 the original author or authors.
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

package com.github.zk.spring.cloud.gateway.security.jackson2;

import com.github.zk.spring.cloud.gateway.security.authentication.WeChatAuthenticationToken;
import com.github.zk.spring.cloud.gateway.security.pojo.WeChatUserInfo;
import org.springframework.security.jackson2.CoreJackson2Module;

/**
 * 自定义 Jackson 模型
 *
 * @author zk
 * @since 3.0
 */
public class CustomCoreJackson2Module extends CoreJackson2Module {

    @Override
    public void setupModule(SetupContext context) {
        //设置支持微信token序列化
        context.setMixInAnnotations(WeChatAuthenticationToken.class, WeChatAuthenticationTokenMixin.class);
        // 设置支持微信用户信息序列化
        context.setMixInAnnotations(WeChatUserInfo.class, WeChatUserMixin.class);
        super.setupModule(context);
    }
}
