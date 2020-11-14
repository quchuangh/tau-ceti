package com.chuang.tauceti.sdk.payment.withdraw;

import com.chuang.tauceti.support.Result;

public class WithdrawInfo {

    private final boolean needCallback;

    private final String providerReference;

    private final String reference;

    private final String response;
    public WithdrawInfo(boolean needCallback, String reference, String providerReference, String response) {
        this.reference = reference;
        this.providerReference = providerReference;
        this.needCallback = needCallback;
        this.response = response;
    }

    public String getProviderReference() {
        return providerReference;
    }

    public boolean isNeedCallback() {
        return needCallback;
    }

    public String getResponse() {
        return response;
    }

    public String getReference() {
        return reference;
    }

    public static Result<WithdrawInfo> parse(boolean success,
                                             String message,
                                             boolean needCallback,
                                             String reference,
                                             String providerReference,
                                             String response) {
        Result<WithdrawInfo> r = Result.whether(success);

        return r.message(message)
                .data(new WithdrawInfo(needCallback, reference, providerReference, response));
    }

}
