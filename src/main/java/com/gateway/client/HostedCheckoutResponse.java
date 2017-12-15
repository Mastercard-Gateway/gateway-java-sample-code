package com.gateway.client;

public class HostedCheckoutResponse extends TransactionResponse {

    String orderDescription;

    public String getOrderDescription() {
        return orderDescription;
    }

    public void setOrderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
    }
}
