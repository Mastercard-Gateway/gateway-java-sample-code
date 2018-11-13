package com.gateway.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

public class ExceptionService {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionService.class);

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
