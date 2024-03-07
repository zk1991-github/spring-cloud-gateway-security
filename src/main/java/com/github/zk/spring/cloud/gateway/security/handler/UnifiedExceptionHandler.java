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

package com.github.zk.spring.cloud.gateway.security.handler;

import com.github.zk.spring.cloud.gateway.security.common.Response;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一请求异常处理
 *
 * @author zhaokai
 * @since 4.3.3
 */
@RestControllerAdvice
public class UnifiedExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public Response handleException(WebExchangeBindException e){
        Map<String, String> map = new HashMap<>();
        if (e.hasErrors()) {
            for (FieldError error : e.getFieldErrors()) {
                String key = error.getField();
                String val = error.getDefaultMessage();
                map.put(key,val);
            }
        }
        return Response.setError("参数异常！",  map);
    }
}
