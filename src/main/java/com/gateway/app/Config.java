package com.gateway.app;

public class Config {

    private String merchantId;
    private String apiPassword;
    private String apiBaseURL;
    private int apiVersion;
    private String gatewayHost;
    private String apiUsername;
    private String proxyServer;
    private int proxyPort;
    private String proxyUsername;
    private String proxyPassword;
    private String proxyAuthType;
    private String ntDomain;
    private String trustStorePath;
    private String trustStorePassword;

    public Config(String merchantId, String apiPassword, String apiBaseURL, int apiVersion) {
        this.merchantId = merchantId;
        this.apiPassword = apiPassword;
        this.apiBaseURL = apiBaseURL;
        this.apiVersion = apiVersion;
        this.gatewayHost = this.apiBaseURL + "/api/rest";
        this.apiUsername = "merchant." + this.merchantId;
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

    public int getApiVersion() {
        return apiVersion;
    }

    public String getGatewayHost() {
        return gatewayHost;
    }

    public String getApiUsername() {
        return apiUsername;
    }

    public String getProxyServer() {
        return proxyServer;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public String getProxyUsername() {
        return proxyUsername;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public String getProxyAuthType() {
        return proxyAuthType;
    }

    public String getNtDomain() {
        return ntDomain;
    }

    public String getTrustStorePath() {
        return trustStorePath;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }
}
