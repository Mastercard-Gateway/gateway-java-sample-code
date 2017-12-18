package com.gateway.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

    @Bean
    public Config buildConfig() {
        Config config = new Config(getEnv("GATEWAY_MERCHANT_ID"), getEnv("GATEWAY_API_PASSWORD"), getEnv("GATEWAY_BASE_URL"));


        String api_version = getEnv("GATEWAY_API_VERSION");

        if (api_version != null) {
            config.setApiVersion(Integer.parseInt(api_version));
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
