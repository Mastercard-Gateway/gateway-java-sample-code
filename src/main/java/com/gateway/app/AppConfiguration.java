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

    @Value("${gateway.base.url}")
    private String baseURL;

    @Value("${gateway.certificate.url}")
    private String gatewayHost;

    @Value("${gateway.api.version}")
    private String apiVersion;

    @Value("${webhooks.notification.secret}")
    private String webhooksNotificationSecret;

    @Value("${javax.net.ssl.keyStore}")
    private String keyStore;

    @Value("${javax.net.ssl.keyStorePassword}")
    private String keyStorePassword;

    @Bean
    public Config buildConfig() {

        Config config = new Config(merchantId, apiPassword, baseURL, gatewayHost);

        config.setApiVersion(Integer.parseInt(apiVersion));

        if (webhooksNotificationSecret != null) {
            config.setWebhooksNotificationSecret(webhooksNotificationSecret);
        }

        return config;
    }
}
