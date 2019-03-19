/*
 * Copyright (c) 2019 MasterCard. All rights reserved.
 */

package com.gateway.response;

import com.gateway.model.TransactionMode;

/**
 * The options available for processing a payment, for example, the credit cards and currencies.
 */
public final class PaymentOptionsResponse {
    private TransactionMode transactionMode;

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

}