/*
 * Copyright (c) 2019 MasterCard. All rights reserved.
 */

package com.gateway.app;

import java.util.List;

import com.gateway.model.SupportedPaymentOperation;
import com.gateway.model.TransactionMode;

public class Config {

    private final String httpProxyHost;
    private final Integer httpProxyPort;
    private String merchantId;
    private String apiPassword;
    private String apiBaseURL;
    private int apiVersion;
    private String apmVersion;
    private String gatewayHost;
    private String apiUsername;
    private String apiThreeDsVersion;
    private String keyStore;
    private String keyStorePassword;
    private String currency;
    private String webhooksNotificationSecret;
    private AuthenticationType authenticationType;
    private TransactionMode transactionMode;
    private List<SupportedPaymentOperation> supportedPaymentOperations;


    public List<SupportedPaymentOperation> getSupportedPaymentOperations() { return supportedPaymentOperations; }

    public Config setSupportedPaymentOperations(List<SupportedPaymentOperation> supportedPaymentOperations) {
        this.supportedPaymentOperations = supportedPaymentOperations;
        return this;
    }

    public TransactionMode getTransactionMode() {
        return transactionMode;
    }

    public Config setTransactionMode(TransactionMode transactionMode) {
        this.transactionMode = transactionMode;
        return this;
    }

    public static String WEBHOOKS_NOTIFICATION_FOLDER = "webhooks-notifications";

    public Config() {
        httpProxyHost = System.getProperty("http.proxyHost");
        httpProxyPort = System.getProperty("http.proxyPort") != null ?
                Integer.valueOf(System.getProperty("http.proxyPort")) : null;
    }

    public String getHttpProxyHost() {
        return httpProxyHost;
    }

    public Integer getHttpProxyPort() {
        return httpProxyPort;
    }

    public enum AuthenticationType {CERTIFICATE, PASSWORD}

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getApiPassword() {
        return apiPassword;
    }

    public void setApiPassword(String apiPassword) {
        this.apiPassword = apiPassword;
    }

    public String getApiBaseURL() {
        return apiBaseURL;
    }

    public void setApiBaseURL(String apiBaseURL) {
        this.apiBaseURL = apiBaseURL;
    }

    public int getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(int apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getApmVersion() {
        return apmVersion;
    }

    public void setApmVersion(String apmVersion) {
        this.apmVersion = apmVersion;
    }

    public String getGatewayHost() {
        return gatewayHost;
    }

    public void setGatewayHost(String gatewayHost) {
        this.gatewayHost = gatewayHost;
    }

    public String getApiUsername() {
        return apiUsername;
    }

    public void setApiUsername(String apiUsername) {
        this.apiUsername = apiUsername;
    }

    public String getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getWebhooksNotificationSecret() {
        return webhooksNotificationSecret;
    }

    public void setWebhooksNotificationSecret(String webhooksNotificationSecret) {
        this.webhooksNotificationSecret = webhooksNotificationSecret;
    }

    public AuthenticationType getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(AuthenticationType authenticationType) {
        this.authenticationType = authenticationType;
    }

    public void setApiThreeDsVersion(String apiThreeDsVersion) {
        this.apiThreeDsVersion = apiThreeDsVersion;
    }

    public String getApiThreeDsVersion() {
        return apiThreeDsVersion;
    }
}
