package com.kd.exception;

@SuppressWarnings("serial")
public class BusinessException extends Exception {

    public BusinessException(String msg) {
        super(msg);
    }
}
