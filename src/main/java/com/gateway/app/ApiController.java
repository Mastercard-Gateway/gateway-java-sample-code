/*
 * Copyright (c) 2019 MasterCard. All rights reserved.
 */

package com.gateway.app;

import com.gateway.client.ApiException;
import com.gateway.client.ApiProtocol;
import com.gateway.client.ApiRequest;
import com.gateway.client.ApiRequestService;
import com.gateway.client.ApiResponseService;
import com.gateway.client.ExceptionService;
import com.gateway.client.HostedSession;
import com.gateway.client.NVPApiClient;
import com.gateway.client.RESTApiClient;
import com.gateway.client.Utils;
import com.gateway.response.BrowserPaymentResponse;
import com.gateway.response.SecureIdEnrollmentResponse;
import com.gateway.response.TransactionResponse;
import com.gateway.response.WalletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import static com.gateway.client.Utils.Prefixes.THREEDS;


@Controller
public class ApiController {

    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    public Config config;

    /* essentials_exclude_start */
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
            String sessionRequestUrl = ApiRequestService.getSessionRequestUrl(ApiProtocol.REST, config);
            String sessionResponse = connection.postTransaction(sessionRequestUrl, config);
            HostedSession hostedSession = ApiResponseService.parseSessionResponse(sessionResponse);

            // Call UPDATE_SESSION to add order information to session
            ApiRequestService.updateSessionWithOrderInfo(ApiProtocol.REST, request, config, hostedSession.getId());

            // Call OPEN_WALLET to retrieve Masterpass configuration
            String walletRequestUrl = ApiRequestService.getSessionRequestUrl(ApiProtocol.REST, config, hostedSession.getId());
            String openWalletPayload = ApiRequestService.buildJSONPayload(request);
            String walletResponse = connection.postTransaction(openWalletPayload, walletRequestUrl, config);
            WalletResponse wallet = ApiResponseService.parseWalletResponse(walletResponse, "masterpass");

            // Save this value in HttpSession to retrieve after returning from issuer authentication form
            HttpSession httpSession = httpServletRequest.getSession();
            httpSession.setAttribute("sessionId", hostedSession.getId());

            mav.setViewName("masterpassButton");
            mav.addObject("wallet", wallet);
            mav.addObject("config", config);
            mav.addObject("hostedSession", hostedSession);
        } catch (ApiException e) {
            ExceptionService.constructApiErrorResponse(mav, e);
        } catch (Exception e) {
            ExceptionService.constructGeneralErrorResponse(mav, e);
        }
        return mav;
    }
    /* essentials_exclude_end */

    /* essentials_exclude_start */
    /**
     * Handles the response from Masterpass. Retrieves the parameters and uses them to complete a payment or authorization.
     * @param oauthToken
     * @param oauthVerifier identifies the transaction. Used to retrieve payment details from Masterpass server
     * @param checkoutResourceUrl
     * @return ModelAndView for masterpassResponse.html
     */
    @GetMapping("/masterpassResponse")
    public ModelAndView completeMasterpassPayment(HttpServletRequest request, @RequestParam("oauth_token") String oauthToken, @RequestParam("oauth_verifier") String oauthVerifier,
                                                  @RequestParam("checkout_resource_url") String checkoutResourceUrl) {

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

            String url = ApiRequestService.getSessionRequestUrl(ApiProtocol.REST, config, sessionId);
            String data = ApiRequestService.buildJSONPayload(req);

            RESTApiClient connection = new RESTApiClient();
            String response = connection.postTransaction(data, url, config);

            // Make a payment using the session
            // Construct API request
            ApiRequest apiReq = ApiRequestService.createApiRequest("PAY", config);
            apiReq.setSessionId(sessionId);
            String payload = ApiRequestService.buildJSONPayload(apiReq);
            String reqUrl = ApiRequestService.getRequestUrl(ApiProtocol.REST, config, apiReq);

            // Perform API operation
            RESTApiClient apiConnection = new RESTApiClient();
            String apiResponse = apiConnection.sendTransaction(payload, reqUrl, config);

            TransactionResponse masterpassResponse = ApiResponseService.parseMasterpassResponse(apiResponse);
            mav.setViewName("receipt");
            mav.addObject("response", masterpassResponse);

        }
        catch(ApiException e) {
            ExceptionService.constructApiErrorResponse(mav, e);
        }
        catch(Exception e) {
            ExceptionService.constructGeneralErrorResponse(mav, e);
        }
        return mav;
    }
    /* essentials_exclude_end */

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

                String requestUrl = ApiRequestService.getRequestUrl(ApiProtocol.REST, config, req);

                RESTApiClient connection = new RESTApiClient();
                String resp = connection.getTransaction(requestUrl, config);
                TransactionResponse hostedCheckoutResponse = ApiResponseService.parseHostedCheckoutResponse(resp);

                mav.addObject("response", hostedCheckoutResponse);
                mav.setViewName("receipt");
            } else {
                mav.setViewName("error");
                logger.info("The payment was unsuccessful");
                mav.addObject("cause", "Payment was unsuccessful");
                mav.addObject("message", "There was a problem completing your transaction.");
            }
        }
        catch (ApiException e) {
            ExceptionService.constructApiErrorResponse(mav, e);
        }
        catch (Exception e) {
            ExceptionService.constructGeneralErrorResponse(mav, e);
        }

        return mav;
    }

    @GetMapping("/hostedCheckout/{orderId}/{successIndicator}/{sessionId}")
    ModelAndView hostedCheckoutRedirect(@PathVariable(value = "orderId") String orderId, @PathVariable(value = "successIndicator") String successIndicator, @PathVariable(value = "sessionId") String sessionId) {
        ModelAndView mav = new ModelAndView("hostedCheckout");

        HostedSession hostedSession = new HostedSession();
        hostedSession.setSuccessIndicator(successIndicator);
        hostedSession.setId(sessionId);

        mav.addObject("hostedSession", hostedSession);
        mav.addObject("config", config);
        mav.addObject("orderId", orderId);
        return mav;
    }

    /**
     * This method processes the API request for Hosted Session (browser) operations (PAY, AUTHORIZE, VERIFY).
     * Whenever card details need to be collected from the browser, Hosted Session is the preferred method.
     *
     * @param apiRequest needed to retrieve various data to complete API operation
     * @return ModelAndView for API response page or error page
     */
    @PostMapping("/processHostedSession")
    public ModelAndView processHostedSession(@RequestBody ApiRequest apiRequest) {

        ModelAndView mav = new ModelAndView();

        try {
            ApiRequestService.updateSessionWithOrderInfo(ApiProtocol.REST, apiRequest, config, apiRequest.getSessionId());

            String jsonPayload = ApiRequestService.buildJSONPayload(apiRequest);
            String requestUrl = ApiRequestService.getRequestUrl(ApiProtocol.REST, config, apiRequest);

            // Perform API operation
            RESTApiClient apiConnection = new RESTApiClient();
            String apiResponse = apiConnection.sendTransaction(jsonPayload, requestUrl, config);

            // Format request/response for easy viewing
            mav = ApiResponseService.formatApiResponse(mav, apiResponse, jsonPayload, config, apiRequest, requestUrl);
        } catch (ApiException e) {
            ExceptionService.constructApiErrorResponse(mav, e);
        } catch (Exception e) {
            ExceptionService.constructGeneralErrorResponse(mav, e);
        }
        return mav;
    }

    @PostMapping("/tokenize")
    public ModelAndView tokenizeAndPay(@RequestBody ApiRequest tokenRequest) {
        ModelAndView mav = new ModelAndView();

        try {
            ApiRequestService.updateSessionWithOrderInfo(ApiProtocol.REST, tokenRequest, config, tokenRequest.getSessionId());

            String tokenRequestUrl = ApiRequestService.getTokenRequestUrl(ApiProtocol.REST, config);

            // We need to delete the order info from the token request. We'll need it later for the payment request, so we'll add it to the payment request here
            ApiRequest payRequest = new ApiRequest();
            payRequest.setApiOperation("PAY");
            payRequest.setSessionId(tokenRequest.getSessionId());
            payRequest.setOrderId(tokenRequest.getOrderId());
            payRequest.setTransactionId(tokenRequest.getTransactionId());

            // We've already updated the session with the order information, so we need to remove it from the token request, which only requires the session ID (if additional fields are present the API will return an error)
            tokenRequest.setOrderAmount(null);
            tokenRequest.setOrderDescription(null);
            tokenRequest.setOrderCurrency(null);
            tokenRequest.setOrderId(null);

            String tokenPayload = ApiRequestService.buildJSONPayload(tokenRequest);

            RESTApiClient tokenConnection = new RESTApiClient();
            String tokenResponse = tokenConnection.postTransaction(tokenPayload, tokenRequestUrl, config);
            String token = ApiResponseService.parseTokenResponse(tokenResponse);

            payRequest.setSourceToken(token);
            String paymentRequestUrl = ApiRequestService.getRequestUrl(ApiProtocol.REST, config, payRequest);

            String paymentPayload = ApiRequestService.buildJSONPayload(payRequest);
            RESTApiClient paymentConnection = new RESTApiClient();
            String paymentResponse = paymentConnection.sendTransaction(paymentPayload, paymentRequestUrl, config);

            // Format request/response for easy viewing
            mav = ApiResponseService.formatApiResponse(mav, paymentResponse, paymentPayload, config, payRequest, paymentRequestUrl);
        } catch (ApiException e) {
            ExceptionService.constructApiErrorResponse(mav, e);
        } catch (Exception e) {
            ExceptionService.constructGeneralErrorResponse(mav, e);
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
            ApiRequestService.updateSessionWithOrderInfo(ApiProtocol.REST, apiRequest, config, apiRequest.getSessionId());

            apiRequest.setApiMethod("POST");

            String requestUrl = ApiRequestService.getRequestUrl(ApiProtocol.NVP, config, apiRequest);
            Map<String, String> dataMap = ApiRequestService.buildMap(apiRequest);

            NVPApiClient connection = new NVPApiClient();
            String response = connection.postTransaction(dataMap, requestUrl, config);
            Map<String, String> responseMap = ApiResponseService.parseNVPResponse(response);

            mav.setViewName("nvpApiResponse");
            mav.addObject("responseMap", responseMap);
            mav.addObject("operation", apiRequest.getApiOperation());
            mav.addObject("method", apiRequest.getApiMethod());
            mav.addObject("request", dataMap);
            mav.addObject("requestUrl", requestUrl);
        } catch (ApiException e) {
            ExceptionService.constructApiErrorResponse(mav, e);
        } catch (Exception e) {
            ExceptionService.constructGeneralErrorResponse(mav, e);
        }
        return mav;
    }

    /**
     * This method processes the API request for server-to-server operations.
     * These are operations that would not commonly be invoked via a user interacting with the browser, but a system event (CAPTURE, REFUND, VOID).
     *
     * @param request contains info on how to construct API call
     * @return ModelAndView for api response page or error page
     */
    @PostMapping("/process")
    public ModelAndView process(ApiRequest request) {

        ModelAndView mav = new ModelAndView();

        String requestUrl = ApiRequestService.getRequestUrl(ApiProtocol.REST, config, request);
        String jsonPayload = ApiRequestService.buildJSONPayload(request);

        String resp = "";

        try {
            RESTApiClient connection = new RESTApiClient();
            if (request.getApiMethod().equals("PUT")) {
                resp = connection.sendTransaction(jsonPayload, requestUrl, config);
            } else if (request.getApiMethod().equals("GET")) {
                resp = connection.getTransaction(requestUrl, config);
            }

            // Format request/response for easy viewing
            mav = ApiResponseService.formatApiResponse(mav, resp, jsonPayload, config, request, requestUrl);
        } catch (ApiException e) {
            ExceptionService.constructApiErrorResponse(mav, e);
        } catch (Exception e) {
            ExceptionService.constructGeneralErrorResponse(mav, e);
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

        String requestUrl = ApiRequestService.getRequestUrl(ApiProtocol.REST, config, request);
        String jsonPayload = ApiRequestService.buildJSONPayload(request);

        try {
            RESTApiClient connection = new RESTApiClient();
            String resp = connection.sendTransaction(jsonPayload, requestUrl, config);
            // Redirect to provider's website
            mav.setViewName("redirect:" + ApiResponseService.getBrowserPaymentRedirectUrl(resp));
        } catch (ApiException e) {
            ExceptionService.constructApiErrorResponse(mav, e);
        } catch (Exception e) {
            ExceptionService.constructGeneralErrorResponse(mav, e);
        }
        return mav;
    }

    /* essentials_exclude_start */
    /**
     * This method handles the callback from the payment provider. It looks up the transaction based on the transaction ID and order ID and displays
     * either a receipt page or an error page.
     *
     * @param transactionId used to retrieve transaction
     * @param orderId       used to construct API endpoint
     * @return ModelAndView for browser payment receipt page or error page
     */
    @GetMapping("/browserPaymentReceipt")
    public ModelAndView browserPaymentReceipt(@RequestParam("transactionId") String transactionId, @RequestParam("orderId") String orderId) {

        ModelAndView mav = new ModelAndView();

        ApiRequest apiReq = new ApiRequest();
        apiReq.setTransactionId(transactionId);
        apiReq.setOrderId(orderId);

        String requestUrl = ApiRequestService.getRequestUrl(ApiProtocol.REST, config, apiReq);

        String data = "";
        try {
            // Retrieve transaction
            RESTApiClient connection = new RESTApiClient();
            String resp = connection.getTransaction(requestUrl, config);
            BrowserPaymentResponse browserPaymentResponse = ApiResponseService.parseBrowserPaymentResponse(resp);

            if (browserPaymentResponse.getApiResult().equals(ApiResponses.SUCCESS.toString()) && browserPaymentResponse.getInteractionStatus().equals(ApiResponses.COMPLETED.toString())) {
                mav.addObject("response", browserPaymentResponse);
                mav.setViewName("receipt");
            } else {
                mav.setViewName("error");
                mav.addObject("cause", browserPaymentResponse.getApiResult());
                mav.addObject("message", browserPaymentResponse.getAcquirerMessage());
            }
        } catch (ApiException e) {
            ExceptionService.constructApiErrorResponse(mav, e);
        } catch (Exception e) {
            ExceptionService.constructGeneralErrorResponse(mav, e);
        }
        return mav;
    }
    /* essentials_exclude_end */

    /**
     * This method handles the response from the CHECK_3DS_ENROLLMENT operation. If the card is enrolled, the response includes the HTML for the issuer's authentication form, to be injected into 3dSecurePayerAuthenticationForm.html.
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
            HostedSession session = ApiResponseService.retrieveSession(config, apiRequest.getSessionId());

            // Construct CHECK_3DS_ENROLLMENT API request
            String jsonPayload = ApiRequestService.buildJSONPayload(apiRequest);

            // Create a unique identifier to use for 3DSecure
            String secureId = Utils.createUniqueId(THREEDS);

            // Save this value in HttpSession to retrieve after returning from issuer authentication form
            HttpSession httpSession = request.getSession();
            httpSession.setAttribute("secureId", secureId);
            httpSession.setAttribute("sessionId", session.getId());

            String requestUrl = ApiRequestService.getSecureIdRequest(ApiProtocol.REST, config, secureId);

            // Perform API operation
            RESTApiClient apiConnection = new RESTApiClient();
            String apiResponse = apiConnection.sendTransaction(jsonPayload, requestUrl, config);

            SecureIdEnrollmentResponse secureIdEnrollmentResponseObject = ApiResponseService.parse3DSecureResponse(apiResponse);
            secureIdEnrollmentResponseObject.setResponseUrl(ApiRequestService.getCurrentContext(request) + "/process3ds");

            if (secureIdEnrollmentResponseObject.getStatus().equals(ApiResponses.CARD_ENROLLED.toString())) {
                mav.setViewName("3dSecurePayerAuthenticationForm");
                mav.addObject("secureId", secureIdEnrollmentResponseObject);
                mav.addObject("config", config);
            } else {
                mav.setViewName("error");
                mav.addObject("cause", secureIdEnrollmentResponseObject.getStatus());
                mav.addObject("message", "Card not enrolled in 3DS.");
            }
        }
        catch(ApiException e) {
            ExceptionService.constructApiErrorResponse(mav, e);
        }
        catch (Exception e) {
            ExceptionService.constructGeneralErrorResponse(mav, e);
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

        ApiRequest processAcsRequest = new ApiRequest();
        processAcsRequest.setApiOperation("PROCESS_ACS_RESULT");
        // Retrieve Payment Authentication Response (PaRes) from request
        processAcsRequest.setPaymentAuthResponse(request.getParameter("PaRes"));

        try {
            HttpSession session = request.getSession();
            String secureId = (String) session.getAttribute("secureId");
            String sessionId = (String) session.getAttribute("sessionId");

            ApiRequestService.updateSessionWithOrderInfo(ApiProtocol.REST, processAcsRequest, config, sessionId);

            // Remove from session after using
            session.removeAttribute("secureId");
            session.removeAttribute("sessionId");

            // Process Access Control Server (ACS) result
            String processAcsRequestUrl = ApiRequestService.getSecureIdRequest(ApiProtocol.REST, config, secureId);
            RESTApiClient processAcsConnection = new RESTApiClient();

            String data = ApiRequestService.buildJSONPayload(processAcsRequest);
            String processAcsResponse = processAcsConnection.postTransaction(data, processAcsRequestUrl, config);
            SecureIdEnrollmentResponse secureIdEnrollmentResponseObject = ApiResponseService.parse3DSecureResponse(processAcsResponse);

            if (!secureIdEnrollmentResponseObject.getStatus().equals(ApiResponses.AUTHENTICATION_FAILED.toString())) {
                // Construct API request
                ApiRequest paymentRequest = ApiRequestService.createApiRequest("PAY", config);
                paymentRequest.setSessionId(sessionId);
                paymentRequest.setSecureId(secureId);

                String paymentData = ApiRequestService.buildJSONPayload(paymentRequest);
                String paymentRequestUrl = ApiRequestService.getRequestUrl(ApiProtocol.REST, config, paymentRequest);

                // Perform API operation
                RESTApiClient paymentConnection = new RESTApiClient();
                String apiResponse = paymentConnection.sendTransaction(paymentData, paymentRequestUrl, config);

                // Format request/response for easy viewing
                mav = ApiResponseService.formatApiResponse(mav, apiResponse, paymentData, config, paymentRequest, paymentRequestUrl);
            } else {
                mav.setViewName("error");
                mav.addObject("cause", ApiResponses.AUTHENTICATION_FAILED.toString());
                mav.addObject("message", "3DS authentication failed. Please try again with another card.");
            }
        } catch (ApiException e) {
            ExceptionService.constructApiErrorResponse(mav, e);
        } catch (Exception e) {
            ExceptionService.constructGeneralErrorResponse(mav, e);
        }
        return mav;
    }

    /**
     * Provided in the UpdateSession operation call as <pre>authentication.redirectResponseUrl</pre>, the payer will be
     * redirected to this URL will be after completing the payer authentication process.
     */
    @PostMapping(value = "/process3ds2Redirect")
    public ModelAndView process3ds2Redirect(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("receipt");
        try {
            String gatewayRecommendation = request.getParameter("response.gatewayRecommendation") != null ?
                    request.getParameter("response.gatewayRecommendation") :
                    request.getParameter("gatewayRecommendation");
            // When the result of the Authenticate Payer operation indicates that you can proceed with the payment, you
            // may initiate an Authorize or Pay operation.
            if (gatewayRecommendation != null &&
                    gatewayRecommendation.equals(ApiResponses.PROCEED_WITH_PAYMENT.toString())) {
                // The gateway will use the authentication.transactionId (provided in the request) to lookup the
                // authentication results that is stored when you asked to perform authentication. The gateway will
                // pass the required information to the acquirer.
                TransactionResponse paymentResponse = ApiRequestService.performTransaction(request, config);
//                mav.setViewName("receipt");
                mav.addObject("response", paymentResponse);
                mav.addObject("config", config);

            } else {
                throw new Exception("Gateway Recommendation not " + ApiResponses.PROCEED_WITH_PAYMENT.toString());

            }
        } catch (ApiException e) {
            ExceptionService.constructApiErrorResponse(mav, e);
        }
        catch (Exception e) {
            ExceptionService.constructGeneralErrorResponse(mav, e);
        }
        return mav;
    }

//    /**
//     * Make payment using the session and display receipt
//     * @return
//     */
//    @PutMapping(value = "/error")
//    public ModelAndView displayError(@RequestBody HttpServletRequest request)
//    {
//        ModelAndView mav = new ModelAndView();
//
//
//        return ExceptionService.constructGeneralErrorResponse(mav, new Exception(request.getParameter("apiResponse")));
//    }


}
