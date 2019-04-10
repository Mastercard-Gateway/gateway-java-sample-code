/*
 * Copyright (c) 2019 MasterCard. All rights reserved.
 */

package com.gateway.model;

/**
 * Information on the supported payment operations available to the merchant.
 * The list of supported payment operations available to the merchant.
 */
public enum SupportedPaymentOperation {
    /**
     * Merchant has privilege to perform Authorizations.
     */
    AUTHORIZE,
    /**
     * Merchant has privilege to perform Purchases
     */
    PURCHASE
}
