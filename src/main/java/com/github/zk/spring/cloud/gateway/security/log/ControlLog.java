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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 控制台显示日志
 *
 * @author zk
 * @date 2022/1/5 17:35
 */
public class ControlLog implements Log {

    private final Logger logger = LoggerFactory.getLogger(Log.class);

    @Override
    public void loginLog(LogInfo logInfo) {
        logger.info("用户id【{}】，用户名【{}】，ip【{}】，登录状态【{}】，登录信息【{}】，时间【{}】",
                logInfo.getUserId(), logInfo.getUsername(),
                logInfo.getIp(), logInfo.getStatus(),
                logInfo.getMsg(), logInfo.getTime());
    }
}
