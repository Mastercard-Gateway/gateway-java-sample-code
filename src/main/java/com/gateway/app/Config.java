package com.gateway.app;

public class Config {

    private String merchantId;
    private String apiPassword;
    private String apiBaseURL;
    private int apiVersion;
    private String gatewayHost;
    private String apiUsername;
    private String keyStore;
    private String keyStorePassword;
    private String webhooksNotificationSecret;
    private AuthenticationType authenticationType;

    public static String WEBHOOKS_NOTIFICATION_FOLDER = "webhooks-notifications";
    public enum AuthenticationType {CERTIFICATE, PASSWORD}

    public Config(String merchantId, String apiPassword, String apiBaseURL) {

        if (merchantId == null || apiBaseURL == null) {
            throw new IllegalArgumentException("Merchant ID & Api Base URL are required arguments!");
        }

        if (System.getProperty("javax.net.ssl.keyStore") == null && System.getProperty("javax.net.ssl.keyStorePassword") == null && apiPassword == null) {
            throw new IllegalArgumentException("Must provide either an API password or a Java keystore");
        }

        if (System.getProperty("javax.net.ssl.keyStore") != null && System.getProperty("javax.net.ssl.keyStorePassword") != null) {
            this.keyStore = System.getProperty("javax.net.ssl.keyStore");
            this.keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
            this.authenticationType = AuthenticationType.CERTIFICATE;
        }
        else if (apiPassword != null) {
            this.apiPassword = apiPassword;
            this.authenticationType = AuthenticationType.PASSWORD;
        }

        this.merchantId = merchantId;
        this.apiBaseURL = apiBaseURL;
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

    public String getKeyStore() {
        return keyStore;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
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

    public AuthenticationType getAuthenticationType() {
        return authenticationType;
    }
}
