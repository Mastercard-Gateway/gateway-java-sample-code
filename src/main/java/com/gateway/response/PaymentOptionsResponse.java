/*
 * Copyright (c) 2019 MasterCard. All rights reserved.
 */

package com.gateway.response;

import com.gateway.model.SupportedPaymentOperation;
import com.gateway.model.TransactionMode;

import java.util.List;
import java.util.Map;

/**
 * The options available for processing a payment, for example, the credit cards and currencies.
 */
public final class PaymentOptionsResponse {
    private TransactionMode transactionMode;
    private List<Map<String, SupportedPaymentOperation>> supportedPaymentOperations;

    /**
     * The transaction mode available for the merchant
     *
     * @return
     */
    public TransactionMode getTransactionMode() {
        return transactionMode;
    }

    public PaymentOptionsResponse setTransactionMode(TransactionMode transactionMode) {
        this.transactionMode = transactionMode;
        return this;
    }

    public List<Map<String, SupportedPaymentOperation>> getSupportedPaymentOperations() {
        return supportedPaymentOperations;
    }

    public PaymentOptionsResponse setSupportedPaymentOperations(List<Map<String, SupportedPaymentOperation>> supportedPaymentOperations) {
        this.supportedPaymentOperations = supportedPaymentOperations;
        return this;
    }

}