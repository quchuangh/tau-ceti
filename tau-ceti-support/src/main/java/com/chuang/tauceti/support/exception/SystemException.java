package com.chuang.tauceti.support.exception;

import java.util.Optional;

public class SystemException extends CodeException {

    public SystemException(int code, String pattern, Object... args) {
        super(code, pattern, args);
    }

    public SystemException(String pattern, Object... args) {
        super(pattern, args);
    }

    public SystemException(int code, Throwable e, String pattern, Object... args) {
        super(code, e, pattern, args);
    }

    public SystemException(Throwable e, String pattern, Object... args) {
        super(e, pattern, args);
    }

    public static boolean hasSystemException(Throwable e) {
        Throwable throwable = e;
        while(null != throwable) {
            throwable = throwable.getCause();
            if(throwable instanceof SystemException) {
                return true;
            }
        }
        return false;
    }

    public static Optional<String> getSystemExceptionMessage(Throwable e) {
        if(e instanceof SystemException) {
            return Optional.of(e.getMessage());
        }
        Throwable throwable = e;
        while(null != throwable) {
            throwable = throwable.getCause();
            if(throwable instanceof SystemException) {
                return Optional.of(throwable.getMessage());
            }
        }
        return Optional.empty();
    }
}
