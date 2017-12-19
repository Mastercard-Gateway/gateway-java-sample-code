package com.gateway.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gateway.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class WebController {

    private static final Logger logger = LoggerFactory.getLogger(WebController.class);

    @Autowired
    public Config config;

    /**
     * Display AUTHORIZE operation page
     * @return ModelAndView for authorize.html
     */
    @GetMapping("/authorize")
    public ModelAndView showAuthorize() {
        return createModel("authorize");
    }

    /**
     * Display PAY operation page
     * @return ModelAndView for pay.html
     */
    @GetMapping("/pay")
    public ModelAndView showPay() {
        return createModel("pay");
    }

    /**
     * Display VERIFY operation page
     * @return ModelAndView for verify.html
     */
    @GetMapping("/verify")
    public ModelAndView showVerify() {
        return createModel("verify");
    }

    /**
     * Display page for PayPal browser payment
     * @param request Get request URL hostname to properly set the redirect URL
     * @return ModelAndView for paypal.html
     */
    @GetMapping("/paypal")
    public ModelAndView showPaypal(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();

        try {
            ApiRequest req = ClientUtil.createBrowserPaymentsRequest("PAY", "PAYPAL", request.getRequestURL().toString());
            mav.setViewName("paypal");
            mav.addObject("apiRequest", req);
        }
        catch (Exception e) {
            mav.setViewName("error");
            logger.error("An error occurred", e);
            mav.addObject("cause", e.getCause());
            mav.addObject("message", e.getMessage());
        }

        return mav;
    }

    /**
     * Display page for UnionPay SecurePay browser payment
     * @param request Get request URL hostname to properly set the redirect URL
     * @return ModelAndView for unionpay.html
     */
    @GetMapping("/unionpay")
    public ModelAndView showUnionPay(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();

        try {
            ApiRequest req = ClientUtil.createBrowserPaymentsRequest("PAY", "UNION_PAY", request.getRequestURL().toString());
            mav.setViewName("unionpay");
            mav.addObject("apiRequest", req);
            mav.addObject("config", config);
        }
        catch (Exception e) {
            mav.setViewName("error");
            logger.error("An error occurred", e);
            mav.addObject("cause", e.getCause());
            mav.addObject("message", e.getMessage());
        }

        return mav;
    }

    /**
     * Display page for Masterpass interaction
     * @return ModelAndView for masterpass.html
     */
    @GetMapping("/masterpass")
    public ModelAndView showMasterpass() {
        ModelAndView mav = new ModelAndView("masterpass");
        return mav;
    }

    /**
     * Display CAPTURE operation page
     * @return ModelAndView for capture.html
     */
    @GetMapping("/capture")
    public ModelAndView showCapture() {
        ModelAndView mav = new ModelAndView("capture");
        ApiRequest req = ClientUtil.createApiRequest("CAPTURE");
        mav.addObject("apiRequest", req);
        return mav;
    }

    /**
     * Display REFUND operation page
     * @return ModelAndView for refund.html
     */
    @GetMapping("/refund")
    public ModelAndView showRefund() {
        ModelAndView mav = new ModelAndView("refund");
        ApiRequest req = ClientUtil.createApiRequest("REFUND");
        mav.addObject("apiRequest", req);
        return mav;
    }

    /**
     * Display RETRIEVE_TRANSACTION operation page
     * @return ModelAndView for retrieve.html
     */
    @GetMapping("/retrieve")
    public ModelAndView showRetrieve() {
        ModelAndView mav = new ModelAndView("retrieve");
        ApiRequest req = ClientUtil.createApiRequest("RETRIEVE_TRANSACTION");
        mav.addObject("apiRequest", req);
        return mav;
    }

    /**
     * Display UPDATE_AUTHORIZATION operation page
     * @return ModelAndView for update.html
     */
    @GetMapping("/update")
    public ModelAndView showUpdate() {
        ModelAndView mav = new ModelAndView("update");
        ApiRequest req = ClientUtil.createApiRequest("UPDATE_AUTHORIZATION");
        req.setTransactionId(ClientUtil.randomNumber());
        mav.addObject("apiRequest", req);
        return mav;
    }

    /**
     * Display VOID operation page
     * @return ModelAndView for void.html
     */
    @GetMapping("/void")
    public ModelAndView showVoid() {
        ModelAndView mav = new ModelAndView("void");
        ApiRequest req = ClientUtil.createApiRequest("VOID");
        req.setTransactionId(ClientUtil.randomNumber());
        mav.addObject("apiRequest", req);
        return mav;
    }

    /**
     * Display 3DSecure operation page
     * @return ModelAndView for secureId.html
     */
    @GetMapping("/secureId")
    public ModelAndView showSecureId() {
        ModelAndView mav = new ModelAndView("secureId");
        mav.addObject("merchantId", config.getMerchantId());
        mav.addObject("baseUrl", config.getApiBaseURL());
        mav.addObject("redirectUrlEndpoint", "process3ds");
        return mav;
    }

    /**
     * Display page for Hosted Checkout operation
     * @return ModelAndView for hostedCheckout.html
     */
    @GetMapping("/hostedCheckout")
    public ModelAndView showHostedCheckout() {

        ModelAndView mav = new ModelAndView();

        ApiRequest req = new ApiRequest();
        req.setApiOperation("CREATE_CHECKOUT_SESSION");
        req.setOrderId(ClientUtil.randomNumber());
        req.setOrderCurrency("USD");

        String requestUrl = ClientUtil.getSessionRequestUrl(config);

        String data = ClientUtil.buildJSONPayload(req);

        try {
            ApiClient connection = new ApiClient();
            String resp = connection.postTransaction(data, requestUrl, config);

            CheckoutSession checkoutSession = ClientUtil.parseSessionResponse(resp);

            mav.setViewName("hostedCheckout");
            mav.addObject("config", config);
            mav.addObject("orderId", req.getOrderId());
            mav.addObject("checkoutSession", checkoutSession);
        } catch (Exception e) {
            mav.setViewName("error");
            logger.error("An error occurred", e);
            mav.addObject("cause", e.getCause());
            mav.addObject("message", e.getMessage());
        }
        return mav;
    }

    /**
     * This method receives the callback from the Hosted Checkout redirect. It looks up the order using the RETRIEVE_ORDER operation and
     * displays either the receipt or an error page.
     * @param orderId needed to retrieve order
     * @param result of Hosted Checkout operation (success or error) - sent from hostedCheckout.html complete() callback
     * @return ModelAndView for hosted checkout receipt page or error page
     */
    @GetMapping("/hostedCheckout/{orderId}/{result}")
    public ModelAndView hostedCheckoutReceipt(@PathVariable(value="orderId") String orderId, @PathVariable(value="result") String result) {

        ModelAndView mav = new ModelAndView();

        try {
            if(result.equals(ApiResponses.SUCCESS.toString())) {
                ApiRequest req = new ApiRequest();
                req.setApiOperation("RETRIEVE_ORDER");
                req.setOrderId(orderId);

                String requestUrl = ClientUtil.getRequestUrl(config, req);

                ApiClient connection = new ApiClient();
                String resp = connection.getTransaction(requestUrl, config);
                TransactionResponse hostedCheckoutResponse = ClientUtil.parseHostedCheckoutResponse(resp);

                mav.addObject("response", hostedCheckoutResponse);
                mav.setViewName("receipt");
            }
            else {
                mav.setViewName("error");
                logger.info("The payment was unsuccessful");
                mav.addObject("cause", "Payment was unsuccessful");
                mav.addObject("message", "There was a problem completing your transaction.");
            }
        }
        catch (Exception e) {
            mav.setViewName("error");
            logger.error("An error occurred", e);
            mav.addObject("cause", e.getCause());
            mav.addObject("message", e.getMessage());
        }

        return mav;
    }

    /**
     * This method processes the API request for Hosted Session (browser) operations (PAY, AUTHORIZE, VERIFY). Any time card details need to be collected, Hosted Session is the preferred method.
     * @param operation indicates which API operation is to be invoked (PAY, AUTHORIZE, VERIFY)
     * @param sessionId used to retrieve session created in hostedSession.js
     * @return ModelAndView for api response page or error page
     */
    @GetMapping("/process/{operation}/{sessionId}")
    public ModelAndView processHostedSession(@PathVariable(value="operation") String operation, @PathVariable(value="sessionId") String sessionId) {

        ModelAndView mav = new ModelAndView();

        try {
            // Retrieve session
            String url = ClientUtil.getSessionRequestUrl(config, sessionId);
            ApiClient sessionConnection = new ApiClient();
            String sessionResponse = sessionConnection.getTransaction(url, config);

            // Parse session response into CheckoutSession object
            CheckoutSession session = ClientUtil.parseSessionResponse(sessionResponse);

            // Construct API request
            ApiRequest request = ClientUtil.createApiRequest(operation);
            request.setSessionId(session.getId());
            String jsonPayload = ClientUtil.buildJSONPayload(request);
            String requestUrl = ClientUtil.getRequestUrl(config, request);

            // Perform API operation
            ApiClient apiConnection = new ApiClient();
            String apiResponse = apiConnection.sendTransaction(jsonPayload, requestUrl, config);

            // Send info on transaction to view
            ObjectMapper mapper = new ObjectMapper();
            Object prettyResp = mapper.readValue(apiResponse, Object.class);
            Object prettyPayload = mapper.readValue(jsonPayload, Object.class);

            mav.setViewName("apiResponse");
            mav.addObject("config", config);
            mav.addObject("resp", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(prettyResp));
            mav.addObject("operation", request.getApiOperation());
            mav.addObject("method", request.getApiMethod());
            mav.addObject("request", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(prettyPayload));
            mav.addObject("requestUrl", requestUrl);
        }
        catch(Exception e) {
            mav.setViewName("error");
            logger.error("An error occurred", e);
            mav.addObject("cause", e.getCause());
            mav.addObject("message", e.getMessage());
        }
        return mav;
    }

    /**
     * This method processes the API request for server-to-server operations. These are operations that would not commonly be invoked via a user interacting with the browser, but a system event (CAPTURE, REFUND, VOID).
     * @param request contains info on how to construct API call
     * @return ModelAndView for api response page or error page
     */
    @PostMapping("/process")
    public ModelAndView process(ApiRequest request) {

        ModelAndView mav = new ModelAndView();

        String requestUrl = ClientUtil.getRequestUrl(config, request);
        String jsonPayload = ClientUtil.buildJSONPayload(request);

        String resp = "";

        try {
            ApiClient connection = new ApiClient();
            if (request.getApiMethod().equals("PUT")) {
                resp = connection.sendTransaction(jsonPayload, requestUrl, config);
            } else if (request.getApiMethod().equals("GET")) {
                resp = connection.getTransaction(requestUrl, config);
            }
            ObjectMapper mapper = new ObjectMapper();
            Object prettyResp = mapper.readValue(resp, Object.class);
            Object prettyPayload = mapper.readValue(jsonPayload, Object.class);

            mav.setViewName("apiResponse");
            mav.addObject("resp", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(prettyResp));
            mav.addObject("operation", request.getApiOperation());
            mav.addObject("method", request.getApiMethod());
            mav.addObject("request", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(prettyPayload));
            mav.addObject("requestUrl", requestUrl);
        } catch (Exception e) {
            mav.setViewName("error");
            logger.error("An error occurred", e);
            mav.addObject("cause", e.getCause());
            mav.addObject("message", e.getMessage());
        }
        return mav;
    }

    /**
     * This method calls the INTIATE_BROWSER_PAYMENT operation, which returns a URL to the provider's website. The user is redirected to this URL, where the purchase is completed.
     * @param request contains info on how to construct API call
     * @return ModelAndView - either redirects to appropriate provider website or returns error page
     */
    @PostMapping("/processBrowserPayment")
    public ModelAndView processBrowserPayment(ApiRequest request) {
        ModelAndView mav = new ModelAndView();

        String requestUrl = ClientUtil.getRequestUrl(config, request);
        String jsonPayload = ClientUtil.buildJSONPayload(request);

        try {
            ApiClient connection = new ApiClient();
            String resp = connection.sendTransaction(jsonPayload, requestUrl, config);
            // Redirect to provider's website
            mav.setViewName("redirect:" + ClientUtil.getBrowserPaymentRedirectUrl(resp));
        } catch (Exception e) {
            mav.setViewName("error");
            mav.addObject("cause", e.getCause());
            mav.addObject("message", e.getMessage());
        }
        return mav;
    }

    /**
     * This method handles the callback from the payment provider (PayPal, UnionPay, etc). It looks up the transaction based on the transaction ID and order ID and displays
     * either a receipt page or an error page.
     * @param transactionId used to retrieve transaction
     * @param orderId used to construct API endpoint
     * @return ModelAndView for PayPal or UnionPay SecurePay receipt page or error page
     */
    @GetMapping("/browserPaymentReceipt")
    public ModelAndView browserPaymentReceipt(@RequestParam("transactionId") String transactionId, @RequestParam("orderId") String orderId) {

        ModelAndView mav = new ModelAndView();

        ApiRequest apiReq = new ApiRequest();
        apiReq.setTransactionId(transactionId);
        apiReq.setOrderId(orderId);
        String requestUrl = ClientUtil.getRequestUrl(config, apiReq);

        String data = "";
        try {
            // Retrieve transaction
            ApiClient connection = new ApiClient();
            String resp = connection.getTransaction(requestUrl, config);
            BrowserPaymentResponse browserPaymentResponse = ClientUtil.parseBrowserPaymentResponse(resp);

            if(browserPaymentResponse.getApiResult().equals(ApiResponses.SUCCESS.toString()) && browserPaymentResponse.getInteractionStatus().equals(ApiResponses.COMPLETED.toString())) {
                mav.addObject("response", browserPaymentResponse);
                mav.setViewName("receipt");
            }
            else {
                mav.setViewName("error");
                mav.addObject("cause", browserPaymentResponse.getApiResult());
                mav.addObject("message", browserPaymentResponse.getAcquirerMessage());
            }
        } catch (Exception e) {
            mav.setViewName("error");
            logger.error("An error occurred", e);
            mav.addObject("cause", e.getCause().toString());
            mav.addObject("message", e.getMessage());
        }
        return mav;
    }

    /**
     * This method handles the response from the CHECK_3DS_ENROLLMENT operation. If the card is enrolled, the response includes the HTML for the issuer's authentication form, to be injected into secureIdPayerAuthenticationForm.html.
     * Otherwise, it displays an error.
     * @param request needed to store 3DSecure ID and session ID in HttpSession
     * @param operation indicates which API operation is to be invoked (PAY, AUTHORIZE, VERIFY)
     * @param sessionId store in HttpSession to to retrieve after returning from issuer authentication form
     * @param redirectUrl indicates where the user should be redirected to after completing issuer authentication
     * @return ModelAndView - displays issuer authentication form or error page
     */
    @GetMapping("/check3dsEnrollment/{operation}/{sessionId}")
    public ModelAndView check3dsEnrollment(HttpServletRequest request, @PathVariable(value="operation") String operation, @PathVariable(value="sessionId") String sessionId, @RequestParam("redirectUrl") String redirectUrl) {

        ModelAndView mav = new ModelAndView();

        try {
            // Retrieve session
            String url = ClientUtil.getSessionRequestUrl(config, sessionId);
            ApiClient sessionConnection = new ApiClient();
            String sessionResponse = sessionConnection.getTransaction(url, config);

            // Parse session response into CheckoutSession object
            CheckoutSession session = ClientUtil.parseSessionResponse(sessionResponse);

            // Construct API request
            ApiRequest req = ClientUtil.createApiRequest(operation);
            req.setSessionId(session.getId());
            req.setSecureIdResponseUrl(redirectUrl);
            String jsonPayload = ClientUtil.buildJSONPayload(req);

            // Create a unique identifier to use for 3DSecure
            String secureId = ClientUtil.randomNumber();

            // Save this value in HttpSession to retrieve after returning from issuer authentication form
            HttpSession httpSession = request.getSession();
            httpSession.setAttribute("secureId", secureId);
            httpSession.setAttribute("sessionId", session.getId());
            String requestUrl = ClientUtil.getSecureIdRequest(config, secureId);

            // Perform API operation
            ApiClient apiConnection = new ApiClient();
            String apiResponse = apiConnection.sendTransaction(jsonPayload, requestUrl, config);

            SecureId secureIdObject = ClientUtil.parse3DSecureResponse(apiResponse);

            if(secureIdObject.getStatus().equals(ApiResponses.CARD_ENROLLED.toString())) {
                mav.setViewName("secureIdPayerAuthenticationForm");
                mav.addObject("authenticationHtml", secureIdObject.getHtmlBodyContent());
            }
            else {
                mav.setViewName("error");
                mav.addObject("cause", secureIdObject.getStatus());
                mav.addObject("message", "Card not enrolled in 3DS.");
            }
        }
        catch(Exception e) {
            mav.setViewName("error");
            logger.error("An error occurred", e);
            mav.addObject("cause", e.getCause());
            mav.addObject("message", e.getMessage());
        }

        return mav;
    }

    /**
     * This method completes the 3DS process after the enrollment check. It calls PROCESS_ACS_RESULT, which returns either a successful or failed authentication response.
     * If the response is successful, complete the operation (PAY, AUTHORIZE, etc) or shows an error page.
     * @param request needed to retrieve 3DSecure ID and session ID to complete 3DS transaction
     * @return ModelAndView - displays api response page or error page
     */
    @PostMapping("/process3ds")
    public ModelAndView process3ds(HttpServletRequest request) {

        ModelAndView mav = new ModelAndView();

        ApiRequest req = new ApiRequest();
        req.setApiOperation("PROCESS_ACS_RESULT");
        // Retrieve Payment Authentication Response (PaRes) from request
        req.setPaymentAuthResponse(request.getParameter("PaRes"));

        try {
            HttpSession session = request.getSession();
            String secureId = (String) session.getAttribute("secureId");
            String sessionId = (String) session.getAttribute("sessionId");

            // Remove from session after using
            session.removeAttribute("secureId");
            session.removeAttribute("sessionId");

            // Process Access Control Server (ACS) result
            String requestUrl = ClientUtil.getSecureIdRequest(config, secureId);
            ApiClient connection = new ApiClient();

            String data = ClientUtil.buildJSONPayload(req);
            String resp = connection.postTransaction(data, requestUrl, config);
            SecureId secureIdObject = ClientUtil.parse3DSecureResponse(resp);

            if(!secureIdObject.getStatus().equals(ApiResponses.AUTHENTICATION_FAILED.toString())) {
                // Construct API request
                ApiRequest apiReq = ClientUtil.createApiRequest("AUTHORIZE");
                apiReq.setSessionId(sessionId);
                apiReq.setSecureId(secureId);
                String payload = ClientUtil.buildJSONPayload(apiReq);
                String reqUrl = ClientUtil.getRequestUrl(config, apiReq);

                // Perform API operation
                ApiClient apiConnection = new ApiClient();
                String apiResponse = apiConnection.sendTransaction(payload, reqUrl, config);

                ObjectMapper mapper = new ObjectMapper();
                Object prettyResp = mapper.readValue(apiResponse, Object.class);
                Object prettyPayload = mapper.readValue(payload, Object.class);

                mav.setViewName("apiResponse");
                mav.addObject("resp", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(prettyResp));
                mav.addObject("operation", apiReq.getApiOperation());
                mav.addObject("method", apiReq.getApiMethod());
                mav.addObject("request", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(prettyPayload));
                mav.addObject("requestUrl", reqUrl);
            }
            else {
                mav.setViewName("error");
                mav.addObject("cause", ApiResponses.AUTHENTICATION_FAILED.toString());
                mav.addObject("message", "3DS authentication failed. Please try again with another card.");
            }
        }
        catch(Exception e) {
            mav.setViewName("error");
            logger.error("An error occurred", e);
            mav.addObject("cause", e.getCause());
            mav.addObject("message", e.getMessage());
        }
        return mav;
    }

    private ModelAndView createModel(String viewName) {
        ModelAndView mav = new ModelAndView(viewName);
        mav.addObject("config", config);
        return mav;
    }

}