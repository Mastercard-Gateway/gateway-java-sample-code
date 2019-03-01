/*
 * Copyright (c) 2019 MasterCard. All rights reserved.
 */

package com.gateway.client;

/**
 * The available operations from the API
 */
public final class ApiOperation {
    private ApiOperation() {
    }

    public static final String CREATE_SESSION = "CREATE_SESSION";
    public static final String PAY = "PAY";
    public static final String UPDATE_SESSION = "UPDATE_SESSION";
    public static final String AUTHORIZE = "AUTHORIZE";
}