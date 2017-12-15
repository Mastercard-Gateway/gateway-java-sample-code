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
    private Config config;

    /*
     * Browser operations
     */
    @GetMapping("/authorize")
    public ModelAndView showAuthorize() {
        return createModel("authorize");
    }

    @GetMapping("/pay")
    public ModelAndView showPay() {
        return createModel("pay");
    }

    @GetMapping("/verify")
    public ModelAndView showVerify() {
        return createModel("verify");
    }

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

    @GetMapping("/unionpay")
    public ModelAndView showUnionPay() {
        ModelAndView mav = new ModelAndView("unionpay");
        ApiRequest req = ClientUtil.createApiRequest("INITIATE_BROWSER_PAYMENT");
        req.setTransactionId(ClientUtil.randomNumber());
        req.setOrderId(ClientUtil.randomNumber());
        mav.addObject("apiRequest", req);
        return mav;
    }

    @GetMapping("/masterpass")
    public ModelAndView showMasterpass() {
        ModelAndView mav = new ModelAndView("masterpass");
        return mav;
    }

    @GetMapping("/webhooks")
    public ModelAndView showWebhooks() {
        ModelAndView mav = new ModelAndView("webhooks");
        return mav;
    }

    @GetMapping("/capture")
    public ModelAndView showCapture() {
        ModelAndView mav = new ModelAndView("capture");
        ApiRequest req = ClientUtil.createApiRequest("CAPTURE");
        mav.addObject("apiRequest", req);
        return mav;
    }

    @GetMapping("/refund")
    public ModelAndView showRefund() {
        ModelAndView mav = new ModelAndView("refund");
        ApiRequest req = ClientUtil.createApiRequest("REFUND");
        mav.addObject("apiRequest", req);
        return mav;
    }

    @GetMapping("/retrieve")
    public ModelAndView showRetrieve() {
        ModelAndView mav = new ModelAndView("retrieve");
        ApiRequest req = ClientUtil.createApiRequest("RETRIEVE_TRANSACTION");
        mav.addObject("apiRequest", req);
        return mav;
    }

    @GetMapping("/update")
    public ModelAndView showUpdate() {
        ModelAndView mav = new ModelAndView("update");
        ApiRequest req = ClientUtil.createApiRequest("UPDATE_AUTHORIZATION");
        req.setTransactionId(ClientUtil.randomNumber());
        mav.addObject("apiRequest", req);
        return mav;
    }

    @GetMapping("/void")
    public ModelAndView showVoid() {
        ModelAndView mav = new ModelAndView("void");
        ApiRequest req = ClientUtil.createApiRequest("VOID");
        req.setTransactionId(ClientUtil.randomNumber());
        mav.addObject("apiRequest", req);
        return mav;
    }

    /*
     * Hosted checkout
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

            CheckoutSession session = ClientUtil.parseSessionResponse(resp);

            mav.setViewName("hostedCheckout");
            mav.addObject("orderId", req.getOrderId());
            mav.addObject("sessionId", session.getId());
            mav.addObject("sessionVersion", session.getVersion());
            mav.addObject("successIndicator", session.getSuccessIndicator());
            mav.addObject("merchantId", config.getMerchantId());
            mav.addObject("apiPassword", config.getApiPassword());
        } catch (Exception e) {
            mav.setViewName("error");
            logger.error("An error occurred", e);
            mav.addObject("cause", e.getCause());
            mav.addObject("message", e.getMessage());
        }
        return mav;
    }

    @GetMapping("/hostedCheckout/{orderId}/{result}")
    public ModelAndView hostedCheckoutReceipt(@PathVariable(value="orderId") String orderId, @PathVariable(value="result") String result) {

        ModelAndView mav = new ModelAndView();

        try {
            // Retrieve order details
            ApiRequest req = new ApiRequest();
            req.setApiOperation("RETRIEVE_ORDER");
            req.setOrderId(orderId);

            String requestUrl = ClientUtil.getRequestUrl(config, req);

            ApiClient connection = new ApiClient();
            String resp = connection.getTransaction(requestUrl, config);
            TransactionResponse transactionResponse = ClientUtil.parseHostedCheckoutResponse(resp);

            if(result.equals(ApiResponses.SUCCESS.toString())) {
                mav.addObject("response", transactionResponse);
                mav.setViewName("receipt");
            }
            else {
                mav.setViewName("error");
                mav.addObject("cause", transactionResponse.getApiResult());
                mav.addObject("message", transactionResponse.getAcquirerMessage());
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
            TransactionResponse transactionResponse = ClientUtil.parseBrowserPaymentResponse(resp);

            if(transactionResponse.getApiResult().equals(ApiResponses.SUCCESS.toString())) {
                mav.addObject("response", transactionResponse);
                mav.setViewName("receipt");
            }
            else {
                mav.setViewName("error");
                mav.addObject("cause", transactionResponse.getApiResult());
                mav.addObject("message", transactionResponse.getAcquirerMessage());
            }
        } catch (Exception e) {
            mav.setViewName("error");
            logger.error("An error occurred", e);
            mav.addObject("cause", e.getCause().toString());
            mav.addObject("message", e.getMessage());
        }
        return mav;
    }

    // Endpoint for Hosted Session
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
            mav.addObject("merchantId", config.getMerchantId());
            mav.addObject("baseUrl", config.getApiBaseURL());
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

    /*
     * Processing endpoint for server-to-server operations
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

    /*
     * Processing endpoint for browser payments (PayPal, Union Pay, etc)
     */
    @PostMapping("/processBrowserPayment")
    public ModelAndView processBrowserPayment(HttpServletRequest request, ApiRequest apiReq) {
        ModelAndView mav = new ModelAndView();

        // INITIATE_BROWSER_PAYMENT
        String requestUrl = ClientUtil.getRequestUrl(config, apiReq);
        String jsonPayload = ClientUtil.buildJSONPayload(apiReq);

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

    /*
     * Display 3DS page
     */
    @GetMapping("/secureId")
    public ModelAndView showSecureId() {
        ModelAndView mav = new ModelAndView("secureId");
        mav.addObject("merchantId", config.getMerchantId());
        mav.addObject("baseUrl", config.getApiBaseURL());
        mav.addObject("redirectUrlEndpoint", "process3ds");
        return mav;
    }

    /*
     * Checks for 3DS enrollment and, if enrolled, redirects to issuer's authentication form
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

    /*
     * Processing endpoint for server-to-server operations
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
        mav.addObject("merchantId", config.getMerchantId());
        mav.addObject("baseUrl", config.getApiBaseURL());
        return mav;
    }

}