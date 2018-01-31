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
    private String currency;
    private String webhooksNotificationSecret;
    private AuthenticationType authenticationType;

    public static String WEBHOOKS_NOTIFICATION_FOLDER = "webhooks-notifications";
    public enum AuthenticationType {CERTIFICATE, PASSWORD}

    public Config(String merchantId, String apiPassword, String apiBaseURL, String gatewayHost, String currency) {

        if (merchantId == null || apiBaseURL == null) {
            throw new IllegalArgumentException("Merchant ID & Api Base URL are required arguments!");
        }

        if (System.getProperty("javax.net.ssl.keyStore") == null && System.getProperty("javax.net.ssl.keyStorePassword") == null && gatewayHost == null && (apiPassword == null || apiPassword.isEmpty())) {
            throw new IllegalArgumentException("Must provide either an API password OR a Java keystore and certificate hostname");
        }

        this.merchantId = merchantId;
        this.currency = currency;
        this.apiBaseURL = apiBaseURL;
        this.apiUsername = "merchant." + this.merchantId;

        if (System.getProperty("javax.net.ssl.keyStore") != null && System.getProperty("javax.net.ssl.keyStorePassword") != null) {
            this.authenticationType = AuthenticationType.CERTIFICATE;
            this.keyStore = System.getProperty("javax.net.ssl.keyStore");
            this.keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
            this.gatewayHost = gatewayHost;
        }
        else if (apiPassword != null) {
            this.authenticationType = AuthenticationType.PASSWORD;
            this.apiPassword = apiPassword;
            this.gatewayHost = this.apiBaseURL;
        }
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

    public String getCurrency() {
        return currency;
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
