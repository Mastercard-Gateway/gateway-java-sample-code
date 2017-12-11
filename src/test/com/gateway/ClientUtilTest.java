package com.gateway;

import com.gateway.app.Config;
import com.gateway.client.ApiRequest;
import com.gateway.client.ClientUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ClientUtilTest {

    private Config config;

    @Before
    public void setUp() {
        config = new Config("TESTMERCHANTID", "APIPASSWORD1234", "https://test-gateway.mastercard.com", 45);
    }

    @Test
    public void getRequestUrl() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setOrderId("DS9SJ3J39A");
        request.setTransactionId("H9JK29SM0J");
        String result = ClientUtil.getRequestUrl(config, request);
        assertEquals("https://test-gateway.mastercard.com/api/rest/version/45/merchant/TESTMERCHANTID/order/DS9SJ3J39A/transaction/H9JK29SM0J", result);
    }

    @Test
    public void getSessionRequestUrl() throws Exception {
        String result = ClientUtil.getSessionRequestUrl(config);
        assertEquals("https://test-gateway.mastercard.com/api/rest/version/45/merchant/TESTMERCHANTID/session", result);
    }

    @Test
    public void parseAuthorizeRequest() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setApiOperation("AUTHORIZE");
        request.setOrderAmount("10.00");
        request.setOrderCurrency("USD");
        request.setSourceType("CARD");
        String result = ClientUtil.buildJSONPayload(request);

        String data = "{\"apiOperation\":\"AUTHORIZE\",\"order\":{\"amount\":\"10.00\",\"currency\":\"USD\"},\"sourceOfFunds\":{\"type\":\"CARD\"}}";

        assertEquals(prettifyJson(data), result);
    }

    @Test
    public void parseCaptureRequest() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setApiOperation("CAPTURE");
        request.setTransactionAmount("10.00");
        request.setTransactionCurrency("USD");
        String result = ClientUtil.buildJSONPayload(request);

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
        String result = ClientUtil.buildJSONPayload(request);

        String data = "{\"apiOperation\":\"PAY\",\"order\":{\"amount\":\"10.00\",\"currency\":\"USD\"},\"sourceOfFunds\":{\"type\":\"CARD\"}}";

        assertEquals(prettifyJson(data), result);
    }

    @Test
    public void parseRefundRequest() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setApiOperation("REFUND");
        request.setTransactionAmount("10.00");
        request.setTransactionCurrency("USD");
        String result = ClientUtil.buildJSONPayload(request);

        String data = "{\"apiOperation\":\"REFUND\",\"transaction\":{\"amount\":\"10.00\",\"currency\":\"USD\"}}";

        assertEquals(prettifyJson(data), result);
    }

    @Test
    public void parseVerifyRequest() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setApiOperation("VERIFY");
        request.setOrderCurrency("USD");
        request.setSourceType("CARD");
        String result = ClientUtil.buildJSONPayload(request);

        String data = "{\"apiOperation\":\"VERIFY\",\"order\":{\"currency\":\"USD\"},\"sourceOfFunds\":{\"type\":\"CARD\"}}";

        assertEquals(prettifyJson(data), result);
    }

    @Test
    public void parseVoidRequest() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setApiOperation("VOID");
        request.setTargetTransactionId("D9DK0KMWBS");
        String result = ClientUtil.buildJSONPayload(request);

        String data = "{\"apiOperation\":\"VOID\",\"transaction\":{\"targetTransactionId\":\"D9DK0KMWBS\"}}";

        assertEquals(prettifyJson(data), result);
    }

    @Test
    public void parseRetrieveTransactionRequest() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setApiOperation("RETRIEVE_TRANSACTION");
        String result = ClientUtil.buildJSONPayload(request);

        String data = "{\"apiOperation\":\"RETRIEVE_TRANSACTION\"}";

        assertEquals(prettifyJson(data), result);
    }

    @Test
    public void parseUpdateAuthRequest() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setApiOperation("UPDATE_AUTHORIZATION");
        String result = ClientUtil.buildJSONPayload(request);

        String data = "{\"apiOperation\":\"UPDATE_AUTHORIZATION\"}";

        assertEquals(prettifyJson(data), result);
    }

    @Test
    public void parseConfirmBrowserPaymentRequest() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setApiOperation("CONFIRM_BROWSER_PAYMENT");
        request.setOrderAmount("10.00");
        request.setOrderCurrency("USD");
        String result = ClientUtil.buildJSONPayload(request);

        String data = "{\"apiOperation\":\"CONFIRM_BROWSER_PAYMENT\",\"order\":{\"amount\":\"10.00\",\"currency\":\"USD\"}}";

        assertEquals(prettifyJson(data), result);
    }

    @Test
    public void parseInitiateBrowserPaymentRequest() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setApiOperation("INITIATE_BROWSER_PAYMENT");
        request.setOrderAmount("10.00");
        request.setOrderCurrency("USD");
        request.setBrowserPaymentOperation("PAY");
        request.setBrowserPaymentConfirmation("CONFIRM_AT_PROVIDER");
        request.setReturnUrl("http://www.mysite.com/receipt");
        request.setSourceType("PAYPAL");
        String result = ClientUtil.buildJSONPayload(request);

        String data = "{\"apiOperation\":\"INITIATE_BROWSER_PAYMENT\",\"order\":{\"amount\":\"10.00\",\"currency\":\"USD\"},\"sourceOfFunds\":{\"type\":\"PAYPAL\"},\"browserPayment\":{\"operation\":\"PAY\",\"paypal\":{\"paymentConfirmation\":\"CONFIRM_AT_PROVIDER\"},\"returnUrl\":\"http://www.mysite.com/receipt\"}}";

        assertEquals(prettifyJson(data), result);
    }

    @Test
    public void parseCreateSessionRequest() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setApiOperation("CREATE_CHECKOUT_SESSION");
        request.setOrderId("DS9SJ3J39A");
        request.setOrderCurrency("USD");
        request.setReturnUrl("http://www.mysite.com/receipt");
        String result = ClientUtil.buildJSONPayload(request);

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
        String result = ClientUtil.buildJSONPayload(request);

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
        String result = ClientUtil.buildJSONPayload(request);

        String data = "{\"apiOperation\":\"CHECK_3DS_ENROLLMENT\",\"order\":{\"amount\":\"10.00\",\"currency\":\"USD\"},\"session\":{\"id\":\"SESSION0002647025380I5651515F86\"},\"3DSecure\":{\"authenticationRedirect\":{\"responseUrl\":\"http://www.mysite.com/receipt\"}}}";

        assertEquals(prettifyJson(data), result);
    }

    @Test
    public void parseACSRequest() throws Exception {
        ApiRequest request = new ApiRequest();
        request.setApiOperation("PROCESS_ACS_RESULT");
        request.setPaymentAuthResponse("LONG_PARES_VALUE");
        String result = ClientUtil.buildJSONPayload(request);

        String data = "{\"apiOperation\":\"PROCESS_ACS_RESULT\",\"3DSecure\":{\"paRes\":\"LONG_PARES_VALUE\"}}";

        assertEquals(prettifyJson(data), result);
    }

    private String prettifyJson(String data) {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(data).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(json);
    }

}