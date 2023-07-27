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

package com.github.zk.spring.cloud.gateway.security.enums;

/**
 * 接口类型枚举
 *
 * @author zk
 * @date 2022/2/18 17:35
 */
public enum IntfTypeEnum {
    /**
     * 私有权限
     */
    PRIVATE_PERMISSION(0),
    /**
     * 公开权限
     */
    PUBLIC_PERMISSION(1),

    /**
     * 匿名权限
     */
    ANONYMOUS_PERMISSION(2);

    private final int index;

    IntfTypeEnum(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
