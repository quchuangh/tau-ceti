package com.chuang.tauceti.sdk.payment;

import com.chuang.tauceti.support.enums.Whether;

import javax.annotation.Nullable;
import java.util.Optional;

public class CallbackInfo {
    /** 本平台的订单号 */
    private String reference;

    /** 第三方的订单号 */
    private @Nullable
    String providerReference;

    private @Nullable Long amount;

    private String merchantId;

    /**
     * 是否支付成功
     */
    private Whether paymentSuccess;
    /**
     * 返回给回调者的信息
     */
    private String backMessage;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public Whether getPaymentSuccess() {
        return paymentSuccess;
    }

    public void setPaymentSuccess(Whether paymentSuccess) {
        this.paymentSuccess = paymentSuccess;
    }

    public String getBackMessage() {
        return backMessage;
    }

    public void setBackMessage(String backMessage) {
        this.backMessage = backMessage;
    }

    public Optional<String> getProviderReference() {
        return Optional.ofNullable(providerReference);
    }

    public void setProviderReference(@Nullable String providerReference) {
        this.providerReference = providerReference;
    }

    public Optional<Long> getAmount() {
        return Optional.ofNullable(amount);
    }

    public void setAmount(@Nullable Long amount) {
        this.amount = amount;
    }

}
