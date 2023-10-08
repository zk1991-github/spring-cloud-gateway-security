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

/*
 * Response.java
 * @version 1.0
 * 2018年4月24日
 * 北京航天宏图信息技术股份有限公司
 */
package com.github.zk.spring.cloud.gateway.security.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

/**
 * Web统一返回格式
 *
 * @author zk
 * @date 2018/8/16 10:44
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"data", "api", "msg", "code"})
public class Response {

    /**
     * 返回枚举类
     */
    public enum CodeEnum {
        /**
         * 成功返回
         */
        SUCCESSED(0),
        /**
         * 登录失效
         */
        INVALID(9000),
        /**
         * 失败返回
         */
        FAIL(10000);

        private final Integer code;

        CodeEnum(Integer code) {
            this.code = code;
        }

        public Integer getCode() {
            return code;
        }
    }

    /**
     * 状态码
     */
    private int code;
    /**
     * 提示信息
     */
    private String msg;
    /**
     * 网关地址
     */
    private String api;
    /**
     * 数据
     */
    private Object data;
    /**
     * 详细信息
     */
    private Map<String, Object> detailsMap;

    private Response() {
    }

    public static Response getInstance() {
        return new Response();
    }

    /**
     * 设置成功返回
     *
     * @param code 状态码
     * @param api 网关地址
     * @param msg 提示信息
     * @param data 数据
     * @return 响应对象
     */
    public Response setOk(int code, String api, String msg, Object data) {
        this.setCode(code);
        this.setApi(api);
        this.setMsg(msg);
        this.setData(data);
        return this;
    }

    /**
     * 设置成功返回
     *
     * @param codeEnum 状态码枚举
     * @param api 网关地址
     * @param msg 提示信息
     * @param data 数据
     * @return 响应对象
     */
    public Response setOk(CodeEnum codeEnum, String api, String msg, Object data) {
        this.setCode(codeEnum.getCode());
        this.setApi(api);
        this.setMsg(msg);
        this.setData(data);
        return this;
    }

    /**
     * 设置成功返回
     *
     * @param code 状态码
     * @param api 网关地址
     * @param msg 提示信息
     * @param data 数据
     * @param total 数据总条数
     * @return 响应对象
     */
    public Response setOk(int code, String api, String msg, Object data, long total) {
        detailsMap = new HashMap<String, Object>(16);
        detailsMap.put("details", data);
        detailsMap.put("total", total);

        this.setCode(code);
        this.setApi(api);
        this.setMsg(msg);
        this.setData(detailsMap);
        return this;
    }

    /**
     * 设置成功返回
     *
     * @param codeEnum 状态码枚举
     * @param api 网关地址
     * @param msg 提示信息
     * @param data 数据
     * @param total 数据总条数
     * @return 响应对象
     */
    public Response setOk(CodeEnum codeEnum, String api, String msg, Object data, long total) {
        detailsMap = new HashMap<String, Object>(16);
        detailsMap.put("details", data);
        detailsMap.put("total", total);

        this.setCode(codeEnum.getCode());
        this.setApi(api);
        this.setMsg(msg);
        this.setData(detailsMap);
        return this;
    }

    /**
     * 设置失败返回
     *
     * @param code 状态码
     * @param api 网关地址
     * @param msg 提示信息
     * @return 响应对象
     */
    public Response setError(int code, String api, String msg) {
        this.setCode(code);
        this.setApi(api);
        this.setMsg(msg);
        return this;
    }

    /**
     * 设置失败返回
     *
     * @param codeEnum 状态码枚举
     * @param api 网关地址
     * @param msg 提示信息
     * @return 响应对象
     */
    public Response setError(CodeEnum codeEnum, String api, String msg) {
        this.setCode(codeEnum.getCode());
        this.setApi(api);
        this.setMsg(msg);
        return this;
    }

    public String getApi() {
        return api;
    }

    private void setApi(String api) {
        this.api = api;
    }

    public Object getData() {
        return data;
    }

    private void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    private void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    private void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", api='" + api + '\'' +
                '}';
    }
}
