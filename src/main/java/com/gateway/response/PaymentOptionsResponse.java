/*
 * Copyright (c) 2019 MasterCard. All rights reserved.
 */

package com.gateway.response;

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

    /**
     * Defines the merchants transaction mode, i.e. if the funds are immediately requested to be moved from the payer's
     * account or the payment is authorized only and the funds will be moved/captured later.
     */
    public enum TransactionMode {
        /**
         * The payment is authorized only (AUTHORIZATION transaction). The merchant must submit a Capture request to
         * move the funds from the payer's account to the merchant's account.
         */
        AUTHORIZE_CAPTURE,
        /**
         * The payment happens immediately (PURCHASE transaction). The funds are immediately moved from the payer's
         * account to the merchant's account.
         */
        PURCHASE
    }

}