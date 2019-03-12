/*
 * Copyright (c) 2018 MasterCard. All rights reserved.
 */

package com.gateway.app;

import com.gateway.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class WebController {

    private static final Logger logger = LoggerFactory.getLogger(WebController.class);

    @Autowired
    public Config config;

    /**
     * If -Dlocal=https (or http) argument is passed, then the base url will be pointing to http(s)://localhost,
     * otherwise base url will point to the GATEWAY_BASE_URL passed in the env variables.
     * @return  String base url
     */
    private String getBaseUrl() {
        return System.getProperty("local") != null ? System.getProperty("local") + "://localhost" : config.getApiBaseURL();
    }

    /**
     * Display AUTHORIZE operation page
     *
     * @return ModelAndView for authorize.html
     */
    @GetMapping("/authorize")
    public ModelAndView showAuthorize() {
        return createHostedSessionModel("authorize");
    }

    /**
     * Display PAY operation page
     *
     * @return ModelAndView for pay.html
     */
    @GetMapping("/pay")
    public ModelAndView showPay() {
        return createHostedSessionModel("pay");
    }

    /**
     * Display page for PAY operation page using tokenization
     *
     * @return ModelAndView for payWithToken.html
     */
    @GetMapping("/payWithToken")
    public ModelAndView showPayWithToken() {

        ModelAndView mav = new ModelAndView();

        try {
            ApiRequest req = new ApiRequest();
            req.setSourceType("CARD");
            req.setOrderId(Utils.createUniqueId("order-"));
            req.setOrderAmount("50.00");
            req.setOrderCurrency(config.getCurrency());
            req.setOrderDescription("Wonderful product that you should buy!");
            req.setTransactionId(Utils.createUniqueId("trans-"));
            mav.setViewName("payWithToken");
            mav.addObject("request", req);
            mav.addObject("config", config);
        } catch (Exception e) {
            ExceptionService.constructGeneralErrorResponse(mav, e);
        }

        return mav;
    }

    /**
     * Display PAY operation page for NVP mode
     *
     * @return ModelAndView for pay.html
     */
    @GetMapping("/payThroughNVP")
    public ModelAndView showPayThroughNVP() {
        return createHostedSessionModel("payThroughNVP");
    }

    /**
     * Display VERIFY operation page
     *
     * @return ModelAndView for verify.html
     */
    @GetMapping("/verify")
    public ModelAndView showVerify() {
        return createHostedSessionModel("verify");
    }

    /* essentials_exclude_start */
    /* targeted_exclude_start */
    /**
     * Display page for PayPal browser payment
     *
     * @param request Get request URL hostname to properly set the redirect URL
     * @return ModelAndView for paypal.html
     */
    @GetMapping("/paypal")
    public ModelAndView showPaypal(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();

        try {
            ApiRequest req = ApiRequestService.createBrowserPaymentsRequest(request, "PAY", "PAYPAL", config);
            mav.setViewName("paypal");
            mav.addObject("apiRequest", req);
            mav.addObject("config", config);
        } catch (Exception e) {
            ExceptionService.constructGeneralErrorResponse(mav, e);
        }

        return mav;
    }
    /* targeted_exclude_end */
    /* essentials_exclude_end */

    /* essentials_exclude_start */
    /**
     * Display page for UnionPay SecurePay browser payment
     *
     * @param request Get request URL hostname to properly set the redirect URL
     * @return ModelAndView for unionpay.html
     */
    @GetMapping("/unionpay")
    public ModelAndView showUnionPay(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();

        try {
            ApiRequest req = ApiRequestService.createBrowserPaymentsRequest(request, "AUTHORIZE", "UNION_PAY", config);
            mav.setViewName("unionpay");
            mav.addObject("apiRequest", req);
            mav.addObject("config", config);
        } catch (Exception e) {
            ExceptionService.constructGeneralErrorResponse(mav, e);
        }

        return mav;
    }
    /* essentials_exclude_end */

    /**
     * Display page for alternate payments methods using apm.js
     *
     * @return ModelAndView for apm.html
     */
    @GetMapping("/apm")
    public ModelAndView showAPMs(HttpServletRequest httpServletRequest) {
        ModelAndView mav = new ModelAndView();

        ApiRequest req = new ApiRequest();
        req.setApiOperation("CREATE_SESSION");
        req.setOrderId(Utils.createUniqueId("order-"));
        req.setTransactionId(Utils.createUniqueId("trans-"));

        String requestUrl = ApiRequestService.getSessionRequestUrl(ApiProtocol.REST, config);

        try {
            RESTApiClient connection = new RESTApiClient();
            String resp = connection.postTransaction(requestUrl, config);

            HostedSession hostedSession = ApiResponseService.parseSessionResponse(resp);

            String correlationId = Utils.createUniqueId("APM_");
            req.setApiOperation("UPDATE_SESSION");
            req.setOrderAmount("50.00");
            req.setOrderCurrency(config.getCurrency());
            req.setBrowserPaymentOperation("PAY");
            // NOTE: Uncomment the below for local testing
            // req.setReturnUrl("https://localhost/sample/apmReceipt?merchantId=" + config.getMerchantId() + "&sessionId=" + hostedSession.getId() + "&orderId=" + req.getOrderId() + "&transactionId=" + req.getTransactionId() + "&correlationId=" + correlationId);
            // NOTE: Comment out the below for local testing
            req.setReturnUrl(ApiRequestService.getCurrentContext(httpServletRequest) + "?merchantId=" + config.getMerchantId() + "&sessionId=" + hostedSession.getId() + "&orderId=" + req.getOrderId() + "&transactionId=" + req.getTransactionId() + "&correlationId=" + correlationId);
            ApiRequestService.updateSession(ApiProtocol.REST, req, config, hostedSession.getId());

            mav.setViewName("apm");
            mav.addObject("config", config);
            mav.addObject("apmApiVersion", config.getApmVersion());
            mav.addObject("hostedSession", hostedSession);
            mav.addObject("request", req);
            mav.addObject("baseUrl", getBaseUrl());
            mav.addObject("correlationId", correlationId);
        } catch (ApiException e) {
            ExceptionService.constructApiErrorResponse(mav, e);
        } catch (Exception e) {
            ExceptionService.constructGeneralErrorResponse(mav, e);
        }
        return mav;
    }

    /**
     * APM receipt page
     *
     * @return ModelAndView for apmReceipt.html
     */
    @GetMapping("/apmReceipt")
    public ModelAndView showAPMReceipt() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("config", config);
        mav.addObject("baseUrl", getBaseUrl());
        mav.addObject("apmApiVersion", config.getApmVersion());
        mav.setViewName("apmReceipt");
        return mav;
    }

    /* essentials_exclude_start */
    /**
     * Show Masterpass page - this is only for demonstration purposes so that the user of this sample code can enter API payload details
     * @param request used to determine context for return URL
     * @return ModelAndView for masterpass.html
     */
    @GetMapping("/masterpass")
    public ModelAndView showMasterpass(HttpServletRequest request) {

        ModelAndView mav = new ModelAndView();

        try {
            mav.setViewName("masterpass");
            ApiRequest req = new ApiRequest();
            req.setOrderId(Utils.createUniqueId("order-"));
            req.setOrderAmount("50.00");
            req.setOrderCurrency(config.getCurrency());
            req.setOrderDescription("Wonderful product that you should buy!");
            req.setWalletProvider("MASTERPASS_ONLINE");
            req.setMasterpassOriginUrl(ApiRequestService.getCurrentContext(request) + "/masterpassResponse");
            mav.addObject("apiRequest", req);
            mav.addObject("config", config);
        }
        catch(Exception e) {
            ExceptionService.constructGeneralErrorResponse(mav, e);
        }

        return mav;
    }
    /* essentials_exclude_end */

    /**
     * Display CAPTURE operation page
     *
     * @return ModelAndView for capture.html
     */
    @GetMapping("/capture")
    public ModelAndView showCapture() {
        ModelAndView mav = new ModelAndView("capture");
        ApiRequest req = ApiRequestService.createApiRequest("CAPTURE", config);
        mav.addObject("apiRequest", req);
        return mav;
    }

    /**
     * Display REFUND operation page
     *
     * @return ModelAndView for refund.html
     */
    @GetMapping("/refund")
    public ModelAndView showRefund() {
        ModelAndView mav = new ModelAndView("refund");
        ApiRequest req = ApiRequestService.createApiRequest("REFUND", config);
        mav.addObject("apiRequest", req);
        return mav;
    }

    /**
     * Display RETRIEVE_TRANSACTION operation page
     *
     * @return ModelAndView for retrieve.html
     */
    @GetMapping("/retrieve")
    public ModelAndView showRetrieve() {
        ModelAndView mav = new ModelAndView("retrieve");
        ApiRequest req = ApiRequestService.createApiRequest("RETRIEVE_TRANSACTION", config);
        mav.addObject("apiRequest", req);
        return mav;
    }

    /**
     * Display VOID operation page
     *
     * @return ModelAndView for void.html
     */
    @GetMapping("/void")
    public ModelAndView showVoid() {
        ModelAndView mav = new ModelAndView("void");
        ApiRequest req = ApiRequestService.createApiRequest("VOID", config);
        req.setTransactionId(Utils.createUniqueId("trans-"));
        mav.addObject("apiRequest", req);
        return mav;
    }

    /**
     * Display 3DSecure operation page
     *
     * @return ModelAndView for 3dSecure.html
     */
    @GetMapping("/3dSecure")
    public ModelAndView showSecureId() {
        return createHostedSessionModel("3dSecure");
    }

    /**
     * Display page for Hosted Checkout operation
     *
     * @return ModelAndView for hostedCheckout.html
     */
    @GetMapping("/hostedCheckout")
    public ModelAndView showHostedCheckout() {

        ModelAndView mav = new ModelAndView();

        ApiRequest req = new ApiRequest();
        req.setApiOperation("CREATE_SESSION");

        String requestUrl = ApiRequestService.getSessionRequestUrl(ApiProtocol.REST, config);

        String data = ApiRequestService.buildJSONPayload(req);

        try {
            RESTApiClient connection = new RESTApiClient();
            String resp = connection.postTransaction(data, requestUrl, config);

            HostedSession hostedSession = ApiResponseService.parseSessionResponse(resp);

            mav.setViewName("hostedCheckout");
            mav.addObject("config", config);
            mav.addObject("hostedSession", hostedSession);
            mav.addObject("baseUrl", getBaseUrl());
        } catch (ApiException e) {
            ExceptionService.constructApiErrorResponse(mav, e);
        } catch (Exception e) {
            ExceptionService.constructGeneralErrorResponse(mav, e);
        }
        return mav;
    }

    /**
     * Constructs view model with prefilled data for Hosted Session requests
     *
     * @param viewName
     * @return mav
     */
    private ModelAndView createHostedSessionModel(String viewName) {
        ModelAndView mav = new ModelAndView(viewName);

        // Add some prefilled data - can be changed by user
        ApiRequest request = new ApiRequest();
        request.setOrderId(Utils.createUniqueId("order-"));
        request.setTransactionId(Utils.createUniqueId("trans-"));
        request.setOrderAmount("50.00");
        request.setOrderCurrency(config.getCurrency());
        request.setOrderDescription("Wonderful product that you should buy!");

        mav.addObject("request", request);
        mav.addObject("config", config);

        return mav;
    }
}