package com.chuang.tauceti.sdk.payment.withdraw;

import com.chuang.tauceti.support.enums.Bank;
import lombok.Data;

@Data
public class BankWithdrawRequest extends WithdrawRequest {

    private Bank bank;

    private String province;

    private String city;

    private String branch;

}
