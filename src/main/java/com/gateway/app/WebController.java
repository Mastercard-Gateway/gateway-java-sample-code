package com.gateway.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gateway.client.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

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
            ApiRequest req = ApiService.createBrowserPaymentsRequest(request, "PAY", "PAYPAL");
            mav.setViewName("paypal");
            mav.addObject("apiRequest", req);
        } catch (Exception e) {
            mav.setViewName("error");
            logger.error("An error occurred", e);
            mav.addObject("cause", e.getCause());
            mav.addObject("message", e.getMessage());
        }

        return mav;
    }

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
            ApiRequest req = ApiService.createBrowserPaymentsRequest(request, "AUTHORIZE", "UNION_PAY");
            mav.setViewName("unionpay");
            mav.addObject("apiRequest", req);
            mav.addObject("config", config);
        } catch (Exception e) {
            mav.setViewName("error");
            logger.error("An error occurred", e);
            mav.addObject("cause", e.getCause());
            mav.addObject("message", e.getMessage());
        }

        return mav;
    }

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
            req.setOrderAmount("10.00");
            req.setOrderCurrency("USD");
            req.setWalletProvider("MASTERPASS_ONLINE");
            req.setMasterpassOriginUrl(ApiService.getCurrentContext(request) + "/masterpassResponse");
            mav.addObject("apiRequest", req);
            mav.addObject("config", config);
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
     * Create session and fetch Masterpass configuration details
     * 1. Create session
     * 2. Use OPEN_WALLET to get merchant's Masterpass configuration
     * 3. Pass these values to client for use with Masterpass Javascript library
     * @return ModelAndView for masterpassButton.html
     */
    @PostMapping("/processMasterpass")
    public ModelAndView processMasterpass(HttpServletRequest httpServletRequest, ApiRequest request) {

        ModelAndView mav = new ModelAndView();

        try {
            RESTApiClient connection = new RESTApiClient();

            // Create session to use with OPEN_WALLET operation
            String sessionRequestUrl = ApiService.getSessionRequestUrl(ApiProtocol.REST, config);
            String sessionResponse = connection.postTransaction(sessionRequestUrl, config);
            CheckoutSession checkoutSession = ApiService.parseSessionResponse(sessionResponse);

            // TODO Update session with order information (description, id, amount, etc)

            // Call OPEN_WALLET to retrieve Masterpass configuration
            String walletRequestUrl = ApiService.getSessionRequestUrl(ApiProtocol.REST, config, checkoutSession.getId());
            String data = ApiService.buildJSONPayload(request);
            String walletResponse = connection.postTransaction(data, walletRequestUrl, config);
            WalletResponse wallet = ApiService.parseWalletResponse(walletResponse, "masterpass");

            // Save this value in HttpSession to retrieve after returning from issuer authentication form
            HttpSession httpSession = httpServletRequest.getSession();
            httpSession.setAttribute("sessionId", checkoutSession.getId());

            mav.setViewName("masterpassButton");
            mav.addObject("wallet", wallet);
            mav.addObject("config", config);
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
     * Handles the response from Masterpass. Retrieves the parameters and uses them to complete a payment or authorization.
     * @param oauthToken
     * @param oauthVerifier identifies the transaction. Used to retrieve payment details from Masterpass server
     * @param checkoutId unique 32-character alphanumeric identifier generated by Masterpass, which identifies your settings during a checkout
     * @param checkoutResourceUrl
     * @param mpstatus returns status of the consumer's interaction with the Masterpass UI
     * @return ModelAndView for masterpassResponse.html
     */
    @GetMapping("/masterpassResponse")
    public ModelAndView completeMasterpassPayment(HttpServletRequest request, @RequestParam("oauth_token") String oauthToken, @RequestParam("oauth_verifier") String oauthVerifier,
                                                  @RequestParam("checkoutId") String checkoutId, @RequestParam("checkout_resource_url") String checkoutResourceUrl, @RequestParam("mpstatus") String mpstatus) {

        ModelAndView mav = new ModelAndView();

        // Retrieve Checkout Session ID from HttpServletRequest session (saved earlier in the Masterpass process)
        HttpSession session = request.getSession();
        String sessionId = (String) session.getAttribute("sessionId");

        try {
            // UPDATE_SESSION_FROM_WALLET - Retrieve payment details from wallet using session ID
            ApiRequest req = new ApiRequest();
            req.setApiOperation("UPDATE_SESSION_FROM_WALLET");
            req.setWalletProvider("MASTERPASS_ONLINE");
            req.setMasterpassOauthToken(oauthToken);
            req.setMasterpassOauthVerifier(oauthVerifier);
            req.setMasterpassCheckoutUrl(checkoutResourceUrl);

            String url = ApiService.getSessionRequestUrl(ApiProtocol.REST, config, sessionId);
            String data = ApiService.buildJSONPayload(req);

            RESTApiClient connection = new RESTApiClient();
            String response = connection.postTransaction(data, url, config);

            // Make a payment using the session
            // Construct API request
            ApiRequest apiReq = ApiService.createApiRequest("PAY");
            apiReq.setSessionId(sessionId);
            String payload = ApiService.buildJSONPayload(apiReq);
            String reqUrl = ApiService.getRequestUrl(ApiProtocol.REST, config, apiReq);

            // Perform API operation
            RESTApiClient apiConnection = new RESTApiClient();
            String apiResponse = apiConnection.sendTransaction(payload, reqUrl, config);

            ApiException exception = ApiService.checkForErrorResponse(apiResponse);
            if(exception != null) {
                throw exception;
            } else {
                TransactionResponse masterpassResponse = ApiService.parseMasterpassResponse(apiResponse);
                mav.setViewName("receipt");
                mav.addObject("response", masterpassResponse);
            }
        }
        catch(ApiException e) {
            mav.setViewName("error");
            logger.error(e.getMessage());
            mav.addObject("errorCode", e.getErrorCode());
            mav.addObject("explanation", e.getExplanation());
            mav.addObject("field", e.getField());
            mav.addObject("validationType", e.getValidationType());
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
     * Display CAPTURE operation page
     *
     * @return ModelAndView for capture.html
     */
    @GetMapping("/capture")
    public ModelAndView showCapture() {
        ModelAndView mav = new ModelAndView("capture");
        ApiRequest req = ApiService.createApiRequest("CAPTURE");
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
        ApiRequest req = ApiService.createApiRequest("REFUND");
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
        ApiRequest req = ApiService.createApiRequest("RETRIEVE_TRANSACTION");
        mav.addObject("apiRequest", req);
        return mav;
    }

    /**
     * Display UPDATE_AUTHORIZATION operation page
     *
     * @return ModelAndView for update.html
     */
    @GetMapping("/update")
    public ModelAndView showUpdate() {
        ModelAndView mav = new ModelAndView("update");
        ApiRequest req = ApiService.createApiRequest("UPDATE_AUTHORIZATION");
        req.setTransactionId(ApiService.randomNumber());
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
        ApiRequest req = ApiService.createApiRequest("VOID");
        req.setTransactionId(ApiService.randomNumber());
        mav.addObject("apiRequest", req);
        return mav;
    }

    /**
     * Display 3DSecure operation page
     *
     * @return ModelAndView for secureId.html
     */
    @GetMapping("/secureId")
    public ModelAndView showSecureId() {
        return createHostedSessionModel("secureId");
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
        req.setApiOperation("CREATE_CHECKOUT_SESSION");
        req.setOrderId(ApiService.randomNumber());
        req.setOrderCurrency("USD");

        String requestUrl = ApiService.getSessionRequestUrl(ApiProtocol.REST, config);

        String data = ApiService.buildJSONPayload(req);

        try {
            RESTApiClient connection = new RESTApiClient();
            String resp = connection.postTransaction(data, requestUrl, config);

            CheckoutSession checkoutSession = ApiService.parseSessionResponse(resp);

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
     *
     * @param orderId needed to retrieve order
     * @param result  of Hosted Checkout operation (success or error) - sent from hostedCheckout.html complete() callback
     * @return ModelAndView for hosted checkout receipt page or error page
     */
    @GetMapping("/hostedCheckout/{orderId}/{result}")
    public ModelAndView hostedCheckoutReceipt(@PathVariable(value = "orderId") String orderId, @PathVariable(value = "result") String result) {

        ModelAndView mav = new ModelAndView();

        try {
            if (result.equals(ApiResponses.SUCCESS.toString())) {
                ApiRequest req = new ApiRequest();
                req.setApiOperation("RETRIEVE_ORDER");
                req.setOrderId(orderId);

                String requestUrl = ApiService.getRequestUrl(ApiProtocol.REST, config, req);

                RESTApiClient connection = new RESTApiClient();
                String resp = connection.getTransaction(requestUrl, config);
                TransactionResponse hostedCheckoutResponse = ApiService.parseHostedCheckoutResponse(resp);

                mav.addObject("response", hostedCheckoutResponse);
                mav.setViewName("receipt");
            } else {
                mav.setViewName("error");
                logger.info("The payment was unsuccessful");
                mav.addObject("cause", "Payment was unsuccessful");
                mav.addObject("message", "There was a problem completing your transaction.");
            }
        } catch (Exception e) {
            mav.setViewName("error");
            logger.error("An error occurred", e);
            mav.addObject("cause", e.getCause());
            mav.addObject("message", e.getMessage());
        }

        return mav;
    }

    /**
     * This method processes the API request for Hosted Session (browser) operations (PAY, AUTHORIZE, VERIFY). Any time card details need to be collected, Hosted Session is the preferred method.
     *
     * @param apiRequest needed to retrieve various data to complete API operation
     * @return ModelAndView for API response page or error page
     */
    @PostMapping("/processHostedSession")
    public ModelAndView processHostedSession(@RequestBody ApiRequest apiRequest) {

        ModelAndView mav = new ModelAndView();

        try {
            String jsonPayload = ApiService.buildJSONPayload(apiRequest);
            String requestUrl = ApiService.getRequestUrl(ApiProtocol.REST, config, apiRequest);

            // Perform API operation
            RESTApiClient apiConnection = new RESTApiClient();
            String apiResponse = apiConnection.sendTransaction(jsonPayload, requestUrl, config);

            // Format request/response for easy viewing
            ObjectMapper mapper = new ObjectMapper();
            Object prettyResp = mapper.readValue(apiResponse, Object.class);
            Object prettyPayload = mapper.readValue(jsonPayload, Object.class);

            mav.setViewName("apiResponse");
            mav.addObject("config", config);
            mav.addObject("resp", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(prettyResp));
            mav.addObject("operation", apiRequest.getApiOperation());
            mav.addObject("method", apiRequest.getApiMethod());
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
     * This method processes the API request using NVP (Name-Value Pair) protocol for Hosted Session (browser) operations (PAY, AUTHORIZE, VERIFY). Any time card details need to be collected, Hosted Session is the preferred method.
     *
     * @return ModelAndView for api response page or error page
     */
    @PostMapping("/processPayThroughNVP")
    public ModelAndView processNVPHostedSession(@RequestBody ApiRequest apiRequest) {

        ModelAndView mav = new ModelAndView();

        try {
            apiRequest.setApiMethod("POST");

            String requestUrl = ApiService.getRequestUrl(ApiProtocol.NVP, config, apiRequest);
            Map<String, String> dataMap = ApiService.buildMap(apiRequest);

            NVPApiClient connection = new NVPApiClient();
            String response = connection.postData(dataMap, requestUrl, config);
            mav.setViewName("apiResponse");
            mav.addObject("resp", response);
            mav.addObject("operation", apiRequest.getApiOperation());
            mav.addObject("method", apiRequest.getApiMethod());
            mav.addObject("request", dataMap);
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
     * This method processes the API request for server-to-server operations. These are operations that would not commonly be invoked via a user interacting with the browser, but a system event (CAPTURE, REFUND, VOID).
     *
     * @param request contains info on how to construct API call
     * @return ModelAndView for api response page or error page
     */
    @PostMapping("/process")
    public ModelAndView process(ApiRequest request) {

        ModelAndView mav = new ModelAndView();

        String requestUrl = ApiService.getRequestUrl(ApiProtocol.REST, config, request);
        String jsonPayload = ApiService.buildJSONPayload(request);

        String resp = "";

        try {
            RESTApiClient connection = new RESTApiClient();
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
     *
     * @param request contains info on how to construct API call
     * @return ModelAndView - either redirects to appropriate provider website or returns error page
     */
    @PostMapping("/processBrowserPayment")
    public ModelAndView processBrowserPayment(ApiRequest request) {
        ModelAndView mav = new ModelAndView();

        String requestUrl = ApiService.getRequestUrl(ApiProtocol.REST, config, request);
        String jsonPayload = ApiService.buildJSONPayload(request);

        try {
            RESTApiClient connection = new RESTApiClient();
            String resp = connection.sendTransaction(jsonPayload, requestUrl, config);
            // Redirect to provider's website
            mav.setViewName("redirect:" + ApiService.getBrowserPaymentRedirectUrl(resp));
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
     *
     * @param transactionId used to retrieve transaction
     * @param orderId       used to construct API endpoint
     * @return ModelAndView for PayPal or UnionPay SecurePay receipt page or error page
     */
    @GetMapping("/browserPaymentReceipt")
    public ModelAndView browserPaymentReceipt(@RequestParam("transactionId") String transactionId, @RequestParam("orderId") String orderId) {

        ModelAndView mav = new ModelAndView();

        ApiRequest apiReq = new ApiRequest();
        apiReq.setTransactionId(transactionId);
        apiReq.setOrderId(orderId);

        String requestUrl = ApiService.getRequestUrl(ApiProtocol.REST, config, apiReq);

        String data = "";
        try {
            // Retrieve transaction
            RESTApiClient connection = new RESTApiClient();
            String resp = connection.getTransaction(requestUrl, config);
            BrowserPaymentResponse browserPaymentResponse = ApiService.parseBrowserPaymentResponse(resp);

            if (browserPaymentResponse.getApiResult().equals(ApiResponses.SUCCESS.toString()) && browserPaymentResponse.getInteractionStatus().equals(ApiResponses.COMPLETED.toString())) {
                mav.addObject("response", browserPaymentResponse);
                mav.setViewName("receipt");
            } else {
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
     *
     * @param request     needed to store 3DSecure ID and session ID in HttpSession
     * @param apiRequest needed to retrieve various data to complete API operation
     * @return ModelAndView - displays issuer authentication form or error page
     */
    @PostMapping("/check3dsEnrollment")
    public ModelAndView check3dsEnrollment(HttpServletRequest request, @RequestBody ApiRequest apiRequest) {

        ModelAndView mav = new ModelAndView();

        try {
            // Retrieve session
            CheckoutSession session = ApiService.retrieveSession(config, apiRequest.getSessionId());

            // Construct UPDATE_SESSION_FROM_WALLET API request
            String jsonPayload = ApiService.buildJSONPayload(apiRequest);

            // Create a unique identifier to use for 3DSecure
            String secureId = ApiService.randomNumber();

            // Save this value in HttpSession to retrieve after returning from issuer authentication form
            HttpSession httpSession = request.getSession();
            httpSession.setAttribute("secureId", secureId);
            httpSession.setAttribute("sessionId", session.getId());

            String requestUrl = ApiService.getSecureIdRequest(ApiProtocol.REST, config, secureId);

            // Perform API operation
            RESTApiClient apiConnection = new RESTApiClient();
            String apiResponse = apiConnection.sendTransaction(jsonPayload, requestUrl, config);

            ApiException exception = ApiService.checkForErrorResponse(apiResponse);
            if(exception != null) {
                throw exception;
            }
            else {
                SecureId secureIdObject = ApiService.parse3DSecureResponse(apiResponse);

                if (secureIdObject.getStatus().equals(ApiResponses.CARD_ENROLLED.toString())) {
                    mav.setViewName("secureIdPayerAuthenticationForm");
                    mav.addObject("authenticationHtml", secureIdObject.getHtmlBodyContent());
                } else {
                    mav.setViewName("error");
                    mav.addObject("cause", secureIdObject.getStatus());
                    mav.addObject("message", "Card not enrolled in 3DS.");
                }
            }
        }
        catch(ApiException e) {
            mav.setViewName("error");
            logger.error(e.getMessage());
            mav.addObject("errorCode", e.getErrorCode());
            mav.addObject("explanation", e.getExplanation());
            mav.addObject("field", e.getField());
            mav.addObject("validationType", e.getValidationType());
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
     * This method completes the 3DS process after the enrollment check. It calls PROCESS_ACS_RESULT, which returns either a successful or failed authentication response.
     * If the response is successful, complete the operation (PAY, AUTHORIZE, etc) or shows an error page.
     *
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
            String requestUrl = ApiService.getSecureIdRequest(ApiProtocol.REST, config, secureId);
            RESTApiClient connection = new RESTApiClient();

            String data = ApiService.buildJSONPayload(req);
            String resp = connection.postTransaction(data, requestUrl, config);
            SecureId secureIdObject = ApiService.parse3DSecureResponse(resp);

            if (!secureIdObject.getStatus().equals(ApiResponses.AUTHENTICATION_FAILED.toString())) {
                // Construct API request
                ApiRequest apiReq = ApiService.createApiRequest("AUTHORIZE");
                apiReq.setSessionId(sessionId);
                apiReq.setSecureId(secureId);

                String payload = ApiService.buildJSONPayload(apiReq);
                String reqUrl = ApiService.getRequestUrl(ApiProtocol.REST, config, apiReq);

                // Perform API operation
                RESTApiClient apiConnection = new RESTApiClient();
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
            } else {
                mav.setViewName("error");
                mav.addObject("cause", ApiResponses.AUTHENTICATION_FAILED.toString());
                mav.addObject("message", "3DS authentication failed. Please try again with another card.");
            }
        } catch (Exception e) {
            mav.setViewName("error");
            logger.error("An error occurred", e);
            mav.addObject("cause", e.getCause());
            mav.addObject("message", e.getMessage());
        }
        return mav;
    }

    private ModelAndView createHostedSessionModel(String viewName) {
        ModelAndView mav = new ModelAndView(viewName);

        // Add some prefilled data - can be changed by user
        ApiRequest request = new ApiRequest();
        request.setOrderId(ApiService.randomNumber());
        request.setTransactionId(ApiService.randomNumber());
        request.setOrderAmount("50.00");
        request.setOrderCurrency("USD");
        request.setOrderDescription("Wonderful product that you should buy!");

        mav.addObject("request", request);
        mav.addObject("config", config);

        return mav;
    }
}