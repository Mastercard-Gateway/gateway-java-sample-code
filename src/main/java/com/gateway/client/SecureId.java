package com.gateway.client;

public class SecureId {

    private String id;
    private String status;
    private String redirectUrl;
    private String htmlBodyContent;

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

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getHtmlBodyContent() {
        return htmlBodyContent;
    }

    public void setHtmlBodyContent(String htmlBodyContent) {
        this.htmlBodyContent = htmlBodyContent;
    }
}
