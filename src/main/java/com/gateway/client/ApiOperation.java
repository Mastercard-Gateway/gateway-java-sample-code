/*
 * Copyright (c) 2019 MasterCard. All rights reserved.
 */

package com.gateway.client;

/**
 * The available operations from the API
 */
public enum ApiOperation {
    /**
     * Request to create a payment session. A payment session can be used to temporarily store any of the request fields
     * of operations that allow a session identifier as a request field.
     */
    CREATE_SESSION,
    /**
     * A single transaction to authorise the payment and transfer funds from the payer's account to your account.
     */
    PAY,
    /**
     * Request to add or update request fields contained in the session.
     */
    UPDATE_SESSION,
    /**
     * Request to obtain an authorization for a proposed funds transfer. An authorization is a response from a financial
     * institution indicating that payment information is valid and funds are available in the payers account.
     */
    AUTHORIZE,
    CREATE_CHECKOUT_SESSION,
    RETRIEVE_TRANSACTION,
    RETRIEVE_ORDER,
    UPDATE_AUTHORIZATION,
    VOID,
    REFUND,
    CAPTURE,
    INITIATE_BROWSER_PAYMENT}