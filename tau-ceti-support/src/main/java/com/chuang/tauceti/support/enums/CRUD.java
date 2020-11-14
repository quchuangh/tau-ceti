package com.chuang.tauceti.support.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum CRUD {
    QUERY(0), UPDATE(1), CREATE(2), DELETE(3);

    @EnumValue
    private final byte code;
    CRUD(int code){
        this.code = (byte)code;
    }

    public byte getCode() {
        return code;
    }

}
