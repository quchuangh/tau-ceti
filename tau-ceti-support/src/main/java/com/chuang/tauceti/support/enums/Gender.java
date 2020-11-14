package com.chuang.tauceti.support.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * Created by ath on 2017/1/10.
 */
public enum Gender {

    MALE(1, "男"),
    FEMALE(2, "女"),
    LADY_MAN(3, "人妖");

    @EnumValue
    private final byte code;
    private String title;
    Gender(byte code, String title) {
        this.code = code;
        this.title = title;
    }

    Gender(int code, String title) {
        this((byte)code, title);
    }

    public byte getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }
}
