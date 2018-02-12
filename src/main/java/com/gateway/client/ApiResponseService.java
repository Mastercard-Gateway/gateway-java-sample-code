package com.gateway.client;

import com.gateway.app.Config;
import com.gateway.response.BrowserPaymentResponse;
import com.gateway.response.SecureIdEnrollmentResponse;
import com.gateway.response.TransactionResponse;
import com.gateway.response.WalletResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

public class ApiResponseService {

    private static final Logger logger = LoggerFactory.getLogger(ApiResponseService.class);

    /**
     * Parses JSON response from session-based API call into HostedSession object
     *
     * @param sessionResponse response from API
     * @return HostedSession
     */
    public static HostedSession parseSessionResponse(String sessionResponse) {
        try {
            JsonObject json = new Gson().fromJson(sessionResponse, JsonObject.class);
            JsonObject jsonSession = json.get("session").getAsJsonObject();

            HostedSession hostedSession = new HostedSession();
            hostedSession.setId(jsonSession.get("id").getAsString());
            hostedSession.setVersion(jsonSession.get("version").getAsString());
            if (json.get("successIndicator") != null)
                hostedSession.setSuccessIndicator(json.get("successIndicator").getAsString());

            return hostedSession;
        } catch (Exception e) {
            logger.error("Unable to parse session response", e);
            throw e;
        }
    }

    /**
     * Parses JSON response from 3DS transaction into SecureIdEnrollmentResponse object
     *
     * @param response response from API
     * @return SecureIdEnrollmentResponse
     */
    public static SecureIdEnrollmentResponse parse3DSecureResponse(String response) {
        try {
            JsonObject json = new Gson().fromJson(response, JsonObject.class);
            JsonObject json3ds = json.get("3DSecure").getAsJsonObject();
            JsonObject jsonAuth = json3ds.get("authenticationRedirect").getAsJsonObject();
            JsonObject jsonCustomized = jsonAuth.get("customized").getAsJsonObject();

            SecureIdEnrollmentResponse secureIdEnrollmentResponse = new SecureIdEnrollmentResponse();
            secureIdEnrollmentResponse.setStatus(json3ds.get("summaryStatus").getAsString());
            secureIdEnrollmentResponse.setAcsUrl(jsonCustomized.get("acsUrl").getAsString());
            secureIdEnrollmentResponse.setPaReq(jsonCustomized.get("paReq").getAsString());
            secureIdEnrollmentResponse.setMdValue(Utils.createUniqueId("md-"));        //This is just a required unique ID to be able to connect the request to the response from ACS

            return secureIdEnrollmentResponse;
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

    /* essentials_exclude_start */
    /**
     * Parses JSON response from Masterpass transaction into TransactionResponse object
     *
     * @param response response from API
     * @return TransactionResponse
     */
    public static TransactionResponse parseMasterpassResponse(String response) {

        try {

            TransactionResponse resp = new TransactionResponse();

            JsonObject json = new Gson().fromJson(response, JsonObject.class);
            JsonObject orderJson = json.get("order").getAsJsonObject();
            JsonObject responseJson = json.getAsJsonObject("response").getAsJsonObject();

            resp.setApiResult(json.get("result").getAsString());
            resp.setGatewayCode(responseJson.get("gatewayCode").getAsString());
            resp.setOrderAmount(orderJson.get("amount").getAsString());
            resp.setOrderCurrency(orderJson.get("currency").getAsString());
            resp.setOrderId(orderJson.get("id").getAsString());

            return resp;
        } catch (Exception e) {
            logger.error("Unable to parse Masterpass response", e);
            throw e;
        }

    }
    /* essentials_exclude_end*/

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
     * Parses JSON response from wallet transaction into WalletResponse object
     *
     * @param response response from API
     * @param provider wallet provider
     * @return WalletResponse
     */
    public static WalletResponse parseWalletResponse(String response, String provider) {

        try {
            WalletResponse wallet = new WalletResponse();
            JsonObject json = new Gson().fromJson(response, JsonObject.class);
            JsonObject orderJson = json.get("order").getAsJsonObject();
            JsonObject walletJson = json.get("wallet").getAsJsonObject();
            JsonObject providerObj = walletJson.get(provider).getAsJsonObject();

            wallet.setAllowedCardTypes(providerObj.get("allowedCardTypes").getAsString());
            wallet.setMerchantCheckoutId(providerObj.get("merchantCheckoutId").getAsString());
            wallet.setOriginUrl(providerObj.get("originUrl").getAsString());
            wallet.setRequestToken(providerObj.get("requestToken").getAsString());
            wallet.setOrderAmount(orderJson.get("amount").getAsString());
            wallet.setOrderCurrency(orderJson.get("currency").getAsString());

            return wallet;
        }
        catch(Exception e) {
            logger.error("Unable to parse Masterpass response", e);
            throw e;
        }
    }

    /**
     * Parses name-value pair response
     *
     * @param response from the API call
     * @throws ApiException
     */
    public static HashMap parseNVPResponse(String response) throws ApiException {

        HashMap<String, String> responseMap = new HashMap<String, String>();
        List<NameValuePair> pairs = URLEncodedUtils.parse(response, Charset.forName("UTF-8"));
        for (NameValuePair p : pairs) {
            responseMap.put(p.getName(), p.getValue());
        }

        if (responseMap.get("result").equals("ERROR")) {
            ApiException apiException = new ApiException("The API returned an error");
            if(Utils.notNullOrEmpty(responseMap.get("error.cause"))) apiException.setErrorCode(responseMap.get("error.cause"));
            if(Utils.notNullOrEmpty(responseMap.get("error.explanation"))) apiException.setExplanation(responseMap.get("error.explanation"));
            if(Utils.notNullOrEmpty(responseMap.get("error.field"))) apiException.setField(responseMap.get("error.field"));
            if(Utils.notNullOrEmpty(responseMap.get("error.validationType"))) apiException.setValidationType(responseMap.get("error.validationType"));
            throw apiException;
        }

        return responseMap;
    }

    /**
     * Retrieve a Gateway session using the RETRIEVE_SESSION API
     * @param config    contains frequently used information like Merchant ID, API password, etc.
     * @param sessionId used to target a specific session
     * @return parsed session or throw exception
     */
    public static HostedSession retrieveSession(Config config, String sessionId) throws Exception {
        String url = ApiRequestService.getSessionRequestUrl(ApiProtocol.REST, config, sessionId);
        RESTApiClient sessionConnection = new RESTApiClient();
        try {
            String sessionResponse = sessionConnection.getTransaction(url, config);
            return parseSessionResponse(sessionResponse);
        } catch (Exception e) {
            logger.error("Unable to retrieve session", e);
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

}
