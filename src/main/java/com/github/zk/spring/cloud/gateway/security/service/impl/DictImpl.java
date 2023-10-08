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

package com.github.zk.spring.cloud.gateway.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.zk.spring.cloud.gateway.security.dao.DictMapper;
import com.github.zk.spring.cloud.gateway.security.pojo.DictInfo;
import com.github.zk.spring.cloud.gateway.security.service.IDict;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 字典实现
 *
 * @author zk
 * @date 2022/2/17 11:10
 */
@Service
public class DictImpl implements IDict {
    @Autowired
    private DictMapper dictMapper;

    @Override
    public List<DictInfo> queryDictByDictTypeId(long dictTypeId) {
        QueryWrapper<DictInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dict_type_id", dictTypeId);
        return dictMapper.selectList(queryWrapper);
    }
}
