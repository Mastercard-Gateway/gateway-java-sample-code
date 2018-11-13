package com.gateway.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

    @Value("${gateway.merchant.id}")
    private String merchantId;

    @Value("${gateway.api.password}")
    private String apiPassword;

    @Value("${gateway.currency}")
    private String currency;

    @Value("${gateway.base.url}")
    private String baseURL;

    @Value("${gateway.certificate.url}")
    private String gatewayHost;

    @Value("${gateway.api.version}")
    private String apiVersion;

    @Value("${gateway.keystore.path}")
    private String keystore;

    @Value("${gateway.keystore.password}")
    private String keystorePassword;

    @Value("${webhooks.notification.secret}")
    private String webhooksNotificationSecret;

    @Bean
    public Config buildConfig() {

        Config config = new Config();

        if (merchantId == null || baseURL == null || currency == null) {
            throw new IllegalArgumentException("Merchant ID, API base URL, and currency are required arguments!");
        }

        if ((keystore == null || keystore.isEmpty()) && (keystorePassword == null || keystorePassword.isEmpty()) && gatewayHost == null && (apiPassword == null || apiPassword.isEmpty())) {
            throw new IllegalArgumentException("Must provide either an API password OR a Java keystore and certificate hostname");
        }

        if (keystore != null && !keystore.isEmpty() && keystorePassword != null && !keystorePassword.isEmpty()) {
            config.setAuthenticationType(Config.AuthenticationType.CERTIFICATE);
            config.setKeyStore(keystore);
            config.setKeyStorePassword(keystorePassword);
            config.setGatewayHost(gatewayHost);
        }
        else if (apiPassword != null) {
            config.setAuthenticationType(Config.AuthenticationType.PASSWORD);
            config.setApiPassword(apiPassword);
            config.setGatewayHost(baseURL);
        }

        if (webhooksNotificationSecret != null) {
            config.setWebhooksNotificationSecret(webhooksNotificationSecret);
        }

        config.setMerchantId(merchantId);
        config.setApiBaseURL(baseURL);
        config.setApiUsername("merchant." + merchantId);
        config.setCurrency(currency);
        config.setApiVersion(Integer.parseInt(apiVersion));

        return config;
    }
}
