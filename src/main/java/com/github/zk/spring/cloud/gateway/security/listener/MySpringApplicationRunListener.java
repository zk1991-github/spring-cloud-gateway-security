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

package com.github.zk.spring.cloud.gateway.security.listener;

import com.github.zk.spring.cloud.gateway.security.core.GatewaySecurityCache;
import com.github.zk.spring.cloud.gateway.security.core.GatewaySecurityCacheMap;
import com.github.zk.spring.cloud.gateway.security.service.IPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Duration;

/**
 * 应用启动监听
 *
 * @author zk
 * @since 4.0
 */
public class MySpringApplicationRunListener implements SpringApplicationRunListener {

    private final Logger logger = LoggerFactory.getLogger(MySpringApplicationRunListener.class);

    public MySpringApplicationRunListener(SpringApplication springApplication, String[] args) {
    }

    @Override
    public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
        IPermission iPermission = context.getBean("permissionImpl", IPermission.class);
        int anonymousPermissionsSize = iPermission.cacheAnonymousPermissions();
        logger.info("【{}】条匿名权限加载完成。", anonymousPermissionsSize);
        int openPermissionsSize = iPermission.cacheOpenPermissions();
        logger.info("【{}】条公开权限加载完成。", openPermissionsSize);
        GatewaySecurityCache gatewaySecurityCache =
                context.getBean(GatewaySecurityCache.class);
        String cacheType =
                gatewaySecurityCache instanceof GatewaySecurityCacheMap ? "单机版本" : "集群版本";
        logger.info("【{}】启动完成", cacheType);
    }
}
