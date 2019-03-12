/*
 * Copyright (c) 2019 MasterCard. All rights reserved.
 */

package com.gateway.client;

/**
 * Indicates the channel in which the authentication request is being initiated.
 * <p>
 * PAYER_BROWSER	Payer is interacting via web browser (for example, with the merchant's ecommerce web-site).
 * <p>
 * MERCHANT_REQUESTED	The merchant is requesting authentication of a cardholder without the payer being available for
 * interaction (for example. as part of processing of a recurring payment).
 */
public enum ApiAuthenticationChannel {
    MERCHANT_REQUESTED,
    PAYER_BROWSER;
}
