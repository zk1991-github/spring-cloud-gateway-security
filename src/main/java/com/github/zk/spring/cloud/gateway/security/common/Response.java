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

    private int code;
    private String msg;
    private String api;
    private Object data;
    private Map<String, Object> detailsMap;

    private Response() {
    }

    public static Response getInstance() {
        Response response = new Response();
        return response;
    }

    public Response setOk(int code, String api, String msg, Object data) {
        this.setCode(code);
        this.setApi(api);
        this.setMsg(msg);
        this.setData(data);
        return this;
    }

    public Response setOk(CodeEnum codeEnum, String api, String msg, Object data) {
        this.setCode(codeEnum.getCode());
        this.setApi(api);
        this.setMsg(msg);
        this.setData(data);
        return this;
    }

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

    public Response setError(int code, String api, String msg) {
        this.setCode(code);
        this.setApi(api);
        this.setMsg(msg);
        return this;
    }

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
