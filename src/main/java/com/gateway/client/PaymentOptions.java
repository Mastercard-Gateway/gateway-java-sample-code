/*
 * Copyright (c) 2019 MasterCard. All rights reserved.
 */

package com.gateway.client;

/**
 * The options available for processing a payment, for example, the credit cards and currencies.
 */
public final class PaymentOptions {
    enum TransactionMode {AUTHORIZE_CAPTURE, PURCHASE}

    private TransactionMode transactionMode;

    public TransactionMode getTransactionMode() {
        return transactionMode;
    }

    public PaymentOptions setTransactionMode(TransactionMode transactionMode) {
        this.transactionMode = transactionMode;
        return this;
    }

}