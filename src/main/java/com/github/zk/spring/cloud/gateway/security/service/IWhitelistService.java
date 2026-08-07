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

package com.github.zk.spring.cloud.gateway.security.service;

import com.github.zk.spring.cloud.gateway.security.pojo.WhitelistInfo;

import java.util.List;

/**
 * 白名单服务接口
 *
 * @author zhaokai
 * @since 5.0.0-1
 */
public interface IWhitelistService {

    int addWhitelist(WhitelistInfo whitelistInfo);

    int delWhitelist(long id);

    int updateWhitelist(WhitelistInfo whitelistInfo);

    List<WhitelistInfo> queryAll();

    WhitelistInfo queryByIpAndMac(String ip, String macAddr);

    /**
     * 仅通过 IP 查询白名单（忽略 MAC 地址）
     *
     * @param ip IP 地址
     * @return 匹配的白名单记录，无匹配返回 null
     */
    WhitelistInfo queryByIpOnly(String ip);

    /**
     * 仅通过 MAC 地址查询白名单（忽略 IP 地址）
     *
     * @param macAddr MAC 地址
     * @return 匹配的白名单记录，无匹配返回 null
     */
    WhitelistInfo queryByMacOnly(String macAddr);
}
