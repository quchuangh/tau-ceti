package com.chuang.tauceti.support.exception;

import java.text.MessageFormat;
import java.util.Optional;

public class BusinessException extends CodeException {
    public BusinessException(int code, String msg) {
        super(code, msg);
    }

    public BusinessException(int code, String msg, Throwable e) {
        super(code, msg, e);
    }

    public BusinessException(int code, String pattern, Object... args) {
        this(code, MessageFormat.format(pattern, args));
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
