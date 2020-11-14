package com.chuang.tauceti.support;

import com.chuang.tauceti.support.enums.Whether;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public final class MapResult<K, V> {

    private final Whether whether;

    private int code;

    private @Nullable String message;

    private final Map<K, V> data = new HashMap<>();


    public Whether getWhether() {
        return whether;
    }


    public int getCode() {
        return code;
    }

    public Map<K, V> getData() {
        return data;
    }

    public @Nullable String getMessage() {
        return message;
    }

    public MapResult<K, V> data(K key, V value) {
        data.put(key, value);
        return this;
    }

    public MapResult<K, V> message(String message) {
        this.message = message;
        return this;
    }
    public MapResult<K, V> code(int code) {
        this.code = code;
        return this;
    }

    public static <K, V> MapResult<K, V> success() {
        return new MapResult<>(Whether.YES, Result.SUCCESS_CODE,"");
    }

    public static <K, V> MapResult<K, V> fail(int code, @Nullable String message) {
        return new MapResult<>(Whether.NO, code, message);
    }

    public static <K, V> MapResult<K, V> fail(String message) {
        return new MapResult<>(Whether.NO, Result.FAIL_CODE, message);
    }

    public static <K, V> MapResult<K, V> whether(boolean success, @Nullable String message) {
        return new MapResult<>(success? Whether.YES: Whether.NO, success ? Result.SUCCESS_CODE : 0, message);
    }
    public static <K, V> MapResult<K, V> whether(Whether whether, @Nullable String message) {
        return new MapResult<>(whether, whether.isSuccess() ? Result.SUCCESS_CODE : 0, message);
    }

    private MapResult(Whether whether, int code, @Nullable String message) {
        this.whether = whether;
        this.code = code;
        this.message = message;
    }


    public Result<Map<K, V>> toResult(){
        return Result.create(this.whether, code, this.data, message);
    }

}
