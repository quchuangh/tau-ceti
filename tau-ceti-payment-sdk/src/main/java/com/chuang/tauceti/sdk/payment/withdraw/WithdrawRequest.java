package com.chuang.tauceti.sdk.payment.withdraw;

import com.chuang.tauceti.sdk.payment.PaymentRequest;
import lombok.Data;

@Data
public class WithdrawRequest extends PaymentRequest {
    private String account;

    private String realname;
}
