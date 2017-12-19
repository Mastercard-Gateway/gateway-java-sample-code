package com.gateway.client;

public class ErrorResponse extends Response {

    private String cause;
    private String message;

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }
}
