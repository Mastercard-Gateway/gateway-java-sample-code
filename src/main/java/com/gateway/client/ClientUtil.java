package com.gateway.client;

import com.gateway.app.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ClientUtil {

    private static final Logger logger = LoggerFactory.getLogger(ClientUtil.class);

    /**
     * Constructs an object used to complete the API. Contains information about which operation to target and what info is needed in the request body.
     *
     * @param apiOperation indicates API operation to target (PAY, AUTHORIZE, CAPTURE, etc)
     * @return ApiRequest
     */
    public static ApiRequest createApiRequest(String apiOperation) {
        ApiRequest req = new ApiRequest();
        req.setApiOperation(apiOperation);
        req.setOrderAmount("5000");
        req.setOrderCurrency("USD");
        req.setOrderId(ClientUtil.randomNumber());
        req.setTransactionId(ClientUtil.randomNumber());
        if (apiOperation.equals("CAPTURE") || apiOperation.equals("REFUND")) {
            req.setTransactionCurrency("USD");
            req.setTransactionAmount("5000");
            req.setOrderId(null);
        }
        if (apiOperation.equals("VOID") || apiOperation.equals("UPDATE_AUTHORIZATION")) {
            req.setOrderId(null);
        }
        if (apiOperation.equals("RETRIEVE_ORDER") || apiOperation.equals("RETRIEVE_TRANSACTION")) {
            req.setApiMethod("GET");
            req.setOrderId(null);
            req.setTransactionId(null);
        }
        if (apiOperation.equals("CREATE_CHECKOUT_SESSION")) {
            req.setApiMethod("POST");
        }
        return req;
    }

    /**
     * Constructs API endpoint
     *
     * @param apiProtocol REST or NVP
     * @param config      contains frequently used information like Merchant ID, API password, etc.
     * @return url
     */
    public static String getRequestUrl(ApiProtocol apiProtocol, Config config, ApiRequest request) {
        switch (apiProtocol) {
            case REST:
                String url = getApiBaseURL(config.getGatewayHost(), apiProtocol) + "/version/" + config.getApiVersion() + "/merchant/" + config.getMerchantId() + "/order/" + request.getOrderId();
                if (notNullOrEmpty(request.getTransactionId())) {
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
        JsonObject order = new JsonObject();

        JsonObject secureId = new JsonObject();
        if (notNullOrEmpty(request.getPaymentAuthResponse())) {
            secureId.addProperty("paRes", request.getPaymentAuthResponse());
        }

        JsonObject authenticationRedirect = new JsonObject();
        if (notNullOrEmpty(request.getSecureIdResponseUrl())) {
            authenticationRedirect.addProperty("responseUrl", request.getSecureIdResponseUrl());
            secureId.add("authenticationRedirect", authenticationRedirect);
        }

        if (request.getApiOperation().equals("CREATE_CHECKOUT_SESSION")) {
            // Need to add order ID in the request body for CREATE_CHECKOUT_SESSION. Its presence in the body will cause an error for the other operations.
            if (notNullOrEmpty(request.getOrderId())) order.addProperty("id", request.getOrderId());
        }
        if (notNullOrEmpty(request.getOrderAmount())) order.addProperty("amount", request.getOrderAmount());
        if (notNullOrEmpty(request.getOrderCurrency())) order.addProperty("currency", request.getOrderCurrency());

        JsonObject transaction = new JsonObject();
        if (notNullOrEmpty(request.getTransactionAmount()))
            transaction.addProperty("amount", request.getTransactionAmount());
        if (notNullOrEmpty(request.getTransactionCurrency()))
            transaction.addProperty("currency", request.getTransactionCurrency());
        if (notNullOrEmpty(request.getTargetTransactionId()))
            transaction.addProperty("targetTransactionId", request.getTargetTransactionId());

        JsonObject expiry = new JsonObject();
        if (notNullOrEmpty(request.getExpiryMonth())) expiry.addProperty("month", request.getExpiryMonth());
        if (notNullOrEmpty(request.getExpiryYear())) expiry.addProperty("year", request.getExpiryYear());

        JsonObject card = new JsonObject();
        if (notNullOrEmpty(request.getSecurityCode())) card.addProperty("securityCode", request.getSecurityCode());
        if (notNullOrEmpty(request.getCardNumber())) card.addProperty("number", request.getCardNumber());
        if (!expiry.entrySet().isEmpty()) card.add("expiry", expiry);

        JsonObject provided = new JsonObject();
        if (!card.entrySet().isEmpty()) provided.add("card", card);

        JsonObject sourceOfFunds = new JsonObject();
        if (notNullOrEmpty(request.getSourceType())) sourceOfFunds.addProperty("type", request.getSourceType());
        if (!provided.entrySet().isEmpty()) sourceOfFunds.add("provided", provided);

        JsonObject browserPayment = new JsonObject();
        if (notNullOrEmpty(request.getBrowserPaymentOperation()))
            browserPayment.addProperty("operation", request.getBrowserPaymentOperation());
        if (notNullOrEmpty(request.getSourceType()) && request.getSourceType().equals("PAYPAL")) {
            JsonObject paypal = new JsonObject();
            paypal.addProperty("paymentConfirmation", "CONFIRM_AT_PROVIDER");
            browserPayment.add("paypal", paypal);
        }

        JsonObject interaction = new JsonObject();
        if (notNullOrEmpty(request.getReturnUrl())) {
            // Return URL needs to be added differently for browser payments and hosted checkout payments
            if (request.getApiOperation().equals("CREATE_CHECKOUT_SESSION")) {
                interaction.addProperty("returnUrl", request.getReturnUrl());
            } else if (request.getApiOperation().equals("INITIATE_BROWSER_PAYMENT") || request.getApiOperation().equals("CONFIRM_BROWSER_PAYMENT")) {
                browserPayment.addProperty("returnUrl", request.getReturnUrl());
            }
        }

        JsonObject session = new JsonObject();
        if (notNullOrEmpty(request.getSessionId())) session.addProperty("id", request.getSessionId());

        // Add all the elements to the main JSON object we'll return from this method
        JsonObject data = new JsonObject();
        if (notNullOrEmpty(request.getApiOperation())) data.addProperty("apiOperation", request.getApiOperation());
        if (notNullOrEmpty(request.getSecureId())) data.addProperty("3DSecureId", request.getSecureId());
        if (!order.entrySet().isEmpty()) data.add("order", order);
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

    /**
     * @param operation  indicates API operation to target (PAY, AUTHORIZE, CAPTURE, etc)
     * @param source     provider for the browser payment (PayPal, UnionPay SecurePay, etc)
     * @param requestUrl needed to determine redirect URL
     * @return ApiRequest
     * @throws MalformedURLException
     */
    public static ApiRequest createBrowserPaymentsRequest(String operation, String source, String requestUrl) throws MalformedURLException {
        ApiRequest req = new ApiRequest();
        req.setApiOperation("INITIATE_BROWSER_PAYMENT");
        req.setTransactionId(ClientUtil.randomNumber());
        req.setOrderId(ClientUtil.randomNumber());
        req.setBrowserPaymentOperation(operation);
        req.setSourceType(source);
        try {
            URL url = new URL(requestUrl);
            String returnUrlBase = url.getProtocol() + "://" + url.getAuthority();
            req.setReturnUrl(returnUrlBase + "/browserPaymentReceipt?transactionId=" + req.getTransactionId() + "&orderId=" + req.getOrderId());
        } catch (MalformedURLException e) {
            logger.error("Unable to parse return URL", e);
            throw e;
        }
        return req;
    }

    /**
     * Parses JSON response from session-based API call into CheckoutSession object
     *
     * @param sessionResponse response from API
     * @return CheckoutSession
     */
    public static CheckoutSession parseSessionResponse(String sessionResponse) {
        try {
            JsonObject json = new Gson().fromJson(sessionResponse, JsonObject.class);
            JsonObject jsonSession = json.get("session").getAsJsonObject();

            CheckoutSession checkoutSession = new CheckoutSession();
            checkoutSession.setId(jsonSession.get("id").getAsString());
            checkoutSession.setVersion(jsonSession.get("version").getAsString());
            if (json.get("successIndicator") != null)
                checkoutSession.setSuccessIndicator(json.get("successIndicator").getAsString());

            return checkoutSession;
        } catch (Exception e) {
            logger.error("Unable to parse session response", e);
            throw e;
        }

    }

    /**
     * Parses JSON response from 3DS transaction into SecureId object
     *
     * @param response response from API
     * @return SecureId
     */
    public static SecureId parse3DSecureResponse(String response) {
        try {
            JsonObject json = new Gson().fromJson(response, JsonObject.class);
            JsonObject json3ds = json.get("3DSecure").getAsJsonObject();
            JsonObject jsonAuth = json3ds.get("authenticationRedirect").getAsJsonObject();
            JsonObject jsonSimple = jsonAuth.get("simple").getAsJsonObject();

            SecureId secureId = new SecureId();
            secureId.setStatus(json3ds.get("summaryStatus").getAsString());
            secureId.setHtmlBodyContent(jsonSimple.get("htmlBodyContent").getAsString());

            return secureId;
        } catch (Exception e) {
            logger.error("Unable to parse 3DSecure response", e);
            throw e;
        }
    }

    /**
     * Parses JSON response from Hosted Checkout transaction into TransactionResponse object
     *
     * @param response response from API
     * @return TransactionResponse
     */
    public static TransactionResponse parseHostedCheckoutResponse(String response) {

        try {

            TransactionResponse resp = new TransactionResponse();

            JsonObject json = new Gson().fromJson(response, JsonObject.class);
            JsonArray arr = json.get("transaction").getAsJsonArray();
            JsonObject transactionJson = arr.get(0).getAsJsonObject();
            JsonObject orderJson = transactionJson.get("order").getAsJsonObject();
            JsonObject responseJson = transactionJson.getAsJsonObject("response").getAsJsonObject();

            resp.setApiResult(transactionJson.get("result").getAsString());
            resp.setGatewayCode(responseJson.get("gatewayCode").getAsString());
            resp.setOrderAmount(orderJson.get("amount").getAsString());
            resp.setOrderCurrency(orderJson.get("currency").getAsString());
            resp.setOrderDescription(orderJson.get("description").getAsString());
            resp.setOrderId(orderJson.get("id").getAsString());

            return resp;
        } catch (Exception e) {
            logger.error("Unable to parse Hosted Checkout response", e);
            throw e;
        }

    }

    /**
     * Parses JSON response from Browser Payment transaction into BrowserPaymentResponse object
     *
     * @param response response from API
     * @return BrowserPaymentResponse
     */
    public static BrowserPaymentResponse parseBrowserPaymentResponse(String response) {

        try {
            BrowserPaymentResponse resp = new BrowserPaymentResponse();

            JsonObject json = new Gson().fromJson(response, JsonObject.class);
            JsonObject r = json.get("response").getAsJsonObject();
            JsonObject browserPayment = json.get("browserPayment").getAsJsonObject();
            JsonObject interaction = browserPayment.get("interaction").getAsJsonObject();
            JsonObject orderJson = json.get("order").getAsJsonObject();

            if (r.get("acquirerMessage") != null) {
                resp.setAcquirerMessage(r.get("acquirerMessage").getAsString());
            }
            resp.setApiResult(json.get("result").getAsString());
            resp.setGatewayCode(r.get("gatewayCode").getAsString());
            resp.setInteractionStatus(interaction.get("status").getAsString());
            resp.setOrderAmount(orderJson.get("amount").getAsString());
            resp.setOrderCurrency(orderJson.get("currency").getAsString());
            resp.setOrderId(orderJson.get("id").getAsString());

            return resp;
        } catch (Exception e) {
            logger.error("Unable to parse browser payment response", e);
            throw e;
        }

    }

    /**
     * Retrieve redirect URL from browser payment response
     *
     * @param response response from API
     * @return redirect URL
     */
    public static String getBrowserPaymentRedirectUrl(String response) {

        try {
            JsonObject json = new Gson().fromJson(response, JsonObject.class);
            JsonObject browserPayment = json.get("browserPayment").getAsJsonObject();
            return browserPayment.get("redirectUrl").getAsString();
        } catch (Exception e) {
            logger.error("Unable to get browser payment redirect URL", e);
            throw e;
        }
    }

    /**
     * Generates a random 10-digit alphanumeric number to use as a unique identifier (order ID and transaction ID, for instance)
     *
     * @return random identifier
     */
    public static String randomNumber() {
        return RandomStringUtils.random(10, true, true);
    }

    private static boolean notNullOrEmpty(String value) {
        return (value != null && !value.equals(""));
    }
}
