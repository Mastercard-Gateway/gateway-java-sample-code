package com.gateway.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

public class ApiException extends Exception {

    private static final Logger logger = LoggerFactory.getLogger(ApiException.class);

    private String errorCode;
    private String explanation;
    private String field;
    private String validationType;

    public ApiException(String message) {
        super(message);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValidationType() {
        return validationType;
    }

    public void setValidationType(String validationType) {
        this.validationType = validationType;
    }

}
