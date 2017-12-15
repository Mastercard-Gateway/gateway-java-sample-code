package com.gateway.client;

public class WebhookNotification {
    long timestamp;
    String orderId;
    String transactionId;
    String orderStatus;
    String amount;

    public WebhookNotification(String orderId, String transactionId, String orderStatus, String amount) {
        this.timestamp = System.currentTimeMillis();
        this.orderId = orderId;
        this.transactionId = transactionId;
        this.orderStatus = orderStatus;
        this.amount = amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getAmount() {
        return amount;
    }
}
