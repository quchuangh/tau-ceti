package com.chuang.tauceti.support.exception;

import java.util.Optional;

public class SystemException extends CodeException {

    public SystemException(String msg) {
        super(-1, msg);
    }

    public SystemException(int code, String msg) {
        super(code, msg);
    }

    public SystemException(String msg, Throwable e) {
        super(-1, msg, e);
    }

    public SystemException(int code, String msg, Throwable e) {
        super(code, msg, e);
    }

    public static boolean hasSystemException(Throwable e) {
        if(e instanceof SystemException) {
            return true;
        }
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
