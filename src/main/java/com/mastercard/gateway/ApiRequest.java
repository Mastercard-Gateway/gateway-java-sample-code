package com.mastercard.gateway;

public class ApiRequest {

    private String orderId;
    private String transactionId;
    private String apiOperation;
    private String sourceType;
    private String cardNumber;
    private String expiryMonth;
    private String expiryYear;
    private String securityCode;
    private String orderAmount;
    private String transactionAmount;
    private String orderCurrency;
    private String transactionCurrency;
    private String targetTransactionId;
    private String returnUrl;
    private String browserPaymentOperation;
    private String browserPaymentConfirmation;
    private String method;

    public ApiRequest(){}

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

    public void setBrowserPaymentConfirmation(String browerPaymentConfirmation) {
        this.browserPaymentConfirmation = browerPaymentConfirmation;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public static ApiRequest createTestRequest(String apiOperation) {
        ApiRequest req = new ApiRequest();

        req.apiOperation = apiOperation;
        req.method = "PUT";
        req.sourceType = "CARD";
        req.cardNumber = "5123450000000008";
        req.expiryMonth = "5";
        req.expiryYear = "21";
        req.securityCode = "100";
        req.orderAmount = "5000";
        req.transactionAmount = "5000";
        req.orderCurrency = "USD";
        req.transactionCurrency = "USD";
        req.returnUrl = "http://localhost:5000/browserPaymentReceipt";

        return req;
    }
}