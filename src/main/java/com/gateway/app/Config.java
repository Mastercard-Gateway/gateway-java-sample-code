package com.gateway.app;

public class Config {

    private String merchantId;
    private String apiPassword;
    private String apiBaseURL;
    private int apiVersion;
    private String gatewayHost;
    private String apiUsername;
    private String trustStorePath;
    private String trustStorePassword;
    private String webhooksNotificationSecret;


    public static String WEBHOOKS_NOTIFICATION_FOLDER = "webhooks-notifications";

    public Config(String merchantId, String apiPassword, String apiBaseURL) {

        // [Snippet] howToConfigureSslCert - start
        // If using certificate validation, modify the following configuration settings

        // alternate trust store file
        // leave as null if you use default java trust store
        String trustStore = null;
        // trust store password
        String trustStorePassword = null;

        if (trustStore != null) {
            System.setProperty("javax.net.ssl.trustStore", trustStore);
            System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
        }
        // [Snippet] howToConfigureSslCert - end

        this.merchantId = merchantId;
        this.apiPassword = apiPassword;
        this.apiBaseURL = apiBaseURL;

        if (merchantId == null || apiPassword == null || apiBaseURL == null) {
            throw new IllegalArgumentException("Merchant ID, Api Password & Api Base URL are required arguments!");
        }

        this.gatewayHost = this.apiBaseURL;
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
