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

package com.github.zk.spring.cloud.gateway.security.listener;

import com.github.zk.spring.cloud.gateway.security.service.IPermission;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 应用启动监听
 *
 * @author zk
 * @date 2022/2/17 19:09
 */
public class MySpringApplicationRunListener implements SpringApplicationRunListener {

    private final Logger logger = LoggerFactory.getLogger(MySpringApplicationRunListener.class);

    public MySpringApplicationRunListener(SpringApplication springApplication, String[] args) {
    }

    @Override
    public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
        logger.info("启动完成，开始加载公开权限。。。");
        IPermission iPermission = context.getBean("permissionImpl", IPermission.class);
        iPermission.cacheOpenPermissions();
        logger.info("公开权限加载完成。");
    }
}
