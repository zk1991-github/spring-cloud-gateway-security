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

package com.github.zk.spring.cloud.gateway.security.pojo;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 字典信息实体
 *
 * @author zk
 * @date 2022/2/17 11:05
 */
@TableName("gateway_dict")
public class DictInfo {
    /** 字典id */
    private Long id;
    /** 字典类型id */
    private Long dictTypeId;
    /** 字典值 */
    private String dictVal;
    /** 字典名称 */
    private String dictName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDictTypeId() {
        return dictTypeId;
    }

    public void setDictTypeId(Long dictTypeId) {
        this.dictTypeId = dictTypeId;
    }

    public String getDictVal() {
        return dictVal;
    }

    public void setDictVal(String dictVal) {
        this.dictVal = dictVal;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }
}
