package com.chuang.tauceti.sdk.payment.withdraw;

import com.chuang.tauceti.sdk.payment.CallbackInfo;
import com.chuang.tauceti.support.Result;
import com.chuang.tauceti.support.enums.Whether;

import javax.annotation.Nullable;

public class WithdrawCallbackInfo extends CallbackInfo {

    private WithdrawCallbackInfo() {}

    public static Result<WithdrawCallbackInfo> create(boolean verify,
                                                      Whether paySuccess,
                                                      String merchantId,
                                                      String reference,
                                                      String successBackMessage,
                                                      @Nullable String providerReference,
                                                      @Nullable Long amount) {
        Result<WithdrawCallbackInfo> r = Result.whether(verify);
        r.message(verify ? "SUCCESS": "sign error");

        WithdrawCallbackInfo result = new WithdrawCallbackInfo();
        result.setPaymentSuccess(paySuccess);
        result.setAmount(amount);
        result.setMerchantId(merchantId);
        result.setBackMessage(paySuccess == Whether.YES ? successBackMessage : "fail");
        result.setReference(reference);
        result.setProviderReference(providerReference);
        return r.data(result);
    }
}
