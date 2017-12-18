package com.gateway.app;

public enum ApiResponses {

    // General API result
    SUCCESS,
    ERROR,

    // 3DS enrollment check
    CARD_ENROLLED,
    CARD_NOT_ENROLLED,
    CARD_DOES_NOT_SUPPORT_3DS,

    // 3DS authentication responses
    AUTHENTICATION_SUCCESSFUL,
    AUTHENTICATION_FAILED,
    AUTHENTICATION_ATTEMPTED,
    AUTHENTICATION_NOT_AVAILABLE,

    // Gateway codes
    APPROVED,
    DECLINED,
    PENDING,
    CANCELLED,
    INSUFFICIENT_FUNDS,
    NOT_SUPPORTED,
    ACQUIRER_SYSTEM_ERROR,
    SYSTEM_ERROR,
    TIMED_OUT,
    UNKNOWN,

    // Browser payment interaction status
    COMPLETED,
    NOT_AVAILABLE,
    RETURNED_TO_MERCHANT
}
