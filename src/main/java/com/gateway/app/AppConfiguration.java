package com.gateway.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

    @Bean
    public Config buildConfig() {
        //TODO: Check if these system props exists, otherwise terminate the process
        System.out.println("################### GATEWAY_MERCHANT_ID" + System.getenv("GATEWAY_MERCHANT_ID"));
        System.out.println("################### GATEWAY_API_PASSWORD" + System.getenv("GATEWAY_API_PASSWORD"));
        System.out.println("################### GATEWAY_BASE_URL" + System.getenv("GATEWAY_BASE_URL"));
        System.out.println("################### GATEWAY_API_VERSION" + System.getenv("GATEWAY_API_VERSION"));
        return new Config(System.getenv("GATEWAY_MERCHANT_ID"), System.getenv("GATEWAY_API_PASSWORD"), System.getenv("GATEWAY_BASE_URL"), Integer.valueOf(System.getenv("GATEWAY_API_VERSION")));
    }
}
