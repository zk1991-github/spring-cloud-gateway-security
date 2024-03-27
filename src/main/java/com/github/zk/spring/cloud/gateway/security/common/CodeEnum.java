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

package com.github.zk.spring.cloud.gateway.security.common;

/**
 * @author zhaokai
 * @since 4.3.3
 */
public enum CodeEnum {
    SUCCESS(200,"成功"),
    FORBIDDEN(403,"没有权限"),
    FAIL(500,"后台出现异常错误"),
    SAVE_FAIL(701, "添加失败"),
    REMOVE_FAIL(702, "删除失败"),
    UPDATE_FAIL(703, "修改失败"),
    QUERY_FAIL(704, "查询失败"),
    BIND_FAIL(705, "绑定失败"),
    NOT_LOGIN(706, "未登录或登录超时"),
    UNKNOWN_ACCOUNT(707, "用户不存在"),
    PASSWORD_ERROR(708, "密码错误"),
    ACCOUNT_LOCKED(709, "用户帐号已被锁定"),
    FORMAT_ERROR(710,"请求格式错误"),
    TOKEN_INVALID(711,"token值无效，请重新登录"),
    ERROR(712, "");

    /**
     * 状态码
     */
    private final int code;
    /**
     * 提示信息
     */
    private final String msg;

    CodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static CodeEnum getByCode(int code) {
        for (CodeEnum anEnum : CodeEnum.values()) {
            if (anEnum.getCode() == code) {
                return anEnum;
            }
        }
        throw new RuntimeException("枚举中无此code码");
    }

    public static CodeEnum getByMsg(String msg) {
        for (CodeEnum anEnum : CodeEnum.values()) {
            if (anEnum.getMsg().equals(msg)) {
                return anEnum;
            }
        }
        throw new RuntimeException("枚举中无此提示信息");
    }
}
