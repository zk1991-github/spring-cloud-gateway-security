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

package com.github.zk.spring.cloud.gateway.security.controller;

import com.github.zk.spring.cloud.gateway.security.common.CodeEnum;
import com.github.zk.spring.cloud.gateway.security.common.Response;
import com.github.zk.spring.cloud.gateway.security.pojo.DictInfo;
import com.github.zk.spring.cloud.gateway.security.service.IDict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 字典请求控制
 *
 * @author zk
 * @since 4.0
 */
@RestController
@RequestMapping("/gateway")
public class DictController {
    @Autowired
    private IDict iDict;

    @GetMapping("/queryDictByDictTypeId")
    public Response queryDictByDictTypeId(@RequestParam Long dictTypeId) {
        List<DictInfo> dictInfos = iDict.queryDictByDictTypeId(dictTypeId);
        if (dictInfos != null) {
            return Response.setOk(dictInfos);
        } else {
            return Response.setError(CodeEnum.QUERY_FAIL);
        }
    }
}
