package com.gateway;

import com.gateway.app.Config;
import com.gateway.client.ApiResponseService;
import com.gateway.client.CheckoutSession;
import com.gateway.client.SecureId;
import com.gateway.response.TransactionResponse;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ApiResponseServiceTest {

    private Config config;

    @Before
    public void setUp() {
        config = new Config("TESTMERCHANTID", "APIPASSWORD1234", "https://test-gateway.mastercard.com", null);
        config.setApiVersion(45);
    }

    @Test
    public void parseSessionResponse() throws Exception {
        String data = "{\"merchant\":\"TESTAB2894354\",\"result\":\"SUCCESS\",\"session\":{\"id\":\"SESSION0002799480514F69145320L2\",\"updateStatus\":\"SUCCESS\",\"version\":\"6f8b683701\"},\"successIndicator\":\"0a292205c57e4dc8\"}";
        CheckoutSession session = ApiResponseService.parseSessionResponse(data);

        assertEquals(session.getId(), "SESSION0002799480514F69145320L2");
        assertEquals(session.getSuccessIndicator(), "0a292205c57e4dc8");
        assertEquals(session.getVersion(), "6f8b683701");
    }

    @Test
    public void parse3DSecureResponse() throws Exception {
        String data = "{\"3DSecure\":{\"authenticationRedirect\":{\"customized\":{\"acsUrl\":\"https://www.issuer.com/acsUrl\",\"paReq\":\"PAREQ_VALUE\"}},\"summaryStatus\":\"CARD_ENROLLED\",\"xid\":\"XID_VALUE\"},\"3DSecureId\":\"SECURE_ID_VALUE\",\"merchant\":\"TESTMERCHANTID\",\"response\":{\"3DSecure\":{\"gatewayCode\":\"CARD_ENROLLED\"}}}";
        SecureId secureId = ApiResponseService.parse3DSecureResponse(data);

        assertEquals(secureId.getAcsUrl(), "https://www.issuer.com/acsUrl");
        assertEquals(secureId.getStatus(), "CARD_ENROLLED");
        assertEquals(secureId.getPaReq(), "PAREQ_VALUE");
    }

    @Test
    public void parseHostedCheckoutResponse() throws Exception {
        String data = "{\"amount\":100.00,\"billing\":{\"address\":{\"city\":\"St. Louis\",\"country\":\"USA\",\"postcodeZip\":\"63001\",\"stateProvince\":\"MO\",\"street\":\"123 Main St., #456, #456\"}},\"creationTime\":\"2018-01-26T22:42:47.769Z\",\"currency\":\"USD\",\"customer\":{\"firstName\":\"John\",\"lastName\":\"Smith\"},\"description\":\"Ordered goods\",\"device\":{\"browser\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36\",\"ipAddress\":\"65.254.97.40\"},\"id\":\"2AbGbrb3xl\",\"merchant\":\"TESTMERCHANTID\",\"result\":\"SUCCESS\",\"sourceOfFunds\":{\"provided\":{\"card\":{\"brand\":\"MASTERCARD\",\"expiry\":{\"month\":\"5\",\"year\":\"21\"},\"fundingMethod\":\"CREDIT\",\"issuer\":\"BANCO DEL PICHINCHA, C.A.\",\"nameOnCard\":\"John Smith\",\"number\":\"512345xxxxxx0008\",\"scheme\":\"MASTERCARD\"}},\"type\":\"CARD\"},\"status\":\"CAPTURED\",\"totalAuthorizedAmount\":100.00,\"totalCapturedAmount\":100.00,\"totalRefundedAmount\":0.00,\"transaction\":[{\"authorizationResponse\":{\"posData\":\"1605S0100130\",\"transactionIdentifier\":\"AmexTidTest\"},\"billing\":{\"address\":{\"city\":\"St. Louis\",\"country\":\"USA\",\"postcodeZip\":\"63001\",\"stateProvince\":\"MO\",\"street\":\"123 Main St., #456, #456\"}},\"customer\":{\"firstName\":\"John\",\"lastName\":\"Smith\"},\"device\":{\"browser\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36\",\"ipAddress\":\"65.254.97.40\"},\"gatewayEntryPoint\":\"CHECKOUT\",\"merchant\":\"TESTMERCHANTID\",\"order\":{\"amount\":100.00,\"creationTime\":\"2018-01-26T22:42:47.769Z\",\"currency\":\"USD\",\"description\":\"Ordered goods\",\"id\":\"2AbGbrb3xl\",\"status\":\"CAPTURED\",\"totalAuthorizedAmount\":100.00,\"totalCapturedAmount\":100.00,\"totalRefundedAmount\":0.00},\"response\":{\"acquirerCode\":\"00\",\"cardSecurityCode\":{\"acquirerCode\":\"M\",\"gatewayCode\":\"MATCH\"},\"gatewayCode\":\"APPROVED\"},\"result\":\"SUCCESS\",\"sourceOfFunds\":{\"provided\":{\"card\":{\"brand\":\"MASTERCARD\",\"expiry\":{\"month\":\"5\",\"year\":\"21\"},\"fundingMethod\":\"CREDIT\",\"issuer\":\"BANCO DEL PICHINCHA, C.A.\",\"nameOnCard\":\"John Smith\",\"number\":\"512345xxxxxx0008\",\"scheme\":\"MASTERCARD\"}},\"type\":\"CARD\"},\"timeOfRecord\":\"2018-01-26T22:42:47.769Z\",\"transaction\":{\"acquirer\":{\"batch\":1,\"id\":\"SYSTEST_ACQ1\",\"merchantId\":\"646515314\"},\"amount\":100.00,\"authorizationCode\":\"004745\",\"currency\":\"USD\",\"frequency\":\"SINGLE\",\"id\":\"1\",\"receipt\":\"180126285\",\"source\":\"INTERNET\",\"terminal\":\"9358\",\"type\":\"PAYMENT\"},\"version\":\"45\"}]}";
        TransactionResponse response = ApiResponseService.parseHostedCheckoutResponse(data);

        assertEquals(response.getApiResult(), "SUCCESS");
        assertEquals(response.getGatewayCode(), "APPROVED");
        assertEquals(response.getOrderAmount(), "100.00");
        assertEquals(response.getOrderCurrency(), "USD");
        assertEquals(response.getOrderId(), "2AbGbrb3xl");
        assertEquals(response.getOrderDescription(), "Ordered goods");
    }

//    @Test
//    public void parseMasterpassResponse() throws Exception {
//
//    }
//
//    @Test
//    public void parseBrowserPaymentResponse() throws Exception {
//
//    }
//
//    @Test
//    public void parseWalletResponse() throws Exception {
//
//    }
//
//    @Test
//    public void parseNVPResponse() throws Exception {
//
//    }
//
//    @Test
//    public void retrieveSession() throws Exception {
//
//    }
//
//    @Test
//    public void getBrowserPaymentRedirectUrl() throws Exception {
//
//    }
}
