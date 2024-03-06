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

package com.github.zk.spring.cloud.gateway.security.pojo;

/**
 * 请求统计信息 实体
 *
 * @author zk
 * @since 4.2.0
 */
public class RequestStatisticInfo {
    /** 请求路径 */
    private String urlPath;
    /** 成功次数 */
    private Long successNum;
    /** 失败次数 */
    private Long failNum;
    /** 成功率 */
    private String successRate;
    /** 平均响应时间 */
    private Integer responseDurationAvg;

    public RequestStatisticInfo() {}
    public RequestStatisticInfo(String urlPath, Long successNum, Long failNum, String successRate, Integer responseDurationAvg) {
        this.urlPath = urlPath;
        this.successNum = successNum;
        this.failNum = failNum;
        this.successRate = successRate;
        this.responseDurationAvg = responseDurationAvg;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public Long getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(Long successNum) {
        this.successNum = successNum;
    }

    public Long getFailNum() {
        return failNum;
    }

    public void setFailNum(Long failNum) {
        this.failNum = failNum;
    }

    public String getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(String successRate) {
        this.successRate = successRate;
    }

    public Integer getResponseDurationAvg() {
        return responseDurationAvg;
    }

    public void setResponseDurationAvg(Integer responseDurationAvg) {
        this.responseDurationAvg = responseDurationAvg;
    }
}
