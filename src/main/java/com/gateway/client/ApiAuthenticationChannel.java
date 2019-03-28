/*
 * Copyright (c) 2019 MasterCard. All rights reserved.
 */

package com.gateway.client;

/**
 * Indicates the channel in which the authentication request is being initiated.
 */
public enum ApiAuthenticationChannel {
    /**
     * Payer is interacting via web browser (for example, with the merchant's e-commerce web-site)
     */
    MERCHANT_REQUESTED,
    /**
     * The merchant is requesting authentication of a cardholder without the payer being available for interaction (for
     * example. as part of processing of a recurring payment).
     */
    PAYER_BROWSER;
}
