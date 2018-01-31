package com.gateway;

import com.gateway.app.Config;
import com.gateway.client.ApiException;
import com.gateway.client.ApiResponseService;
import com.gateway.client.HostedSession;
import com.gateway.response.SecureIdEnrollmentResponse;
import com.gateway.response.BrowserPaymentResponse;
import com.gateway.response.TransactionResponse;
import com.gateway.response.WalletResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ApiResponseServiceTest {

    private Config config;

    @Before
    public void setUp() {
        config = new Config();
        config.setMerchantId("TESTMERCHANTID");
        config.setApiPassword("APIPASSWORD1234");
        config.setApiBaseURL("https://test-gateway.mastercard.com");
        config.setGatewayHost("https://test-gateway.mastercard.com");
        config.setCurrency("USD");
        config.setApiVersion(45);
    }

    @Test
    public void parseSessionResponse() throws Exception {
        String data = "{\"merchant\":\"TESTAB2894354\",\"result\":\"SUCCESS\",\"session\":{\"id\":\"SESSION0002799480514F69145320L2\",\"updateStatus\":\"SUCCESS\",\"version\":\"6f8b683701\"},\"successIndicator\":\"0a292205c57e4dc8\"}";
        HostedSession session = ApiResponseService.parseSessionResponse(data);

        assertEquals(session.getId(), "SESSION0002799480514F69145320L2");
        assertEquals(session.getSuccessIndicator(), "0a292205c57e4dc8");
        assertEquals(session.getVersion(), "6f8b683701");
    }

    @Test
    public void parse3DSecureResponse() throws Exception {
        String data = "{\"3DSecure\":{\"authenticationRedirect\":{\"customized\":{\"acsUrl\":\"https://www.issuer.com/acsUrl\",\"paReq\":\"PAREQ_VALUE\"}},\"summaryStatus\":\"CARD_ENROLLED\"},\"3DSecureId\":\"wqUyNrvOO6\",\"merchant\":\"TESTAB2894354\",\"response\":{\"3DSecure\":{\"gatewayCode\":\"CARD_ENROLLED\"}}}";
        SecureIdEnrollmentResponse secureIdEnrollmentResponse = ApiResponseService.parse3DSecureResponse(data);

        assertEquals(secureIdEnrollmentResponse.getAcsUrl(), "https://www.issuer.com/acsUrl");
        assertEquals(secureIdEnrollmentResponse.getStatus(), "CARD_ENROLLED");
        assertEquals(secureIdEnrollmentResponse.getPaReq(), "PAREQ_VALUE");
    }

    @Test
    public void parseHostedCheckoutResponse() throws Exception {
        String data = "{\"amount\":100.00,\"billing\":{\"address\":{\"city\":\"St. Louis\",\"country\":\"USA\",\"postcodeZip\":\"63001\",\"stateProvince\":\"MO\",\"street\":\"123 Main St., #456, #456\"}},\"currency\":\"USD\",\"customer\":{\"firstName\":\"John\",\"lastName\":\"Smith\"},\"description\":\"Ordered goods\",\"device\":{\"browser\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36\",\"ipAddress\":\"65.254.97.40\"},\"id\":\"2AbGbrb3xl\",\"merchant\":\"TESTMERCHANTID\",\"result\":\"SUCCESS\",\"sourceOfFunds\":{\"provided\":{\"card\":{\"brand\":\"MASTERCARD\",\"expiry\":{\"month\":\"5\",\"year\":\"21\"},\"fundingMethod\":\"CREDIT\",\"issuer\":\"BANCO DEL PICHINCHA, C.A.\",\"nameOnCard\":\"John Smith\",\"number\":\"512345xxxxxx0008\",\"scheme\":\"MASTERCARD\"}},\"type\":\"CARD\"},\"status\":\"CAPTURED\",\"totalAuthorizedAmount\":100.00,\"totalCapturedAmount\":100.00,\"totalRefundedAmount\":0.00,\"transaction\":[{\"authorizationResponse\":{\"posData\":\"1605S0100130\",\"transactionIdentifier\":\"AmexTidTest\"},\"billing\":{\"address\":{\"city\":\"St. Louis\",\"country\":\"USA\",\"postcodeZip\":\"63001\",\"stateProvince\":\"MO\",\"street\":\"123 Main St., #456, #456\"}},\"customer\":{\"firstName\":\"John\",\"lastName\":\"Smith\"},\"device\":{\"browser\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36\",\"ipAddress\":\"65.254.97.40\"},\"gatewayEntryPoint\":\"CHECKOUT\",\"merchant\":\"TESTMERCHANTID\",\"order\":{\"amount\":100.00,\"creationTime\":\"2018-01-26T22:42:47.769Z\",\"currency\":\"USD\",\"description\":\"Ordered goods\",\"id\":\"2AbGbrb3xl\",\"status\":\"CAPTURED\",\"totalAuthorizedAmount\":100.00,\"totalCapturedAmount\":100.00,\"totalRefundedAmount\":0.00},\"response\":{\"acquirerCode\":\"00\",\"cardSecurityCode\":{\"acquirerCode\":\"M\",\"gatewayCode\":\"MATCH\"},\"gatewayCode\":\"APPROVED\"},\"result\":\"SUCCESS\",\"sourceOfFunds\":{\"provided\":{\"card\":{\"brand\":\"MASTERCARD\",\"expiry\":{\"month\":\"5\",\"year\":\"21\"},\"fundingMethod\":\"CREDIT\",\"issuer\":\"BANCO DEL PICHINCHA, C.A.\",\"nameOnCard\":\"John Smith\",\"number\":\"512345xxxxxx0008\",\"scheme\":\"MASTERCARD\"}},\"type\":\"CARD\"},\"timeOfRecord\":\"2018-01-26T22:42:47.769Z\",\"transaction\":{\"acquirer\":{\"batch\":1,\"id\":\"SYSTEST_ACQ1\",\"merchantId\":\"646515314\"},\"amount\":100.00,\"authorizationCode\":\"004745\",\"currency\":\"USD\",\"frequency\":\"SINGLE\",\"id\":\"1\",\"receipt\":\"180126285\",\"source\":\"INTERNET\",\"terminal\":\"9358\",\"type\":\"PAYMENT\"},\"version\":\"45\"}]}";
        TransactionResponse response = ApiResponseService.parseHostedCheckoutResponse(data);

        assertEquals(response.getApiResult(), "SUCCESS");
        assertEquals(response.getGatewayCode(), "APPROVED");
        assertEquals(response.getOrderAmount(), "100.00");
        assertEquals(response.getOrderCurrency(), "USD");
        assertEquals(response.getOrderId(), "2AbGbrb3xl");
        assertEquals(response.getOrderDescription(), "Ordered goods");
    }

    @Test
    public void parseMasterpassResponse() throws Exception {
        String data = "{\"authorizationResponse\":{\"cardLevelIndicator\":\"88\",\"commercialCard\":\"888\",\"commercialCardIndicator\":\"3\",\"marketSpecificData\":\"8\",\"processingCode\":\"003000\",\"responseCode\":\"00\",\"returnAci\":\"8\",\"stan\":\"236396\",\"transactionIdentifier\":\"123456789012345\",\"validationCode\":\"6789\"},\"billing\":{\"address\":{\"city\":\"Saint Louis\",\"country\":\"USA\",\"postcodeZip\":\"63139\",\"stateProvince\":\"US-MO\",\"street\":\"6701 Clayton Avenue\"}},\"customer\":{\"email\":\"ellen_heitman@mastercard.com\",\"firstName\":\"Ellen\",\"lastName\":\"Heitman\",\"phone\":\"3143130360\"},\"gatewayEntryPoint\":\"WEB_SERVICES_API\",\"merchant\":\"TESTCSTESTMID\",\"order\":{\"amount\":5000.00,\"creationTime\":\"2018-01-28T03:31:29.450Z\",\"currency\":\"USD\",\"id\":\"1iUe0JRNYK\",\"status\":\"CAPTURED\",\"totalAuthorizedAmount\":5000.00,\"totalCapturedAmount\":5000.00,\"totalRefundedAmount\":0.00,\"walletIndicator\":\"101\",\"walletProvider\":\"MASTERPASS_ONLINE\"},\"response\":{\"acquirerCode\":\"00\",\"acquirerMessage\":\"Approved\",\"gatewayCode\":\"APPROVED\"},\"result\":\"SUCCESS\",\"risk\":{\"response\":{\"gatewayCode\":\"ACCEPTED\",\"review\":{\"decision\":\"NOT_REQUIRED\"},\"rule\":[{\"id\":\"GATEKEEPER\",\"name\":\"Gatekeeper\",\"recommendation\":\"NO_ACTION\",\"score\":0,\"type\":\"EXTERNAL_RULE\"},{\"data\":\"444000\",\"name\":\"MERCHANT_BIN_RANGE\",\"recommendation\":\"NO_ACTION\",\"type\":\"MERCHANT_RULE\"},{\"name\":\"SUSPECT_CARD_LIST\",\"recommendation\":\"NO_ACTION\",\"type\":\"MERCHANT_RULE\"},{\"name\":\"TRUSTED_CARD_LIST\",\"recommendation\":\"NO_ACTION\",\"type\":\"MERCHANT_RULE\"},{\"data\":\"444000\",\"name\":\"MSO_BIN_RANGE\",\"recommendation\":\"NO_ACTION\",\"type\":\"MSO_RULE\"}],\"totalScore\":0}},\"shipping\":{\"address\":{\"city\":\"Saint Louis\",\"country\":\"USA\",\"postcodeZip\":\"63139\",\"stateProvince\":\"US-MO\",\"street\":\"6701 Clayton Avenue\"},\"contact\":{\"firstName\":\"Ellen\",\"lastName\":\"Heitman\",\"phone\":\"3143130360\"}},\"sourceOfFunds\":{\"provided\":{\"card\":{\"brand\":\"VISA\",\"expiry\":{\"month\":\"5\",\"year\":\"21\"},\"fundingMethod\":\"DEBIT\",\"issuer\":\"CLYDESDALE BANK PLC\",\"nameOnCard\":\"Ellen Heitman\",\"number\":\"444000xxxxxx0022\",\"scheme\":\"VISA\"}},\"type\":\"CARD\"},\"timeOfRecord\":\"2018-01-28T03:31:29.450Z\",\"transaction\":{\"acquirer\":{\"batch\":20180128,\"date\":\"0128\",\"id\":\"CBA_S2I\",\"merchantId\":\"1234567890\",\"settlementDate\":\"2018-01-28\",\"timeZone\":\"+1100\",\"transactionId\":\"123456789012345\"},\"amount\":5000.00,\"authorizationCode\":\"236396\",\"currency\":\"USD\",\"frequency\":\"SINGLE\",\"id\":\"pM5TwoDocz\",\"receipt\":\"802803236396\",\"source\":\"INTERNET\",\"terminal\":\"CBAS2I01\",\"type\":\"PAYMENT\"},\"version\":\"45\"}";
        TransactionResponse response = ApiResponseService.parseMasterpassResponse(data);

        assertEquals(response.getApiResult(), "SUCCESS");
        assertEquals(response.getGatewayCode(), "APPROVED");
        assertEquals(response.getOrderAmount(), "5000.00");
        assertEquals(response.getOrderCurrency(), "USD");
        assertEquals(response.getOrderId(), "1iUe0JRNYK");
    }

    @Test
    public void parseBrowserPaymentResponse() throws Exception {
        String data = "{\"browserPayment\":{\"interaction\":{\"status\":\"COMPLETED\",\"timeCompleted\":\"2018-01-29T16:11:19.541Z\",\"timeInitiated\":\"2018-01-29T16:08:39.299Z\",\"timeRedirected\":\"2018-01-29T16:10:20.444Z\",\"timeReturned\":\"2018-01-29T16:11:19.353Z\"},\"operation\":\"PAY\",\"paypal\":{\"displayShippingAddress\":true,\"overrideShippingAddress\":true,\"paymentConfirmation\":\"CONFIRM_AT_PROVIDER\"},\"redirectUrl\":\"https://test-gateway.mastercard.com/bpui/pp/out/BP-4652f0dd79cade57ba6726992464c994\",\"returnUrl\":\"http://localhost:5000/browserPaymentReceipt?transactionId=oZRL5sU3Fm&orderId=Qcgkl4EGnR\"},\"device\":{\"browser\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.119 Safari/537.36\",\"ipAddress\":\"10.149.16.35\"},\"gatewayEntryPoint\":\"WEB_SERVICES_API\",\"merchant\":\"TESTSIMPLIFYDEV1\",\"order\":{\"amount\":50.00,\"creationTime\":\"2018-01-29T16:08:39.296Z\",\"currency\":\"USD\",\"id\":\"Qcgkl4EGnR\",\"status\":\"CAPTURED\",\"totalAuthorizedAmount\":50.00,\"totalCapturedAmount\":50.00,\"totalRefundedAmount\":0},\"response\":{\"acquirerCode\":\"Success\",\"gatewayCode\":\"APPROVED\"},\"result\":\"SUCCESS\",\"shipping\":{\"address\":{\"city\":\"Market City\",\"country\":\"AUS\",\"postcodeZip\":\"4322\",\"stateProvince\":\"Queensland\",\"street\":\"35 Rainbow street\",\"street2\":\"Floor 5, Apartment 34\"},\"contact\":{\"firstName\":\"John\",\"lastName\":\"Smith\",\"phone\":\"0745231111\"}},\"sourceOfFunds\":{\"provided\":{\"paypal\":{\"accountEmail\":\"john@smith.com\",\"accountHolder\":\"John Smith\"}},\"type\":\"PAYPAL\"},\"timeOfRecord\":\"2018-01-29T16:08:39.296Z\",\"transaction\":{\"acquirer\":{\"date\":\"2018-01-29\",\"id\":\"PAYPAL\",\"merchantId\":\"test.sandbox@paypal.com\",\"time\":\"16:11:19\"},\"amount\":50.00,\"currency\":\"USD\",\"frequency\":\"SINGLE\",\"id\":\"oZRL5sU3Fm\",\"receipt\":\"IXBNIX8UFZ06J09Z3\",\"source\":\"CALL_CENTRE\",\"type\":\"PAYMENT\"},\"version\":\"45\"}";
        BrowserPaymentResponse response = ApiResponseService.parseBrowserPaymentResponse(data);

        assertEquals(response.getApiResult(), "SUCCESS");
        assertEquals(response.getGatewayCode(), "APPROVED");
        assertEquals(response.getInteractionStatus(), "COMPLETED");
        assertEquals(response.getOrderAmount(), "50.00");
        assertEquals(response.getOrderCurrency(), "USD");
        assertEquals(response.getOrderId(), "Qcgkl4EGnR");
    }

    @Test
    public void parseWalletResponse() throws Exception {
        String data = "{\"merchant\":\"TESTCSTESTMID\",\"order\":{\"amount\":\"50.00\",\"currency\":\"USD\",\"walletProvider\":\"MASTERPASS_ONLINE\"},\"session\":{\"id\":\"SESSION0002798226376L35023121J8\",\"updateStatus\":\"SUCCESS\",\"version\":\"831cb86303\"},\"version\":\"45\",\"wallet\":{\"masterpass\":{\"allowedCardTypes\":\"visa,master\",\"merchantCheckoutId\":\"MERCHANT_CHECKOUT_ID\",\"originUrl\":\"http://localhost:5000/masterpassResponse\",\"requestToken\":\"REQUEST_TOKEN\"}}}";
        WalletResponse response = ApiResponseService.parseWalletResponse(data, "masterpass");

        assertEquals(response.getOrderCurrency(), "USD");
        assertEquals(response.getOrderAmount(), "50.00");
        assertEquals(response.getAllowedCardTypes(), "visa,master");
        assertEquals(response.getMerchantCheckoutId(), "MERCHANT_CHECKOUT_ID");
        assertEquals(response.getOriginUrl(), "http://localhost:5000/masterpassResponse");
        assertEquals(response.getRequestToken(), "REQUEST_TOKEN");
    }

    @Test
    public void parseNVPResponse() throws Exception {
        String data = "merchant=TESTCSTESTMID&order.amount=50.00&order.currency=USD&order.id=IXoAyo48VS&order.status=CAPTURED&response.gatewayCode=APPROVED&result=SUCCESS";
        Map<String, String> map = ApiResponseService.parseNVPResponse(data);

        assertEquals(map.get("merchant"), "TESTCSTESTMID");
        assertEquals(map.get("order.amount"), "50.00");
        assertEquals(map.get("order.currency"), "USD");
        assertEquals(map.get("order.id"), "IXoAyo48VS");
        assertEquals(map.get("order.status"), "CAPTURED");
        assertEquals(map.get("response.gatewayCode"), "APPROVED");
        assertEquals(map.get("result"), "SUCCESS");
    }

    @Test
    public void parseNVPErrorResponseThrowsApiException() throws Exception {
        String data = "error.cause=INVALID_REQUEST&error.explanation=Value+%27PAY%27+is+invalid.+Pay+request+not+permitted+for+this+merchant.&error.field=apiOperation&error.validationType=INVALID&result=ERROR";

        try {
            Map<String, String> map = ApiResponseService.parseNVPResponse(data);
            fail("Expected exception was not thrown");
        } catch(ApiException e) {
            assertEquals(e.getErrorCode(), "INVALID_REQUEST");
            assertEquals(e.getExplanation(), "Value 'PAY' is invalid. Pay request not permitted for this merchant.");
            assertEquals(e.getField(), "apiOperation");
            assertEquals(e.getValidationType(), "INVALID");
        }
    }

    @Test
    public void getBrowserPaymentRedirectUrl() throws Exception {
        String data = "{\"browserPayment\":{\"interaction\":{\"status\":\"INITIATED\",\"timeInitiated\":\"2018-01-29T16:08:39.299Z\"},\"operation\":\"PAY\",\"paypal\":{\"displayShippingAddress\":true,\"overrideShippingAddress\":true,\"paymentConfirmation\":\"CONFIRM_AT_PROVIDER\"},\"redirectUrl\":\"https://test-gateway.mastercard.com/bpui/pp/out/BP-4652f0dd79cade57ba6726992464c994\",\"returnUrl\":\"http://localhost:5000/browserPaymentReceipt?transactionId=oZRL5sU3Fm&orderId=Qcgkl4EGnR\"},\"gatewayEntryPoint\":\"WEB_SERVICES_API\",\"merchant\":\"TESTSIMPLIFYDEV1\",\"order\":{\"amount\":50.00,\"creationTime\":\"2018-01-29T16:08:39.296Z\",\"currency\":\"USD\",\"id\":\"Qcgkl4EGnR\",\"status\":\"INITIATED\",\"totalAuthorizedAmount\":0,\"totalCapturedAmount\":0,\"totalRefundedAmount\":0},\"response\":{\"gatewayCode\":\"SUBMITTED\"},\"result\":\"SUCCESS\",\"sourceOfFunds\":{\"type\":\"PAYPAL\"},\"timeOfRecord\":\"2018-01-29T16:08:39.296Z\",\"transaction\":{\"acquirer\":{\"date\":\"2018-01-29\",\"id\":\"PAYPAL\",\"merchantId\":\"test.sandbox@paypal.com\",\"time\":\"16:08:39\"},\"amount\":50.00,\"currency\":\"USD\",\"frequency\":\"SINGLE\",\"id\":\"oZRL5sU3Fm\",\"source\":\"CALL_CENTRE\",\"type\":\"PAYMENT\"},\"version\":\"45\"}";

        assertEquals(ApiResponseService.getBrowserPaymentRedirectUrl(data), "https://test-gateway.mastercard.com/bpui/pp/out/BP-4652f0dd79cade57ba6726992464c994");

    }
}
