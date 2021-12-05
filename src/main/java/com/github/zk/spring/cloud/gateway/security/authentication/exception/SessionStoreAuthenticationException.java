package com.github.zk.spring.cloud.gateway.security.authentication.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 会话存储异常
 *
 * @author zk
 * @date 2021/12/2 10:11
 */
public class SessionStoreAuthenticationException extends AuthenticationException {

    public SessionStoreAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SessionStoreAuthenticationException(String msg) {
        super(msg);
    }
}
