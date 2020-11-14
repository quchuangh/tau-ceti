package com.chuang.tauceti.sdk.payment.query;

import com.chuang.tauceti.support.Result;

public class QueryInfo {
    private OrderStatus status;

    public QueryInfo(OrderStatus status) {
        this.status = status;
    }

    public QueryInfo() {}

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public static Result<QueryInfo> parse(boolean success, String message, OrderStatus status) {
        Result<QueryInfo> result = Result.whether(success);
        return result
                .message(message)
                .data(new QueryInfo(status));
    }
}
