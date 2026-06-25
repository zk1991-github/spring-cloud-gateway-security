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

package com.github.zk.spring.cloud.gateway.security.controller;

import com.github.zk.spring.cloud.gateway.security.common.CodeEnum;
import com.github.zk.spring.cloud.gateway.security.common.Response;
import com.github.zk.spring.cloud.gateway.security.pojo.WhitelistInfo;
import com.github.zk.spring.cloud.gateway.security.service.IWhitelistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 白名单 请求控制
 *
 * @author zhaokai
 * @since 5.0.0-1
 */
@RestController
@RequestMapping("/gateway/whitelist")
public class WhitelistController {

    @Autowired
    private IWhitelistService whitelistService;

    @PostMapping("/add")
    public Response addWhitelist(@RequestBody WhitelistInfo whitelistInfo) {
        int r = whitelistService.addWhitelist(whitelistInfo);
        if (r > 0) {
            return Response.setOk();
        }
        return Response.setError(CodeEnum.SAVE_FAIL);
    }

    @PostMapping("/del")
    public Response delWhitelist(@RequestParam long id) {
        int r = whitelistService.delWhitelist(id);
        if (r > 0) {
            return Response.setOk();
        }
        return Response.setError(CodeEnum.REMOVE_FAIL);
    }

    @PostMapping("/update")
    public Response updateWhitelist(@RequestBody WhitelistInfo whitelistInfo) {
        int r = whitelistService.updateWhitelist(whitelistInfo);
        if (r > 0) {
            return Response.setOk();
        }
        return Response.setError(CodeEnum.UPDATE_FAIL);
    }

    @GetMapping("/list")
    public Response list() {
        List<WhitelistInfo> list = whitelistService.queryAll();
        return Response.setOk(list);
    }

    @GetMapping("/query")
    public Response queryByIpAndMac(@RequestParam String ip, @RequestParam String macAddr) {
        WhitelistInfo w = whitelistService.queryByIpAndMac(ip, macAddr);
        if (w != null) {
            return Response.setOk(w);
        }
        return Response.setError(CodeEnum.QUERY_FAIL);
    }
}
