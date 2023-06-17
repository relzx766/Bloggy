package com.zyq.bloggy.exception;


public class ServiceException extends RuntimeException {
    private int code;
    private Throwable cause;
    private String message;

    public ServiceException() {
    }

    public ServiceException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }
}
