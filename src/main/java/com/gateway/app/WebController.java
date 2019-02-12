/*
 * Copyright (c) 2018 MasterCard. All rights reserved.
 */

package com.gateway.app;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import com.gateway.client.ApiException;
import com.gateway.client.ApiProtocol;
import com.gateway.client.ApiRequest;
import com.gateway.client.ApiRequestService;
import com.gateway.client.ApiResponseService;
import com.gateway.client.ExceptionService;
import com.gateway.client.HostedSession;
import com.gateway.client.RESTApiClient;
import com.gateway.client.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import static com.gateway.client.ApiRequestService.ApiOperation.CREATE_SESSION;
import static com.gateway.client.ApiRequestService.ApiOperation.UPDATE_SESSION;

@Controller
public class WebController {

    private static final Logger logger = LoggerFactory.getLogger(WebController.class);

    @Autowired
    public Config config;

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
            req.setOrderCurrency("EUR");
            req.setBrowserPaymentOperation("PAY");
            // NOTE: Uncomment the below for local testing
            //req.setReturnUrl("https://localhost/sample/apmReceipt?merchantId=" + config.getMerchantId() + "&sessionId=" + hostedSession.getId() + "&orderId=" + req.getOrderId() + "&transactionId=" + req.getTransactionId() + "&correlationId=" + correlationId);
            // NOTE: Comment out the below for local testing
            req.setReturnUrl(ApiRequestService.getCurrentContext(httpServletRequest) + "?merchantId=" + config.getMerchantId() + "&sessionId=" + hostedSession.getId() + "&orderId=" + req.getOrderId() + "&transactionId=" + req.getTransactionId() + "&correlationId=" + correlationId);
            ApiRequestService.updateSession(ApiProtocol.REST, req, config, hostedSession.getId());

            mav.setViewName("apm");
            mav.addObject("config", config);
            mav.addObject("apmApiVersion", "1.0.0");
            mav.addObject("hostedSession", hostedSession);
            mav.addObject("request", req);
            mav.addObject("correlationId", correlationId);
        } catch (ApiException e) {
            ExceptionService.constructApiErrorResponse(mav, e);
        } catch (Exception e) {
            ExceptionService.constructGeneralErrorResponse(mav, e);
        }
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

    private static final Map<String, String> currencies = new HashMap<String, String>(){
        {
            put("AUD", "Australian Dollar");
            put("BRL", "Brazilian Real");
            put("CAD", "Canadian Dollar");
            put("CHF", "Swiss Franc");
            put("CZK", "Czech Rep. Koruna");
            put("DKK", "Danish Krone");
            put("EUR", "Euro");
            put("GBP", "UK Pound Sterling");
            put("HKD", "Hong Kong Dollar");
            put("HUF", "Hungarian Forint");
            put("ILS", "Israeli Sheqel");
            put("JPY", "Japanese Yen");
            put("MXN", "Mexican Peso");
            put("MYR", "Malaysian Ringgit");
            put("NOK", "Norwegian Krone");
            put("NZD", "New Zealand Dollar");
            put("PHP", "Philippine Peso");
            put("PLN", "Polish Zloty");
            put("SEK", "Swedish Krona");
            put("SGD", "Singapore Dollar");
            put("THB", "Thai Baht");
            put("TWD", "New Taiwan Dollar");
            put("USD", "US Dollar");
        }
    };

    /**
     * Display 3DSecure-2.0 operation page
     *
     * @param httpServletRequest
     * @return ModelAndView for 3dSecure2.html
     */
    @GetMapping("/3dSecure2")
    public ModelAndView showSecure2Id(HttpServletRequest httpServletRequest) {
        ModelAndView mav = new ModelAndView();

        try {
            RESTApiClient connection = new RESTApiClient();

            //CREATE_SESSION
//            ApiRequest createSessionRequest = ApiRequestService.createApiRequest(CREATE_SESSION, config);
            ApiRequest createSessionRequest = new ApiRequest();
            createSessionRequest.setApiOperation(CREATE_SESSION);
            String requestUrl = ApiRequestService.getSessionRequestUrl(ApiProtocol.REST, config);
            String createSessionRequestPayload = ApiRequestService.buildJSONPayload(createSessionRequest);
            String resp = connection.postTransaction(createSessionRequestPayload, requestUrl, config);
            HostedSession hostedSession = ApiResponseService.parseSessionResponse(resp);

            //UPDATE_SESSION
            ApiRequest sessionRequest = ApiRequestService.createApiRequest(UPDATE_SESSION, config);
            String updateResp = ApiRequestService.update3DSSession(ApiProtocol.REST, sessionRequest, config, hostedSession.getId());
            hostedSession = ApiResponseService.parseSessionResponse(updateResp);
            sessionRequest.setSessionId(hostedSession.getId());

            mav.setViewName("3dSecure2");
            mav.addObject("config", config)
                    .addObject("hostedSession", hostedSession)
                    .addObject("request", sessionRequest);
        } catch (ApiException e) {
            ExceptionService.constructApiErrorResponse(mav, e);
        } catch (Exception e) {
            ExceptionService.constructGeneralErrorResponse(mav, e);
        }
        return mav;
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
            mav.addObject("currencies", currencies);
            mav.addObject("hostedSession", hostedSession);
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
