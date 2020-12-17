package com.chuang.tauceti.support;


import com.chuang.tauceti.support.enums.Whether;
import com.chuang.tauceti.support.exception.CodeException;

import javax.annotation.Nullable;
import java.util.function.Function;

public final class Result<T> {

    public static final int SUCCESS_CODE = 0;
    public static final int FAIL_CODE = CodeException.DEFAULT_ERROR_CODE;
    public static final int UNKNOWN_CODE = -2;

    private final Whether whether;

    private int code;

    private @Nullable String message;

    private @Nullable T data;


    public static <T> Result<T> success(T data) {
        return new Result<>(Whether.YES, SUCCESS_CODE, data, "");
    }

    public static <T> Result<T> success() {
        return new Result<>(Whether.YES, SUCCESS_CODE, null, "");
    }

    public static <T> Result<T> fail(int code, @Nullable String message) {
        return new Result<>(Whether.NO, code, null, message);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(Whether.NO, FAIL_CODE, null, message);
    }

    public static <T> Result<T> whether(boolean success) {
        return whether(success, null);
    }

    public static <T> Result<T> whether(boolean success, @Nullable String message) {
        return new Result<>(success? Whether.YES: Whether.NO, success ? SUCCESS_CODE : 0, null, message);
    }
    public static <T> Result<T> whether(Whether whether, @Nullable String message) {
        return new Result<>(whether, whether.isSuccess() ? SUCCESS_CODE : 0, null, message);
    }

    public static <T> Result<T> create(Whether whether, int code, @Nullable T data, @Nullable String message) {
        return new Result<>(whether, code, data, message);
    }

    public boolean isSuccess() {
        return this.whether == Whether.YES;
    }

    private Result(Whether whether, int code, @Nullable T data, @Nullable String message) {
        this.whether = whether;
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public Whether getWhether() {
        return whether;
    }


    public int getCode() {
        return code;
    }

    public @Nullable T getData() {
        return data;
    }

    public @Nullable String getMessage() {
        return message;
    }


    public Result<T> data(T data) {
        this.data = data;
        return this;
    }

    public Result<T> message(String message) {
        this.message = message;
        return this;
    }
    public Result<T> code(int code) {
        this.code = code;
        return this;
    }

    public <R> Result<R> map(Function<T, R> map) {
        return create(this.whether, this.code, map.apply(this.data), this.message);
    }
}
