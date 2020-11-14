package com.chuang.tauceti.sdk.payment.deposit;


import com.chuang.tauceti.sdk.payment.CallbackInfo;
import com.chuang.tauceti.support.Result;
import com.chuang.tauceti.support.enums.Whether;

import javax.annotation.Nullable;

public class DepositCallbackInfo extends CallbackInfo {

    private DepositCallbackInfo() {}

    public static Result<DepositCallbackInfo> create(boolean verify,
                                                     boolean paySuccess,
                                                     String merchantId,
                                                     String reference,
                                                     String successBackMessage,
                                                     @Nullable String providerReference,
                                                     @Nullable Long amount) {
        Result<DepositCallbackInfo> r = Result.whether(verify);
        r.message(verify ? "SUCCESS": "sign error");

        DepositCallbackInfo result = new DepositCallbackInfo();

        result.setPaymentSuccess(paySuccess ? Whether.YES: Whether.NO);
        result.setAmount(amount);
        result.setMerchantId(merchantId);
        result.setBackMessage(r.isSuccess() ? successBackMessage : "fail");
        result.setReference(reference);
        result.setProviderReference(providerReference);
        return r.data(result);
    }

    public static Result<DepositCallbackInfo> error(Throwable throwable) {
        DepositCallbackInfo result = new DepositCallbackInfo();
        result.setPaymentSuccess(Whether.NO);

        Result<DepositCallbackInfo> r = Result.fail("error ->" + throwable.getMessage());

        result.setAmount(null);
        result.setMerchantId("");
        result.setBackMessage("fail");
        result.setReference(null);
        result.setProviderReference("");
        return r.code(Result.UNKNOWN_CODE).data(result);
    }
}
