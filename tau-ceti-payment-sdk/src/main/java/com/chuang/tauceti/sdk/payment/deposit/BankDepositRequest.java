package com.chuang.tauceti.sdk.payment.deposit;

import com.chuang.tauceti.support.enums.Bank;
import lombok.Data;

@Data
public class BankDepositRequest extends DepositRequest {
    private String realname;
    private Bank bank;
    private String paymentAccount;
}
