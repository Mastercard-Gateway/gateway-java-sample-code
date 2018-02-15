package com.gateway.client;

public class ApiRequest {

    private String apiOperation;
    private String apiMethod = "PUT";
    private String sourceType;
    private String sourceToken;

    // Order details
    private String orderId;
    private String orderAmount;
    private String orderCurrency;
    private String orderDescription;
    private String transactionId;
    private String targetTransactionId;
    private String transactionAmount;
    private String transactionCurrency;

    // Card data
    private String cardNumber;
    private String expiryMonth;
    private String expiryYear;
    private String securityCode;

    // Browser payments properties
    private String returnUrl;
    private String browserPaymentOperation;
    private String browserPaymentConfirmation;

    // 3DS properties
    private String sessionId;
    private String secureId;
    private String secureIdResponseUrl;
    private String paymentAuthResponse;

    /* essentials_exclude_start */
    // Wallet properties
    private String walletProvider;
    private String masterpassOriginUrl;
    private String masterpassOauthToken;
    private String masterpassOauthVerifier;
    private String masterpassCheckoutUrl;
    /* essentials_exclude_end */

    public String getSourceToken() {
        return sourceToken;
    }

    public void setSourceToken(String sourceToken) {
        this.sourceToken = sourceToken;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getApiOperation() {
        return apiOperation;
    }

    public void setApiOperation(String apiOperation) {
        this.apiOperation = apiOperation;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(String expiryYear) {
        this.expiryYear = expiryYear;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getOrderCurrency() {
        return orderCurrency;
    }

    public void setOrderCurrency(String orderCurrency) {
        this.orderCurrency = orderCurrency;
    }

    public String getOrderDescription() {
        return orderDescription;
    }

    public void setOrderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
    }

    public String getTransactionCurrency() {
        return transactionCurrency;
    }

    public void setTransactionCurrency(String transactionCurrency) {
        this.transactionCurrency = transactionCurrency;
    }

    public String getTargetTransactionId() {
        return targetTransactionId;
    }

    public void setTargetTransactionId(String targetTransactionId) {
        this.targetTransactionId = targetTransactionId;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getBrowserPaymentOperation() {
        return browserPaymentOperation;
    }

    public void setBrowserPaymentOperation(String browserPaymentOperation) {
        this.browserPaymentOperation = browserPaymentOperation;
    }

    public String getBrowserPaymentConfirmation() {
        return browserPaymentConfirmation;
    }

    public void setBrowserPaymentConfirmation(String browserPaymentConfirmation) {
        this.browserPaymentConfirmation = browserPaymentConfirmation;
    }

    public String getApiMethod() {
        return apiMethod;
    }

    public void setApiMethod(String apiMethod) {
         this.apiMethod = apiMethod;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSecureId() {
        return secureId;
    }

    public void setSecureId(String secureId) {
        this.secureId = secureId;
    }

    public String getSecureIdResponseUrl() {
        return secureIdResponseUrl;
    }

    public void setSecureIdResponseUrl(String secureIdResponseUrl) {
        this.secureIdResponseUrl = secureIdResponseUrl;
    }

    public String getPaymentAuthResponse() {
        return paymentAuthResponse;
    }

    public void setPaymentAuthResponse(String paymentAuthResponse) {
        this.paymentAuthResponse = paymentAuthResponse;
    }

    public String getWalletProvider() {
        return walletProvider;
    }

    public void setWalletProvider(String walletProvider) {
        this.walletProvider = walletProvider;
    }

    /* essentials_exclude_start */
    public String getMasterpassOriginUrl() {
        return masterpassOriginUrl;
    }

    public void setMasterpassOriginUrl(String originUrl) {
        this.masterpassOriginUrl = originUrl;
    }

    public String getMasterpassOauthToken() {
        return masterpassOauthToken;
    }

    public void setMasterpassOauthToken(String masterpassOauthToken) {
        this.masterpassOauthToken = masterpassOauthToken;
    }

    public String getMasterpassOauthVerifier() {
        return masterpassOauthVerifier;
    }

    public void setMasterpassOauthVerifier(String masterpassOauthVerifier) {
        this.masterpassOauthVerifier = masterpassOauthVerifier;
    }

    public String getMasterpassCheckoutUrl() {
        return masterpassCheckoutUrl;
    }

    public void setMasterpassCheckoutUrl(String masterpassCheckoutUrl) {
        this.masterpassCheckoutUrl = masterpassCheckoutUrl;
    }
    /* essentials_exclude_end */
}