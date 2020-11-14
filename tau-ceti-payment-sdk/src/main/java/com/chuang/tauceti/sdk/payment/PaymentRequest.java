package com.chuang.tauceti.sdk.payment;

import com.chuang.tauceti.support.enums.PaymentType;
import lombok.Data;

/**
 * 支付请求
 */
@Data
public class PaymentRequest {
    private String reference;
    private PaymentType type;
    private Long amount;
    private String username;
    private String clientIp;
}
