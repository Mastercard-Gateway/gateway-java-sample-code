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


    @Bean
    public Config buildConfig() {
        Config config = new Config(merchantId, apiPassword, baseURL);


        String api_version = getEnv("GATEWAY_API_VERSION");

        if (api_version != null) {
            config.setApiVersion(Integer.parseInt(api_version));
        } else {
            config.setApiVersion(Config.DEFAULT_API_VERSION);
        }


        String notificationSecret = getEnv("WEBHOOKS_NOTIFICATION_SECRET");

        if (notificationSecret != null) {
            config.setWebhooksNotificationSecret(notificationSecret);
        }

        return config;
    }

    private String getEnv(String envVariable) {
        return System.getenv(envVariable) != null && System.getenv(envVariable).trim().length() > 0 ? System.getenv(envVariable).trim() : null;
    }
}
