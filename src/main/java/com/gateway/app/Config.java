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
    private String webhooksNotificationSecret;


    public static String WEBHOOKS_NOTIFICATION_FOLDER = "webhooks-notifications";
    public static int DEFAULT_API_VERSION = 45;

    public Config(String merchantId, String apiPassword, String apiBaseURL) {
        this.merchantId = merchantId;
        this.apiPassword = apiPassword;
        this.apiBaseURL = apiBaseURL;

        if (merchantId == null || apiPassword == null || apiBaseURL == null) {
            throw new IllegalArgumentException("Merchant ID, Api Password & Api Base URL are required arguments!");
        }

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

    public String getWebhooksNotificationSecret() {
        return webhooksNotificationSecret;
    }

    public void setWebhooksNotificationSecret(String webhooksNotificationSecret) {
        this.webhooksNotificationSecret = webhooksNotificationSecret;
    }

    public void setApiVersion(int apiVersion) {
        this.apiVersion = apiVersion;
    }


}
