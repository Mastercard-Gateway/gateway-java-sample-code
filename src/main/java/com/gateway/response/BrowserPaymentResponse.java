package com.gateway.response;

public class BrowserPaymentResponse extends TransactionResponse {

    private String acquirerMessage;
    private String interactionStatus;

    public String getInteractionStatus() {
        return interactionStatus;
    }

    public void setInteractionStatus(String interactionStatus) {
        this.interactionStatus = interactionStatus;
    }

    public String getAcquirerMessage() {
        return acquirerMessage;
    }

    public void setAcquirerMessage(String acquirerMessage) {
        this.acquirerMessage = acquirerMessage;
    }
}
