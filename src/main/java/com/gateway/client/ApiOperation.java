/*
 * Copyright (c) 2019 MasterCard. All rights reserved.
 */

package com.gateway.client;

/**
 * The available operations from the API
 */
public enum ApiOperation {
    CREATE_SESSION,
    PAY,
    UPDATE_SESSION,
    AUTHORIZE,
    CREATE_CHECKOUT_SESSION,
    RETRIEVE_TRANSACTION,
    RETRIEVE_ORDER,
    UPDATE_AUTHORIZATION,
    VOID,
    REFUND,
    CAPTURE
}