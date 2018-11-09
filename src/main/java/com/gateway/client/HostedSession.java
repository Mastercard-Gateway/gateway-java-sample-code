/*
 * Copyright (c) 2018 MasterCard. All rights reserved.
 */

package com.gateway.client;

public class HostedSession {

    private String id;
    private String version;
    private String successIndicator;
    private String updateStatus;

    public HostedSession() {}

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

    public HostedSession setUpdateStatus(String updateStatus) {
        this.updateStatus = updateStatus;
        return this;
    }

    public String getUpdateStatus() {
        return updateStatus;
    }
}
