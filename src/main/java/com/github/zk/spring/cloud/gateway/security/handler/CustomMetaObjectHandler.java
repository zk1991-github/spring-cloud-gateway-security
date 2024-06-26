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

package com.github.zk.spring.cloud.gateway.security.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 自定义 元对象处理器
 * 用于处理新增和修改时，自动填充数据
 *
 * @author zk
 * @since 1.0
 */
@Component
public class CustomMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入时填充
     *
     * @param metaObject 元数据
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatTime = localDateTime.format(dateTimeFormatter);
        // 创建时间字段自动填充
        this.fillStrategy(metaObject, "createTime", formatTime);
        // 修改时间字段自动填充
        this.fillStrategy(metaObject, "updateTime", formatTime);
    }

    /**
     * 修改时填充
     *
     * @param metaObject 元数据
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatTime = localDateTime.format(dateTimeFormatter);
        // 修改时间字段自动填充
        this.fillStrategy(metaObject, "updateTime", formatTime);
    }
}
