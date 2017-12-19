package com.gateway.client;

public class CheckoutSession {
    private String id;
    private String version;
    private String successIndicator;

    public CheckoutSession() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSuccessIndicator() {
        return successIndicator;
    }

    public void setSuccessIndicator(String successIndicator) {
        this.successIndicator = successIndicator;
    }
}
