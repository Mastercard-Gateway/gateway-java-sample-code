package com.mastercard.gateway;

public class Config {

    private String merchantId;
    private String apiPassword;
    private String apiBaseURL;

    public Config(String merchantId, String apiPassword, String apiBaseURL) {
        this.merchantId = merchantId;
        this.apiPassword = apiPassword;
        this.apiBaseURL = apiBaseURL;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getApiPassword() {
        return apiPassword;
    }

    public String getApiBaseURL() {
        return apiBaseURL;
    }
}
