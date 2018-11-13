package com.gateway.response;

public class SecureIdEnrollmentResponse {

    private String id;
    private String status;
    private String responseUrl;
    private String acsUrl;
    private String paReq;
    private String mdValue;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponseUrl() {
        return responseUrl;
    }

    public void setResponseUrl(String responseUrl) {
        this.responseUrl = responseUrl;
    }

    public String getAcsUrl() {
        return acsUrl;
    }

    public void setAcsUrl(String acsUrl) {
        this.acsUrl = acsUrl;
    }

    public String getPaReq() {
        return paReq;
    }

    public void setPaReq(String paReq) {
        this.paReq = paReq;
    }

    public String getMdValue() {
        return mdValue;
    }

    public void setMdValue(String mdValue) {
        this.mdValue = mdValue;
    }
}
