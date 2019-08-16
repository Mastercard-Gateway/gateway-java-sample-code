/*
 * Copyright (c) 2019 MasterCard. All rights reserved.
 */

package com.gateway.response;

import java.util.List;

import com.gateway.model.SupportedPaymentOperation;
import com.gateway.model.TransactionMode;

/**
 * The options available for processing a payment, for example, the credit cards and currencies.
 */
public final class PaymentOptionsResponse {
    private TransactionMode transactionMode;
    private List<SupportedPaymentOperation> supportedPaymentOperations;

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

    /**
     * The supported payment operations available for the merchant (aka transaction mode for api version 51 backwards)
     *
     * @return
     */
    public List<SupportedPaymentOperation> getSupportedPaymentOperations() {
        return supportedPaymentOperations;
    }

    public PaymentOptionsResponse setSupportedPaymentOperations(
            List<SupportedPaymentOperation> supportedPaymentOperations) {
        this.supportedPaymentOperations = supportedPaymentOperations;
        return this;
    }

}
