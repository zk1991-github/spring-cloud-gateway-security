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
import com.github.zk.spring.cloud.gateway.security.core.WhitelistToggle;
import com.github.zk.spring.cloud.gateway.security.dao.ConfigMapper;
import com.github.zk.spring.cloud.gateway.security.pojo.ConfigInfo;
import com.github.zk.spring.cloud.gateway.security.pojo.WhitelistToggleInfo;
import com.github.zk.spring.cloud.gateway.security.service.IConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 通用配置服务实现
 *
 * @author zhaokai
 * @since 5.2.0
 */
@Service
public class ConfigServiceImpl implements IConfigService {

    private static final String KEY_IP_ENABLED = "ip_whitelist_enabled";
    private static final String KEY_MAC_ENABLED = "mac_whitelist_enabled";

    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private WhitelistToggle whitelistToggle;

    @Override
    public WhitelistToggleInfo getWhitelistToggleInfo() {
        WhitelistToggleInfo toggleInfo = new WhitelistToggleInfo();
        toggleInfo.setIpEnabled(loadConfig(KEY_IP_ENABLED));
        toggleInfo.setMacEnabled(loadConfig(KEY_MAC_ENABLED));
        return toggleInfo;
    }

    @Override
    public void setWhitelistToggleInfo(WhitelistToggleInfo toggleInfo) {
        if (toggleInfo.getIpEnabled() != null) {
            saveConfig(KEY_IP_ENABLED, String.valueOf(toggleInfo.getIpEnabled()));
            whitelistToggle.setIpEnabled(toggleInfo.getIpEnabled());
        }
        if (toggleInfo.getMacEnabled() != null) {
            saveConfig(KEY_MAC_ENABLED, String.valueOf(toggleInfo.getMacEnabled()));
            whitelistToggle.setMacEnabled(toggleInfo.getMacEnabled());
        }
    }

    /**
     * 从数据库读取配置值，不存在返回 false
     */
    private boolean loadConfig(String key) {
        QueryWrapper<ConfigInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("config_key", key);
        ConfigInfo info = configMapper.selectOne(wrapper);
        return info != null && Boolean.parseBoolean(info.getConfigValue());
    }

    /**
     * 持久化配置值到数据库，存在则更新，不存在则插入
     */
    private void saveConfig(String key, String value) {
        QueryWrapper<ConfigInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("config_key", key);
        ConfigInfo info = configMapper.selectOne(wrapper);
        if (info != null) {
            info.setConfigValue(value);
            configMapper.updateById(info);
        } else {
            ConfigInfo seed = new ConfigInfo();
            seed.setConfigKey(key);
            seed.setConfigValue(value);
            configMapper.insert(seed);
        }
    }
}
