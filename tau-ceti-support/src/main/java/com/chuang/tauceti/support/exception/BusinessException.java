package com.chuang.tauceti.support.exception;

import java.util.Optional;

public class BusinessException extends CodeException {

    public BusinessException(int code, String pattern, Object... args) {
        super(code, pattern, args);
    }

    public BusinessException(String pattern, Object... args) {
        super(pattern, args);
    }

    public BusinessException(int code, Throwable e, String pattern, Object... args) {
        super(code, e, pattern, args);
    }

    public BusinessException(Throwable e, String pattern, Object... args) {
        super(e, pattern, args);
    }


    public static boolean hasBusinessException(Throwable e) {
        if(e instanceof BusinessException) {
            return true;
        }
        Throwable throwable = e;
        while(null != throwable) {
            throwable = throwable.getCause();
            if(throwable instanceof BusinessException) {
                return true;
            }
        }
        return false;
    }

    public static Optional<String> getBusinessExceptionMessage(Throwable e) {
        if(e instanceof BusinessException) {
            return Optional.of(e.getMessage());
        }
        Throwable throwable = e;
        while(null != throwable) {
            throwable = throwable.getCause();
            if(throwable instanceof BusinessException) {
                return Optional.of(throwable.getMessage());
            }
        }
        return Optional.empty();
    }
}
