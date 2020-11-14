package com.chuang.tauceti.support.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 币种，实际上本项目中币种使用没有那么多。
 * 该枚举的构造参数说明如下
 * val:  编号（用于存入DB等)
 * Currency: java自带的Currency类，构建符合 ISO 4217 规范
 * 建立这样一个枚举的原因是，java自带的Currency没有提供枚举和常量，为了在本项目内统一货币标准而设计。
 * 如果要新增一个币种，编号必须连贯。
 * 另外，游戏、支付等接口接入时，如果需要币种，可以通过该枚举的 currency 参数来统一标准。
 * 实际上 gi 工程使用的货币接口就是java.util.currency。
 * ISO 4217 标准可能有变，所以功能必须限制，例如货币兑换比例这里无法去实现。
 * 对于ISO 4217中的准代码都用 @Deprecated 注解了。例如韩元（KRW）
 *
 * Created by ath on 2016/7/2.
 */
public enum Currency {

    /** 虚拟币 */
    VIRTUAL(0, null),

    /** 人民币 */
    CNY(1, java.util.Currency.getInstance("CNY")),
    /** 美元 */
    USD(2, java.util.Currency.getInstance("USD")),
    /** 港币 */
    HKD(3, java.util.Currency.getInstance("HKD")),
    /** 台币 */
    TWD(4, java.util.Currency.getInstance("TWD")),
    /** 澳门元 */
    MOP(5, java.util.Currency.getInstance("MOP")),
    /** 欧元 */
    EUR(6, java.util.Currency.getInstance("EUR")),
    /** 银 */
    XAG(7, java.util.Currency.getInstance("XAG")),
    /** 金 */
    XAU(8, java.util.Currency.getInstance("XAU")),
    /** 越南盾 */
    VND(9, java.util.Currency.getInstance("VND")),
    /** 泰铢 */
    THB(10, java.util.Currency.getInstance("THB")),

    /** 菲律宾比索 */
    PHP(11, java.util.Currency.getInstance("PHP")),
    /** 英镑 */
    GBP(12, java.util.Currency.getInstance("GBP")),
    /** 日元 */
    JPY(13, java.util.Currency.getInstance("JPY")),
    /** 韩元(准代号) */
    @Deprecated
    KRW(14, java.util.Currency.getInstance("KRW")),
    /** 加拿大元 */
    CAD(15, java.util.Currency.getInstance("CAD")),
    /** 澳元 */
    AUD(16, java.util.Currency.getInstance("AUD")),
    /** 瑞士法郎 */
    CHF(17, java.util.Currency.getInstance("CHF")),
    /** 新加坡元 */
    SGD(18, java.util.Currency.getInstance("SGD")),
    /** 马来西亚 令吉(林吉特) */
    MYR(19, java.util.Currency.getInstance("MYR")),
    /** 印度尼西亚盾/卢比 */
    IDR(20, java.util.Currency.getInstance("IDR")),
    /** 新西兰币 */
    NZD(21, java.util.Currency.getInstance("NZD"));

//    /** 大陆人民银行币 */
//    CNX(22, java.util.Currency.getInstance("CNX"));

    @EnumValue
    private final byte code;
    private java.util.Currency currency;

    Currency(int val, java.util.Currency currency) {
        this.code = (byte)val;
        this.currency = currency;
    }

    public byte getCode() {
        return code;
    }

    public java.util.Currency toJavaCurrency() {
        return currency;
    }



}
