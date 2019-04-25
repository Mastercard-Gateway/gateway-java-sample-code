/*
 * Copyright (c) 2019 MasterCard. All rights reserved.
 */

package com.gateway;

import java.util.HashMap;
import java.util.Map;

import com.gateway.app.Config;
import com.gateway.client.ApiAuthenticationChannel;
import com.gateway.client.ApiProtocol;
import com.gateway.client.ApiRequest;
import com.gateway.client.ApiRequestService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Before;
import org.junit.Test;

import static com.gateway.client.ApiOperation.CREATE_CHECKOUT_SESSION;
import static com.gateway.client.ApiOperation.INITIATE_BROWSER_PAYMENT;
import static org.junit.Assert.assertEquals;

public class ApiRequestServiceTest {

    private Config config;

    @Before
    public void setUp() {
        config = new Config();
        config.setMerchantId("TESTMERCHANTID");
        config.setApiPassword("APIPASSWORD1234");
        config.setApiBaseURL("https://test-gateway.com");
        config.setGatewayHost("https://test-gateway.com");
        config.setCurrency("USD");
        config.setApiVersion(45);
    }

    @Test
    public void getRequestUrl() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setOrderId("DS9SJ3J39A");
        request.setTransactionId("H9JK29SM0J");
        String result = ApiRequestService.getRequestUrl(ApiProtocol.REST, config, request);
        assertEquals("https://test-gateway.com/api/rest/version/45/merchant/TESTMERCHANTID/order/DS9SJ3J39A/transaction/H9JK29SM0J", result);
    }

    @Test
    public void getSessionRequestUrl() throws Exception {
        String result = ApiRequestService.getSessionRequestUrl(ApiProtocol.REST, config);
        assertEquals("https://test-gateway.com/api/rest/version/45/merchant/TESTMERCHANTID/session", result);
    }

    @Test
    public void getSessionRequestUrlWithSessionId() throws Exception {
        String result = ApiRequestService.getSessionRequestUrl(ApiProtocol.REST, config, "SESSIONID");
        assertEquals("https://test-gateway.com/api/rest/version/45/merchant/TESTMERCHANTID/session/SESSIONID", result);
    }

    @Test
    public void getNVPSecureIdRequest() throws Exception {
        String result = ApiRequestService.getSecureIdRequest(ApiProtocol.NVP, config, "SECURE_ID");
        assertEquals(result, "https://test-gateway.com/api/nvp/version/45/merchant/TESTMERCHANTID/3DSecureId/SECURE_ID");
    }

    @Test
    public void getRESTSecureIdRequest() throws Exception {
        String result = ApiRequestService.getSecureIdRequest(ApiProtocol.REST, config, "SECURE_ID");
        assertEquals(result, "https://test-gateway.com/api/rest/version/45/merchant/TESTMERCHANTID/3DSecureId/SECURE_ID");
    }

    @Test
    public void parseAuthorizeRequest() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setApiOperation("AUTHORIZE");
        request.setOrderAmount("10.00");
        request.setOrderCurrency("USD");
        request.setSourceType("CARD");
        String result = ApiRequestService.buildJSONPayload(request);

        String data = "{\"apiOperation\":\"AUTHORIZE\",\"order\":{\"amount\":\"10.00\",\"currency\":\"USD\"},\"sourceOfFunds\":{\"type\":\"CARD\"}}";

        assertEquals(prettifyJson(data), result);
    }

    @Test
    public void parseCaptureRequest() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setApiOperation("CAPTURE");
        request.setTransactionAmount("10.00");
        request.setTransactionCurrency("USD");
        String result = ApiRequestService.buildJSONPayload(request);

        String data = "{\"apiOperation\":\"CAPTURE\",\"transaction\":{\"amount\":\"10.00\",\"currency\":\"USD\"}}";

        assertEquals(prettifyJson(data), result);
    }

    @Test
    public void parsePayRequest() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setApiOperation("PAY");
        request.setOrderAmount("10.00");
        request.setOrderCurrency("USD");
        request.setSourceType("CARD");
        String result = ApiRequestService.buildJSONPayload(request);

        String data = "{\"apiOperation\":\"PAY\",\"order\":{\"amount\":\"10.00\",\"currency\":\"USD\"},\"sourceOfFunds\":{\"type\":\"CARD\"}}";

        assertEquals(prettifyJson(data), result);
    }

    @Test
    public void parseRefundRequest() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setApiOperation("REFUND");
        request.setTransactionAmount("10.00");
        request.setTransactionCurrency("USD");
        String result = ApiRequestService.buildJSONPayload(request);

        String data = "{\"apiOperation\":\"REFUND\",\"transaction\":{\"amount\":\"10.00\",\"currency\":\"USD\"}}";

        assertEquals(prettifyJson(data), result);
    }

    @Test
    public void parseVerifyRequest() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setApiOperation("VERIFY");
        request.setOrderCurrency("USD");
        request.setSourceType("CARD");
        String result = ApiRequestService.buildJSONPayload(request);

        String data = "{\"apiOperation\":\"VERIFY\",\"order\":{\"currency\":\"USD\"},\"sourceOfFunds\":{\"type\":\"CARD\"}}";

        assertEquals(prettifyJson(data), result);
    }

    @Test
    public void parseVoidRequest() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setApiOperation("VOID");
        request.setTargetTransactionId("D9DK0KMWBS");
        String result = ApiRequestService.buildJSONPayload(request);

        String data = "{\"apiOperation\":\"VOID\",\"transaction\":{\"targetTransactionId\":\"D9DK0KMWBS\"}}";

        assertEquals(prettifyJson(data), result);
    }

    @Test
    public void parseRetrieveTransactionRequest() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setApiOperation("RETRIEVE_TRANSACTION");
        String result = ApiRequestService.buildJSONPayload(request);

        String data = "{\"apiOperation\":\"RETRIEVE_TRANSACTION\"}";

        assertEquals(prettifyJson(data), result);
    }

    @Test
    public void parseConfirmBrowserPaymentRequest() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setApiOperation("CONFIRM_BROWSER_PAYMENT");
        request.setOrderAmount("10.00");
        request.setOrderCurrency("USD");
        String result = ApiRequestService.buildJSONPayload(request);

        String data = "{\"apiOperation\":\"CONFIRM_BROWSER_PAYMENT\",\"order\":{\"amount\":\"10.00\",\"currency\":\"USD\"}}";

        assertEquals(prettifyJson(data), result);
    }

    /* essentials_exclude_start */
    /* targeted_exclude_start */
    @Test
    public void parsePayPalRequest() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setApiOperation(INITIATE_BROWSER_PAYMENT.toString());
        request.setOrderAmount("10.00");
        request.setOrderCurrency("USD");
        request.setBrowserPaymentOperation("PAY");
        request.setBrowserPaymentConfirmation("CONFIRM_AT_PROVIDER");
        request.setReturnUrl("http://www.mysite.com/receipt");
        request.setSourceType("PAYPAL");
        String result = ApiRequestService.buildJSONPayload(request);

        String data = "{\"apiOperation\":\"INITIATE_BROWSER_PAYMENT\",\"order\":{\"amount\":\"10.00\",\"currency\":\"USD\"},\"sourceOfFunds\":{\"type\":\"PAYPAL\"},\"browserPayment\":{\"operation\":\"PAY\",\"paypal\":{\"paymentConfirmation\":\"CONFIRM_AT_PROVIDER\"},\"returnUrl\":\"http://www.mysite.com/receipt\"}}";

        assertEquals(prettifyJson(data), result);
    }
    /* targeted_exclude_end */
    /* essentials_exclude_end */

    /* essentials_exclude_start */
    @Test
    public void parseUnionPayRequest() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setApiOperation(INITIATE_BROWSER_PAYMENT.toString());
        request.setOrderAmount("10.00");
        request.setOrderCurrency("USD");
        request.setBrowserPaymentOperation("PAY");
        request.setReturnUrl("http://www.mysite.com/receipt");
        request.setSourceType("UNION_PAY");
        String result = ApiRequestService.buildJSONPayload(request);

        String data = "{\"apiOperation\":\"INITIATE_BROWSER_PAYMENT\",\"order\":{\"amount\":\"10.00\",\"currency\":\"USD\"},\"sourceOfFunds\":{\"type\":\"UNION_PAY\"},\"browserPayment\":{\"operation\":\"PAY\",\"returnUrl\":\"http://www.mysite.com/receipt\"}}";

        assertEquals(prettifyJson(data), result);
    }
    /* essentials_exclude_end */

    @Test
    public void parseCreateSessionRequest() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setApiOperation(CREATE_CHECKOUT_SESSION.toString());
        request.setOrderId("DS9SJ3J39A");
        request.setOrderCurrency("USD");
        request.setReturnUrl("http://www.mysite.com/receipt");
        String result = ApiRequestService.buildJSONPayload(request);

        String data = "{\"apiOperation\":\"CREATE_CHECKOUT_SESSION\",\"order\":{\"id\":\"DS9SJ3J39A\",\"currency\":\"USD\"},\"interaction\":{\"returnUrl\":\"http://www.mysite.com/receipt\"}}";

        assertEquals(prettifyJson(data), result);
    }

    @Test
    public void parseRequestWithSessionId() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setApiOperation("PAY");
        request.setOrderAmount("10.00");
        request.setOrderCurrency("USD");
        request.setSessionId("SESSION0002647025380I5651515F86");
        String result = ApiRequestService.buildJSONPayload(request);

        String data = "{\"apiOperation\":\"PAY\",\"order\":{\"amount\":\"10.00\",\"currency\":\"USD\"},\"session\":{\"id\":\"SESSION0002647025380I5651515F86\"}}";

        assertEquals(prettifyJson(data), result);
    }

    @Test
    public void parse3dsRequest() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setApiOperation("CHECK_3DS_ENROLLMENT");
        request.setOrderAmount("10.00");
        request.setOrderCurrency("USD");
        request.setSessionId("SESSION0002647025380I5651515F86");
        request.setSecureIdResponseUrl("http://www.mysite.com/receipt");
        String result = ApiRequestService.buildJSONPayload(request);

        String data = "{\"apiOperation\":\"CHECK_3DS_ENROLLMENT\",\"order\":{\"amount\":\"10.00\",\"currency\":\"USD\"},\"session\":{\"id\":\"SESSION0002647025380I5651515F86\"},\"3DSecure\":{\"authenticationRedirect\":{\"responseUrl\":\"http://www.mysite.com/receipt\",\"pageGenerationMode\": \"CUSTOMIZED\"}}}";

        assertEquals(prettifyJson(data), result);
    }

    @Test
    public void parseUpdate3DS2Session() throws Exception {
        String ApiOperation = "UPDATE_SESSION";
        String orderAmount = "10.00";
        String orderCurrency = "USD";
        String orderId = "order-194";
        String authenticationChannel = ApiAuthenticationChannel.MERCHANT_REQUESTED.toString();

        ApiRequest request = new ApiRequest();
        request.setApiOperation(ApiOperation);
        request.setOrderAmount(orderAmount);
        request.setAuthenticationChannel(authenticationChannel);
        request.setOrderId(orderId);
        request.setOrderCurrency(orderCurrency);

        String result = ApiRequestService.buildJSONPayload(request);

        String data = "{\n" +
                "  \"order\": {\n" +
                "    \"id\": "+orderId+",\n" +
                "    \"amount\": \""+orderAmount+"\",\n" +
                "    \"currency\": "+orderCurrency+"\n" +
                "  },\n" +
                "  \"authentication\": {\n" +
                "    \"channel\": "+authenticationChannel+"\n" +
                "  }\n" +
                "}";

        assertEquals(prettifyJson(data), result);
    }

    @Test
    public void parse3ds2Request() throws Exception {
        String ApiOperation = "PAY";
        String orderAmount = "10.00";
        String orderCurrency = "USD";
        String sessionId = "SESSION0002647025380I5651515F86";
        String transactionId = "H9JK29SM0J";
        String secureIdResponseUrl = "http://www.mybank.com/receipt";

        ApiRequest request = new ApiRequest();
        request.setApiOperation(ApiOperation);
        request.setOrderAmount(orderAmount);
        request.setOrderCurrency(orderCurrency);
        request.setSessionId(sessionId);
        request.setTransactionId(transactionId);
        request.setSecureIdResponseUrl(secureIdResponseUrl);
        String result = ApiRequestService.buildJSONPayload(request);

        String data = "{\"apiOperation\":\""+ApiOperation+"\"," +
                "\"order\":{\"amount\":\""+orderAmount+"\"," +
                "\"currency\":\""+orderCurrency+"\"}," +
                "\"session\":{\"id\":\""+sessionId+"\"}," +
                "\"3DSecure\":{\"authenticationRedirect\":{\"responseUrl\":\""+secureIdResponseUrl+"\"," +
                "\"pageGenerationMode\": \"CUSTOMIZED\"}}}";

        assertEquals(prettifyJson(data), result);
    }

    @Test
    public void parseACSRequest() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setApiOperation("PROCESS_ACS_RESULT");
        request.setPaymentAuthResponse("LONG_PARES_VALUE");
        String result = ApiRequestService.buildJSONPayload(request);

        String data = "{\"apiOperation\":\"PROCESS_ACS_RESULT\",\"3DSecure\":{\"paRes\":\"LONG_PARES_VALUE\"}}";

        assertEquals(prettifyJson(data), result);
    }

    @Test
    public void parseUsingSecureIdWithTransaction() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setApiOperation("PAY");
        request.setOrderAmount("10.00");
        request.setOrderCurrency("USD");
        request.setSessionId("SESSION0002647025380I5651515F86");
        request.setSecureId("1234567890");
        String result = ApiRequestService.buildJSONPayload(request);

        String data = "{\"apiOperation\":\"PAY\",\"3DSecureId\":\"1234567890\",\"order\":{\"amount\":\"10.00\",\"currency\":\"USD\"},\"session\":{\"id\":\"SESSION0002647025380I5651515F86\"}}";

        assertEquals(prettifyJson(data), result);
    }

    /* essentials_exclude_start */
    @Test
    public void parseWalletRequest() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setWalletProvider("MASTERPASS_ONLINE");
        request.setOrderAmount("10.00");
        request.setOrderCurrency("USD");
        request.setMasterpassOriginUrl("http://www.mysite.com/receipt");

        String result = ApiRequestService.buildJSONPayload(request);

        String data = "{\"order\":{\"amount\":\"10.00\",\"currency\":\"USD\",\"walletProvider\":\"MASTERPASS_ONLINE\"},\"wallet\":{\"masterpass\":{\"originUrl\":\"http://www.mysite.com/receipt\"}}}";

        assertEquals(prettifyJson(data), result);
    }
    /* essentials_exclude_end */

    /* essentials_exclude_start */
    @Test
    public void parseGetWalletDetailsRequest() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setWalletProvider("MASTERPASS_ONLINE");

        String result = ApiRequestService.buildJSONPayload(request);

        String data = "{\"order\":{\"walletProvider\":\"MASTERPASS_ONLINE\"}}";

        assertEquals(prettifyJson(data), result);
    }
    /* essentials_exclude_end */

    @Test
    public void parseNVPRequest() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setApiOperation("PAY");
        request.setOrderAmount("10.00");
        request.setOrderCurrency("USD");
        request.setSourceType("CARD");
        request.setOrderId("DS9SJ3J39A");
        request.setTransactionId("H9JK29SM0J");
        request.setSessionId("SESSION0002650629789J85543139F3");
        Map result = ApiRequestService.buildMap(request);

        HashMap<String, String> requestMap= new HashMap<String, String>();
        requestMap.put("apiOperation", "PAY");
        requestMap.put("order.id", "DS9SJ3J39A");
        requestMap.put("order.amount", "10.00");
        requestMap.put("order.currency", "USD");
        requestMap.put("transaction.id", "H9JK29SM0J");
        requestMap.put("session.id", "SESSION0002650629789J85543139F3");
        requestMap.put("sourceOfFunds.type", "CARD");

        assertEquals(requestMap, result);
    }

    private String prettifyJson(String data) {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(data).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(json);
    }

}