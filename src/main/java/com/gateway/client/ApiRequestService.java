/*
 * Copyright (c) 2018 MasterCard. All rights reserved.
 */

package com.gateway.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import com.gateway.app.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.gateway.client.ApiRequestService.ApiOperation.CREATE_SESSION;
import static com.gateway.client.ApiRequestService.ApiOperation.UPDATE_SESSION;

public class ApiRequestService {

    private static final Logger logger = LoggerFactory.getLogger(ApiRequestService.class);

    /**
     * The available operations from the API
     */
    public interface ApiOperation {
        String CREATE_SESSION = "CREATE_SESSION";
        String UPDATE_SESSION = "UPDATE_SESSION";
        String PAY = "PAY";
    }

    /**
     * Constructs an object used to complete the API.
     * Contains information about which operation to target and what info is needed in the request body.
     * Also populates with some pre-filled test data (such as order amount, currency, etc).
     *
     * @param apiOperation  indicates API operation to target of type com.gateway.client.ApiRequestService.ApiOperation
     * (PAY, AUTHORIZE, CAPTURE, CREATE_SESSION, UPDATE_SESSION, etc.)
     * @return ApiRequest ApiRequest with updated orderId, transactionId, ApiOperation and ApiMethod
     */
    public static ApiRequest createApiRequest(String apiOperation, Config config) {
        ApiRequest req = new ApiRequest();
        req.setApiOperation(apiOperation);
        req.setOrderAmount("5000");
        req.setOrderCurrency(config.getCurrency());
        req.setOrderId(Utils.createUniqueId("order-"));
        req.setTransactionId(Utils.createUniqueId("trans-"));

        switch (apiOperation) {
            case "CAPTURE":
            case "REFUND":
            case "VOID":
            case "UPDATE_AUTHORIZATION":
            req.setOrderId(null);
                break;
            case "RETRIEVE_ORDER":
            case "RETRIEVE_TRANSACTION": {
                req.setApiMethod("GET");
                req.setOrderId(null);
                req.setTransactionId(null);
            }
            break;
            case "CREATE_CHECKOUT_SESSION":
            case CREATE_SESSION:
            req.setApiMethod("POST");
                break;
            case UPDATE_SESSION:
            req.setApiMethod("PUT");
        }

        return req;
    }

    /**
     * Constructs API endpoint
     *
     * @param apiProtocol   REST or NVP
     * @param config        contains frequently used information like Merchant ID, API password, etc.
     * @return url
     */
    public static String getRequestUrl(ApiProtocol apiProtocol, Config config, ApiRequest request) {
        switch (apiProtocol) {
            case REST:
                String url = getApiBaseURL(config.getGatewayHost(), apiProtocol) + "/version/" + config.getApiVersion() + "/merchant/" + config.getMerchantId() + "/order/" + request.getOrderId();
                if (Utils.notNullOrEmpty(request.getTransactionId())) {
                    url += "/transaction/" + request.getTransactionId();
                }
                return url;
            case NVP:
                return getApiBaseURL(config.getGatewayHost(), apiProtocol) + "/version/" + config.getApiVersion();
            default:
                throwUnsupportedProtocolException();
        }
        return null;
    }

    /**
     * Constructs API endpoint to create a new session
     *
     * @param apiProtocol REST or NVP
     * @param config      contains frequently used information like Merchant ID, API password, etc.
     * @return url
     */
    public static String getSessionRequestUrl(ApiProtocol apiProtocol, Config config) {
        return getApiBaseURL(config.getGatewayHost(), apiProtocol) + "/version/" + config.getApiVersion() + "/merchant/" + config.getMerchantId() + "/session";
    }

    /**
     * Constructs API endpoint to create a new token
     *
     * @param apiProtocol REST or NVP
     * @param config      contains frequently used information like Merchant ID, API password, etc.
     * @return url
     */
    public static String getTokenRequestUrl(ApiProtocol apiProtocol, Config config) {
        return getApiBaseURL(config.getGatewayHost(), apiProtocol) + "/version/" + config.getApiVersion() + "/merchant/" + config.getMerchantId() + "/token";
    }

    /**
     * Constructs API endpoint for session-based requests with an existing session ID
     *
     * @param apiProtocol REST or NVP
     * @param config      contains frequently used information like Merchant ID, API password, etc.
     * @param sessionId   used to target a specific session
     * @return url
     */
    public static String getSessionRequestUrl(ApiProtocol apiProtocol, Config config, String sessionId) {
        switch (apiProtocol) {
            case REST:
                return getApiBaseURL(config.getGatewayHost(), apiProtocol) + "/version/" + config.getApiVersion() + "/merchant/" + config.getMerchantId() + "/session/" + sessionId;
            case NVP:
                return getApiBaseURL(config.getGatewayHost(), apiProtocol) + "/version/" + config.getApiVersion();
            default:
                throwUnsupportedProtocolException();
        }
        return null;
    }

    /**
     * Constructs API endpoint for 3DS requests
     *
     * @param apiProtocol REST or NVP
     * @param config      contains frequently used information like Merchant ID, API password, etc.
     * @param secureId    used to target a specific secureId
     * @return url
     */
    public static String getSecureIdRequest(ApiProtocol apiProtocol, Config config, String secureId) {
        return getApiBaseURL(config.getGatewayHost(), apiProtocol) + "/version/" + config.getApiVersion() + "/merchant/" + config.getMerchantId() + "/3DSecureId/" + secureId;
    }

    /**
     * Constructs the API payload based on properties of ApiRequest
     *
     * @param request contains info on what data the payload should include (order ID, amount, currency, etc) depending on the operation (PAY, AUTHORIZE, CAPTURE, etc)
     * @return JSON string
     */
    public static String buildJSONPayload(ApiRequest request) {

        JsonObject secureId = new JsonObject();
        if (Utils.notNullOrEmpty(request.getPaymentAuthResponse())) {
            // Used for 3DS Process ACS Result operation
            secureId.addProperty("paRes", request.getPaymentAuthResponse());
        }

        JsonObject authenticationRedirect = new JsonObject();
        // Used for 3DS check enrollment operation
        if (Utils.notNullOrEmpty(request.getSecureIdResponseUrl())) {
            authenticationRedirect.addProperty("responseUrl", request.getSecureIdResponseUrl());
            authenticationRedirect.addProperty("pageGenerationMode", "CUSTOMIZED");
            secureId.add("authenticationRedirect", authenticationRedirect);
        }

        // Used for hosted checkout - CREATE_CHECKOUT_SESSION operation
        JsonObject order = new JsonObject();
        if (Utils.notNullOrEmpty(request.getApiOperation()) &&
                (request.getApiOperation().equals("CREATE_CHECKOUT_SESSION")
                        || request.getApiOperation().equals(UPDATE_SESSION))) {
            // Need to add order ID in the request body only for CREATE_CHECKOUT_SESSION. Its presence in the body will cause an error for the other operations.
            if (Utils.notNullOrEmpty(request.getOrderId())) order.addProperty("id", request.getOrderId());
        }
        if (Utils.notNullOrEmpty(request.getOrderAmount())) order.addProperty("amount", request.getOrderAmount());
        if (Utils.notNullOrEmpty(request.getOrderCurrency())) order.addProperty("currency", request.getOrderCurrency());

        //3DS2
        JsonObject authentication = new JsonObject();
        if (Utils.notNullOrEmpty(request.getAuthenticationChannel())) authentication.addProperty("channel", request.getAuthenticationChannel());
        if (Utils.notNullOrEmpty(request.getAcceptVersions()))
            authentication.addProperty("acceptVersions", request.getAcceptVersions());
        if (Utils.notNullOrEmpty(request.getRedirectResponseUrl()))
            authentication.addProperty("redirectResponseUrl", request.getRedirectResponseUrl());

        JsonObject wallet = new JsonObject();
        /* essentials_exclude_start */
        if (Utils.notNullOrEmpty(request.getWalletProvider())) {
            order.addProperty("walletProvider", request.getWalletProvider());
            // Used for Masterpass operations
            if(request.getWalletProvider().equals("MASTERPASS_ONLINE")) {
                JsonObject masterpass = new JsonObject();
                if(Utils.notNullOrEmpty(request.getMasterpassOriginUrl())) masterpass.addProperty("originUrl", request.getMasterpassOriginUrl());
                if(Utils.notNullOrEmpty(request.getMasterpassOauthToken())) masterpass.addProperty("oauthToken", request.getMasterpassOauthToken());
                if(Utils.notNullOrEmpty(request.getMasterpassOauthVerifier())) masterpass.addProperty("oauthVerifier", request.getMasterpassOauthVerifier());
                if(Utils.notNullOrEmpty(request.getMasterpassCheckoutUrl())) masterpass.addProperty("checkoutUrl", request.getMasterpassCheckoutUrl());
                if (!masterpass.entrySet().isEmpty()) wallet.add("masterpass", masterpass);
            }
        }
        /* essentials_exclude_end */

        JsonObject transaction = new JsonObject();
        if (Utils.notNullOrEmpty(request.getTransactionAmount()))
            transaction.addProperty("amount", request.getTransactionAmount());
        if (Utils.notNullOrEmpty(request.getTransactionCurrency()))
            transaction.addProperty("currency", request.getTransactionCurrency());
        if (Utils.notNullOrEmpty(request.getTargetTransactionId()))
            transaction.addProperty("targetTransactionId", request.getTargetTransactionId());

        JsonObject expiry = new JsonObject();
        if (Utils.notNullOrEmpty(request.getExpiryMonth())) expiry.addProperty("month", request.getExpiryMonth());
        if (Utils.notNullOrEmpty(request.getExpiryYear())) expiry.addProperty("year", request.getExpiryYear());

        JsonObject card = new JsonObject();
        if (Utils.notNullOrEmpty(request.getSecurityCode())) card.addProperty("securityCode", request.getSecurityCode());
        if (Utils.notNullOrEmpty(request.getCardNumber())) card.addProperty("number", request.getCardNumber());
        if (!expiry.entrySet().isEmpty()) card.add("expiry", expiry);

        JsonObject provided = new JsonObject();
        if (!card.entrySet().isEmpty()) provided.add("card", card);

        JsonObject sourceOfFunds = new JsonObject();
        if (Utils.notNullOrEmpty(request.getSourceType())) sourceOfFunds.addProperty("type", request.getSourceType());
        if (Utils.notNullOrEmpty(request.getSourceToken())) sourceOfFunds.addProperty("token", request.getSourceToken());
        if (!provided.entrySet().isEmpty()) sourceOfFunds.add("provided", provided);

        JsonObject browserPayment = new JsonObject();
        /* essentials_exclude_start */
        if (Utils.notNullOrEmpty(request.getBrowserPaymentOperation()))
            browserPayment.addProperty("operation", request.getBrowserPaymentOperation());
        /* targeted_exclude_start */
        if (Utils.notNullOrEmpty(request.getSourceType()) && request.getSourceType().equals("PAYPAL")) {
            JsonObject paypal = new JsonObject();
            paypal.addProperty("paymentConfirmation", "CONFIRM_AT_PROVIDER");
            browserPayment.add("paypal", paypal);
        }
        /* targeted_exclude_end */
        /* essentials_exclude_end */

        JsonObject interaction = new JsonObject();
        if (Utils.notNullOrEmpty(request.getReturnUrl()) && Utils.notNullOrEmpty(request.getApiOperation())) {
            // Return URL needs to be added differently for browser payments and hosted checkout payments
            if (request.getApiOperation().equals("CREATE_CHECKOUT_SESSION")) {
                interaction.addProperty("returnUrl", request.getReturnUrl());
            } else if (request.getApiOperation().equals("INITIATE_BROWSER_PAYMENT")
                    || request.getApiOperation().equals("CONFIRM_BROWSER_PAYMENT")
                    || request.getApiOperation().equals(UPDATE_SESSION)) {
                browserPayment.addProperty("returnUrl", request.getReturnUrl());
            }
        }

        JsonObject session = new JsonObject();
        if (Utils.notNullOrEmpty(request.getSessionId())) session.addProperty("id", request.getSessionId());

        // Add all the elements to the main JSON object we'll return from this method
        JsonObject data = new JsonObject();
        if (Utils.notNullOrEmpty(request.getApiOperation()) && !request.getApiOperation().equals(UPDATE_SESSION)
                && !request.getApiOperation().equals(CREATE_SESSION))
            data.addProperty("apiOperation", request.getApiOperation());
        if (Utils.notNullOrEmpty(request.getSecureId())) data.addProperty("3DSecureId", request.getSecureId());
        if (!order.entrySet().isEmpty()) data.add("order", order);
        if (!authentication.entrySet().isEmpty()) data.add("authentication", authentication);
        if (!wallet.entrySet().isEmpty()) data.add("wallet", wallet);
        if (!transaction.entrySet().isEmpty()) data.add("transaction", transaction);
        if (!sourceOfFunds.entrySet().isEmpty()) data.add("sourceOfFunds", sourceOfFunds);
        if (!browserPayment.entrySet().isEmpty()) data.add("browserPayment", browserPayment);
        if (!interaction.entrySet().isEmpty()) data.add("interaction", interaction);
        if (!session.entrySet().isEmpty()) data.add("session", session);
        if (!secureId.entrySet().isEmpty()) data.add("3DSecure", secureId);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        return gson.toJson(data);
    }

    /**
     * Constructs the API payload request map based on properties of ApiRequest
     *
     * @param request contains info on what data the payload should include (order ID, amount, currency, etc) depending on the operation (PAY, AUTHORIZE, CAPTURE, etc)
     * @return JSON string
     */
    public static Map<String, String> buildMap(ApiRequest request) {
        Map<String, String> keyValueMap = new HashMap<>();

        keyValueMap.put("apiOperation", "PAY");
        keyValueMap.put("order.id", request.getOrderId());
        keyValueMap.put("order.amount", request.getOrderAmount());
        keyValueMap.put("order.currency", request.getOrderCurrency());
        keyValueMap.put("transaction.id", request.getTransactionId());
        keyValueMap.put("session.id", request.getSessionId());
        keyValueMap.put("sourceOfFunds.type", "CARD");

        return keyValueMap;
    }

    /* essentials_exclude_start */
    /**
     * Constructs API request to initiate browser payment
     *
     * @param request   needed to determine the current context
     * @param operation indicates API operation to target (PAY, AUTHORIZE, CAPTURE, etc)
     * @param source    provider for the browser payment
     * @return ApiRequest
     * @throws MalformedURLException
     */
    public static ApiRequest createBrowserPaymentsRequest(HttpServletRequest request, String operation, String source, Config config) throws Exception {
        try {
            ApiRequest req = new ApiRequest();
            req.setApiOperation("INITIATE_BROWSER_PAYMENT");
            req.setTransactionId(Utils.createUniqueId("trans-"));
            req.setOrderId(Utils.createUniqueId("order-"));
            req.setOrderAmount("50.00");
            req.setOrderCurrency(config.getCurrency());
            req.setOrderDescription("Wonderful product that you should buy!");
            req.setBrowserPaymentOperation(operation);
            req.setSourceType(source);
            req.setReturnUrl(getCurrentContext(request) + "/browserPaymentReceipt?transactionId=" + req.getTransactionId() + "&orderId=" + req.getOrderId());
            return req;
        }
        catch(Exception e) {
            logger.error("Unable to create browser payment request", e);
            throw e;
        }
    }
    /* essentials_exclude_end */

    /**
     * This method updates the Hosted Session
     *
     * @param protocol  REST or NVP
     * @param request   contains info on what data the payload should include
     * @param config    contains frequently used information like Merchant ID, API password, etc.
     * @param sessionId used to target a specific session
     * @throws Exception
     */
    public static void updateSession(ApiProtocol protocol, ApiRequest request, Config config, String sessionId) throws Exception {
        RESTApiClient connection = new RESTApiClient();

        try {
            String updateSessionRequestUrl = ApiRequestService.getSessionRequestUrl(protocol, config, sessionId);
            ApiRequest updateSessionRequest = new ApiRequest();
            updateSessionRequest.setApiOperation(request.getApiOperation());
            updateSessionRequest.setOrderAmount(request.getOrderAmount());
            updateSessionRequest.setOrderCurrency(request.getOrderCurrency());
            updateSessionRequest.setOrderId(request.getOrderId());
            updateSessionRequest.setReturnUrl(request.getReturnUrl());
            updateSessionRequest.setBrowserPaymentOperation(request.getBrowserPaymentOperation());
            String updateSessionPayload = ApiRequestService.buildJSONPayload(updateSessionRequest);
            connection.sendTransaction(updateSessionPayload, updateSessionRequestUrl, config);
        }
        catch (Exception e) {
            logger.error("Unable to update session", e);
            throw e;
        }
    }

    /**
     * This method updates the Hosted Session for 3DS-2.0
     *
     * @param protocol REST or NVP
     * @param request contains info on what data the payload should not include. Should include sessionID
     * @param config contains frequently used information like Merchant ID, API password, etc.
     * @throws Exception
     */
    public static String update3DSSession(ApiProtocol protocol, ApiRequest request, Config config, String sessionId,
            String redirectResponseUrl) throws Exception {
        request.setApiOperation(UPDATE_SESSION);
        request.setApiMethod("PUT");

        request.setAcceptVersions("3DS2,3DS1").
                setAuthenticationChannel("PAYER_BROWSER").
                setRedirectResponseUrl(redirectResponseUrl);

        String updateSessionPayload = ApiRequestService.buildJSONPayload(request);
        try {
            RESTApiClient connection = new RESTApiClient();
            String updateSessionRequestUrl = ApiRequestService.getSessionRequestUrl(protocol, config, sessionId);
            return connection.sendTransaction(updateSessionPayload, updateSessionRequestUrl, config);
        } catch (Exception e) {
            logger.error("Unable to update session", e);
            throw e;
        }
    }

    /**
     * This method updates the Hosted Session with order info (description, amount, currency, ID)
     *
     * @param protocol  REST or NVP
     * @param request   contains info on what data the payload should include (order ID, amount, currency, etc) depending on the operation (PAY, AUTHORIZE, CAPTURE, etc)
     * @param config    contains frequently used information like Merchant ID, API password, etc.
     * @param sessionId used to target a specific session
     * @throws Exception
     */
    public static void updateSessionWithOrderInfo(ApiProtocol protocol, ApiRequest request, Config config, String sessionId) throws Exception {
        RESTApiClient connection = new RESTApiClient();

        try {
            String updateSessionRequestUrl = ApiRequestService.getSessionRequestUrl(protocol, config, sessionId);
            ApiRequest updateSessionRequest = new ApiRequest();
            updateSessionRequest.setOrderAmount(request.getOrderAmount());
            updateSessionRequest.setOrderCurrency(request.getOrderCurrency());
            updateSessionRequest.setOrderId(request.getOrderId());
            String updateSessionPayload = ApiRequestService.buildJSONPayload(updateSessionRequest);
            connection.sendTransaction(updateSessionPayload, updateSessionRequestUrl, config);
        }
        catch (Exception e) {
            logger.error("Unable to update session", e);
            throw e;
        }
    }

    /**
     * This helper method gets the current context so that an appropriate return URL can be constructed
     *
     * @return current context string
     */
    public static String getCurrentContext(HttpServletRequest request) throws MalformedURLException {
        try {
            URL url = new URL(request.getRequestURL().toString());
            return url.getProtocol() + "://" + url.getAuthority();
        }
        catch (MalformedURLException e) {
            logger.error("Unable to parse return URL", e);
            throw e;
        }
    }

    /**
     * Returns the base URL for the API call (either REST or NVP)
     *
     * @param gatewayHost
     * @param apiProtocol (REST or NVP)
     * @return base url or throw exception
     */
    private static String getApiBaseURL(String gatewayHost, ApiProtocol apiProtocol) {
        switch (apiProtocol) {
            case REST:
                return gatewayHost + "/api/rest";
            case NVP:
                return gatewayHost + "/api/nvp";
            default:
                throwUnsupportedProtocolException();
        }
        return null;
    }

    private static void throwUnsupportedProtocolException() {
        throw new IllegalArgumentException("Unsupported API protocol!");
    }
}
