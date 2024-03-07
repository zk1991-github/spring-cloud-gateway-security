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

/*
 * 统一响应
 *
 * @since 1.0
 */
package com.github.zk.spring.cloud.gateway.security.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Web统一返回格式
 *
 * @author zk
 * @since 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"data", "msg", "code"})
public class Response {

    /**
     * 状态码
     */
    private int code;
    /**
     * 提示信息
     */
    private String msg;

    /**
     * 数据
     */
    private Object data;

    private Response() {
    }

    /**
     * 成功返回
     *
     * @return 响应对象
     */
    public static Response setOk() {
        return setOk(CodeEnum.SUCCESS, null);
    }

    /**
     * 成功返回
     *
     * @param codeEnum 状态码
     * @return 响应对象
     */
    public static Response setOk(CodeEnum codeEnum) {
        return setOk(codeEnum, null);
    }

    /**
     * 成功返回
     *
     * @param data     数据
     * @return 响应对象
     */
    public static Response setOk(Object data) {
        return Response.setOk(CodeEnum.SUCCESS, data);
    }

    /**
     * 成功返回
     *
     * @param codeEnum 状态码枚举
     * @param data     数据
     * @return 响应对象
     */
    public static Response setOk(CodeEnum codeEnum, Object data) {
        Response response = new Response();
        response.setCode(codeEnum.getCode());
        response.setMsg(codeEnum.getMsg());
        response.setData(data);
        return response;
    }

    /**
     * 成功返回
     *
     * @param code 状态码
     * @param msg 提示信息
     * @param data 数据
     * @return 响应对象
     */
    public static Response setOk(int code, String msg, Object data) {
        Response response = new Response();
        response.setCode(code);
        response.setMsg(msg);
        response.setData(data);
        return response;
    }


    /**
     * 失败返回
     *
     * @return 响应对象
     */
    public static Response setError() {
        return setError(CodeEnum.FAIL);
    }
    /**
     * 失败返回
     *
     * @param codeEnum 状态码
     */
    public static Response setError(CodeEnum codeEnum) {
        Response response = new Response();
        response.setCode(codeEnum.getCode());
        response.setMsg(codeEnum.getMsg());
        return response;
    }

    /**
     * 错误返回
     *
     * @param msg 提示信息
     * @return 响应对象
     */
    public static Response setError(String msg) {
        Response response = new Response();
        response.setCode(CodeEnum.ERROR.getCode());
        response.setMsg(msg);
        return response;
    }

    public static Response setError(String msg, Object data) {
        Response response = new Response();
        response.setCode(CodeEnum.ERROR.getCode());
        response.setMsg(msg);
        response.setData(data);
        return response;
    }

    /**
     * 失败返回
     *
     * @param code 状态码
     * @param msg 提示信息
     * @return 响应对象
     */
    public static Response setError(int code, String msg) {
        Response response = new Response();
        response.setCode(code);
        response.setMsg(msg);
        return response;
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
                '}';
    }
}
