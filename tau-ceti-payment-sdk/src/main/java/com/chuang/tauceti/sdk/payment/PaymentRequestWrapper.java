package com.chuang.tauceti.sdk.payment;


import com.chuang.tauceti.support.enums.PaymentType;

/**
 * wrapper的目的是提供一个多父类继承的手段，在某些场景中，payment-sdk会有多个payment-request实现。
 * 而实现者也可以有多个实现。为了减少不必要的组合和麻烦。payment-sdk内部会直接继承PaymentRequest来进行扩展。
 * 而实现者最好是使用PaymentRequestWrapper
 *
 */
public class PaymentRequestWrapper extends PaymentRequest {

    private PaymentRequest request;

    public PaymentRequestWrapper(PaymentRequest request) {
        this.request = request;
    }

    public String getReference() {
        return request.getReference();
    }

    public void setReference(String reference) {
        request.setReference(reference);
    }

    public Long getAmount() {
        return request.getAmount();
    }

    public void setAmount(Long amount) {
        request.setAmount(amount);
    }

    public String getUsername() {
        return request.getUsername();
    }

    public void setUsername(String username) {
        request.setUsername(username);
    }

    public PaymentType getType() {
        return request.getType();
    }

    public void setType(PaymentType type) {
        request.setType(type);
    }

    public PaymentRequest getRequest() {
        return request;
    }

    @Override
    public String getClientIp() {
        return request.getClientIp();
    }

    @Override
    public void setClientIp(String clientIp) {
        request.setClientIp(clientIp);
    }
}
