package com.gateway.app;

public enum ApiResponses {

    // 3DS enrollment check
    CARD_ENROLLED,
    CARD_NOT_ENROLLED,
    CARD_DOES_NOT_SUPPORT_3DS,

    // 3DS authentication responses
    AUTHENTICATION_SUCCESSFUL,
    AUTHENTICATION_FAILED,
    AUTHENTICATION_ATTEMPTED,
    AUTHENTICATION_NOT_AVAILABLE
}
