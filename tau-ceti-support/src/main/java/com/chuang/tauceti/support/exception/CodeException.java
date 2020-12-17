package com.chuang.tauceti.support.exception;

import java.text.MessageFormat;

public class CodeException extends RuntimeException {
    public static final int DEFAULT_ERROR_CODE = -1;

    private final int code;

    public CodeException(Throwable throwable, String pattern, Object... args){
        this(DEFAULT_ERROR_CODE, throwable, pattern, args);
    }

    public CodeException(int code, Throwable throwable, String pattern, Object... args){
        super(MessageFormat.format(pattern, args), throwable);
        this.code = code;
    }

    public CodeException(String pattern, Object... args){
        this(DEFAULT_ERROR_CODE, pattern, args);
    }


    public CodeException(int code, String pattern, Object... args){
        super(MessageFormat.format(pattern, args));
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
