package com.gateway.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

    @Bean
    public Config buildConfig() {
        //TODO: Check if these system props exists, otherwise terminate the process
        return new Config(System.getenv("GATEWAY_MERCHANT_ID"), System.getenv("GATEWAY_API_PASSWORD"), System.getenv("GATEWAY_BASE_URL"), Integer.valueOf(System.getenv("GATEWAY_API_VERSION")));
    }
}
