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

    /**
     * Constructs the view model for an API error response
     *
     * @param mav model from controller
     * @param e ApiException
     * @return mav
     */
    public static ModelAndView constructApiErrorResponse(ModelAndView mav, ApiException e) {
        mav.setViewName("error");
        logger.error(e.getMessage());
        mav.addObject("errorCode", e.getErrorCode());
        mav.addObject("explanation", e.getExplanation());
        mav.addObject("field", e.getField());
        mav.addObject("validationType", e.getValidationType());
        return mav;
    }

    /**
     * Constructs the view model for a general error response
     *
     * @param mav model from controller
     * @param e Exception
     * @return mav
     */
    public static ModelAndView constructGeneralErrorResponse(ModelAndView mav, Exception e) {
        mav.setViewName("error");
        logger.error("An error occurred", e);
        mav.addObject("cause", e.getCause());
        mav.addObject("message", e.getMessage());
        return mav;
    }
}
