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

package com.github.zk.spring.cloud.gateway.security.service;

import com.github.zk.spring.cloud.gateway.security.pojo.DictInfo;
import java.util.List;

/**
 * 字典接口
 *
 * @author zk
 * @date 2022/2/17 11:09
 */
public interface IDict {
    /**
     * 根据字典类型id，查询字典信息
     * @param dictTypeId
     * @return
     */
    List<DictInfo> queryDictByDictTypeId(long dictTypeId);
}
