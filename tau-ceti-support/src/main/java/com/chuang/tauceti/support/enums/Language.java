package com.chuang.tauceti.support.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 语言，事实上 Locale 已经有标准了。
 * 以这种方式来使用，主要还是为了限制本项目中的语种。
 * Created by ath on 2016/7/2.
 */
public enum Language {
    /** 中文简体 */
    ZH_CN(1),
    /** 中文繁体 */
    ZH_TW(2),
    /** US */
    EN_US(3);

    @EnumValue
    private final byte code;
    Language(int code) {
        this.code = (byte)code;
    }



    public static Language valueOf(byte code) {
        if(code < 1 || code > Language.values().length) {
            code = 1;
        }
        code -= 1;
        return Language.values()[code];
    }


    public byte getCode() {
        return code;
    }
    public Locale getLocale() {
        return map.get(this);
    }
    private final static Map<Language, Locale> map;
    static {
        map = new HashMap<>();
        map.put(Language.ZH_CN, Locale.CHINA);

        map.put(Language.ZH_TW, Locale.TRADITIONAL_CHINESE);
        map.put(Language.EN_US, Locale.US);
    }


}
