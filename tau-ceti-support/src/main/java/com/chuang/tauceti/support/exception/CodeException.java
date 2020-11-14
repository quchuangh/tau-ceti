package com.chuang.tauceti.support.exception;

public class CodeException extends RuntimeException {
    private final int code;
    public CodeException(int code, String message, Throwable throwable){
        super(message, throwable);
        this.code = code;
    }


    public CodeException(int code, String message){
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
