package com.gateway.response;

public class WalletResponse {

    private String allowedCardTypes;
    private String merchantCheckoutId;
    private String requestToken;
    private String originUrl;
    private String orderAmount;
    private String orderCurrency;

    public String getAllowedCardTypes() {
        return allowedCardTypes;
    }

    public void setAllowedCardTypes(String allowedCardTypes) {
        this.allowedCardTypes = allowedCardTypes;
    }

    public String getMerchantCheckoutId() {
        return merchantCheckoutId;
    }

    public void setMerchantCheckoutId(String merchantCheckoutId) {
        this.merchantCheckoutId = merchantCheckoutId;
    }

    public String getRequestToken() {
        return requestToken;
    }

    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getOrderCurrency() {
        return orderCurrency;
    }

    public void setOrderCurrency(String orderCurrency) {
        this.orderCurrency = orderCurrency;
    }
}
