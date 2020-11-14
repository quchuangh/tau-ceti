package com.chuang.tauceti.support.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * Created by ath on 2016/7/12.
 */
public enum PaymentType {
    /** 银行线下 */
    OFFLINE_BANK(1),
    /** 网银 */
    ONLINE_BANK(2),
    /** VISA */
    VISA(3),
    /** VISA_H5 */
    VISA_H5(4),
    /** 快捷 */
    FAST(5),
    /** 快捷H5 */
    FAST_H5(6),
    /** 快捷H5 */
    YUN(7),
    /** 快捷H5 */
    YUN_H5(8),


    /** 微信扫码 */
    WE_CHAT(21),
    /** 微信H5(App唤醒) */
    WE_CHAT_H5(22),
    /**微信公众号支付*/
    WE_CHAT_PUBLIC(23),
    /** QQ钱包 */
    QQ_WALLET(24),
    /** QQ钱包h5 */
    QQ_WALLET_H5(25),

    /** 支付宝扫码 */
    ALI(31),
    /** 支付宝H5(App唤醒) */
    ALI_H5(32),


    /** 京东支付 */
    JD(41),
    /** 京东支付H5 */
    JD_H5(42),

    /** 银行线下打款 */
    OFFLINE_BANK_PAYOUT(-1),
    /** 银行线上打款 */
    ONLINE_BANK_PAYOUT(-2),

    /** 微信打款 */
    WE_CHAT_PAYOUT(-20),
    /** 支付宝打款 */
    ALI_PAYOUT(-30);


    @EnumValue
    private final byte code;

    PaymentType(int code) {
        this.code = (byte)code;

    }


    public byte getCode(){return code;}

    public boolean isH5() {
        return this == WE_CHAT_H5 ||
                this == ALI_H5 ||
                this == JD_H5 ||
                this == FAST_H5 ||
                this == QQ_WALLET_H5 ||
                this == VISA_H5;
    }

    public boolean isBank() {
        return this == OFFLINE_BANK ||
                this == ONLINE_BANK ||
                this == VISA ||
                this == VISA_H5 ||
                this == FAST ||
                this == FAST_H5 ||
                this == OFFLINE_BANK_PAYOUT ||
                this == ONLINE_BANK_PAYOUT;
    }

    public boolean isWithdraw() {
        return this == OFFLINE_BANK_PAYOUT ||
                this == ONLINE_BANK_PAYOUT ||
                this == WE_CHAT_PAYOUT ||
                this == ALI_PAYOUT;
    }

    public boolean isOffline() {
        return this == OFFLINE_BANK ||
                this == OFFLINE_BANK_PAYOUT;
    }
}
