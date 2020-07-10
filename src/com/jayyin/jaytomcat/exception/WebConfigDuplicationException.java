package com.jayyin.jaytomcat.exception;

/**
 * Servlet配置重复的异常
 */
public class WebConfigDuplicationException extends Exception {
    public WebConfigDuplicationException(String msg) {
        super(msg);
    }
}
