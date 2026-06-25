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

package com.github.zk.spring.cloud.gateway.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.zk.spring.cloud.gateway.security.dao.WhitelistMapper;
import com.github.zk.spring.cloud.gateway.security.pojo.WhitelistInfo;
import com.github.zk.spring.cloud.gateway.security.service.IWhitelistService;
import com.github.zk.spring.cloud.gateway.security.util.IpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 白名单服务实现
 *
 * @author zhaokai
 * @since 5.0.0-1
 */
@Service
public class WhitelistServiceImpl implements IWhitelistService {

    @Autowired
    private WhitelistMapper whitelistMapper;

    @Override
    public int addWhitelist(WhitelistInfo whitelistInfo) {
        return whitelistMapper.insert(whitelistInfo);
    }

    @Override
    public int delWhitelist(long id) {
        return whitelistMapper.deleteById(id);
    }

    @Override
    public int updateWhitelist(WhitelistInfo whitelistInfo) {
        return whitelistMapper.updateById(whitelistInfo);
    }

    @Override
    public List<WhitelistInfo> queryAll() {
        return whitelistMapper.selectList(null);
    }

    @Override
    public WhitelistInfo queryByIpAndMac(String ip, String macAddr) {
        QueryWrapper<WhitelistInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("mac_addr", macAddr);
        List<WhitelistInfo> list = whitelistMapper.selectList(wrapper);
        for (WhitelistInfo w : list) {
            if (IpUtils.isIpInRange(ip, w.getIpAddr())) {
                return w;
            }
        }
        return null;
    }
}
