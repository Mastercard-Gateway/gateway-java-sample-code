package com.gateway;

import com.gateway.app.Config;
import com.gateway.client.ApiResponseService;
import com.gateway.client.CheckoutSession;
import com.gateway.client.SecureId;
import com.gateway.response.TransactionResponse;
import com.gateway.response.WalletResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

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

//    @Test
//    public void parseBrowserPaymentResponse() throws Exception {
//
//    }

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

//    @Test
//    public void parseNVPResponse() throws Exception {
//        String data = "authorizationResponse.cardSecurityCodeError=M&authorizationResponse.commercialCard=888&authorizationResponse.commercialCardIndicator=3&authorizationResponse.financialNetworkCode=777&authorizationResponse.processingCode=003000&authorizationResponse.responseCode=00&authorizationResponse.stan=237436&authorizationResponse.transactionIdentifier=123456789&device.browser=Mozilla%2F5.0+%28Macintosh%3B+Intel+Mac+OS+X+10_13_1%29+AppleWebKit%2F537.36+%28KHTML%2C+like+Gecko%29+Chrome%2F63.0.3239.132+Safari%2F537.36&device.ipAddress=10.149.16.36&gatewayEntryPoint=WEB_SERVICES_API&merchant=TESTCSTESTMID&order.amount=50.00&order.creationTime=2018-01-28T03%3A46%3A52.961Z&order.currency=USD&order.id=IXoAyo48VS&order.status=CAPTURED&order.totalAuthorizedAmount=50.00&order.totalCapturedAmount=50.00&order.totalRefundedAmount=0.00&response.acquirerCode=00&response.acquirerMessage=Approved&response.cardSecurityCode.acquirerCode=M&response.cardSecurityCode.gatewayCode=MATCH&response.gatewayCode=APPROVED&result=SUCCESS&risk.response.gatewayCode=ACCEPTED&risk.response.review.decision=NOT_REQUIRED&risk.response.rule%5B0%5D.id=GATEKEEPER&risk.response.rule%5B0%5D.name=Gatekeeper&risk.response.rule%5B0%5D.recommendation=NO_ACTION&risk.response.rule%5B0%5D.score=0&risk.response.rule%5B0%5D.type=EXTERNAL_RULE&risk.response.rule%5B10%5D.data=UK1&risk.response.rule%5B10%5D.name=MSO_IP_COUNTRY&risk.response.rule%5B10%5D.recommendation=NO_ACTION&risk.response.rule%5B10%5D.type=MSO_RULE&risk.response.rule%5B1%5D.data=512345&risk.response.rule%5B1%5D.name=MERCHANT_BIN_RANGE&risk.response.rule%5B1%5D.recommendation=NO_ACTION&risk.response.rule%5B1%5D.type=MERCHANT_RULE&risk.response.rule%5B2%5D.data=M&risk.response.rule%5B2%5D.name=MERCHANT_CSC&risk.response.rule%5B2%5D.recommendation=NO_ACTION&risk.response.rule%5B2%5D.type=MERCHANT_RULE&risk.response.rule%5B3%5D.data=10.149.16.36&risk.response.rule%5B3%5D.name=MERCHANT_IP_ADDRESS_RANGE&risk.response.rule%5B3%5D.recommendation=NO_ACTION&risk.response.rule%5B3%5D.type=MERCHANT_RULE&risk.response.rule%5B4%5D.data=UK1&risk.response.rule%5B4%5D.name=MERCHANT_IP_COUNTRY&risk.response.rule%5B4%5D.recommendation=NO_ACTION&risk.response.rule%5B4%5D.type=MERCHANT_RULE&risk.response.rule%5B5%5D.name=SUSPECT_CARD_LIST&risk.response.rule%5B5%5D.recommendation=NO_ACTION&risk.response.rule%5B5%5D.type=MERCHANT_RULE&risk.response.rule%5B6%5D.name=TRUSTED_CARD_LIST&risk.response.rule%5B6%5D.recommendation=NO_ACTION&risk.response.rule%5B6%5D.type=MERCHANT_RULE&risk.response.rule%5B7%5D.data=512345&risk.response.rule%5B7%5D.name=MSO_BIN_RANGE&risk.response.rule%5B7%5D.recommendation=NO_ACTION&risk.response.rule%5B7%5D.type=MSO_RULE&risk.response.rule%5B8%5D.data=M&risk.response.rule%5B8%5D.name=MSO_CSC&risk.response.rule%5B8%5D.recommendation=NO_ACTION&risk.response.rule%5B8%5D.type=MSO_RULE&risk.response.rule%5B9%5D.data=10.149.16.36&risk.response.rule%5B9%5D.name=MSO_IP_ADDRESS_RANGE&risk.response.rule%5B9%5D.recommendation=NO_ACTION&risk.response.rule%5B9%5D.type=MSO_RULE&risk.response.totalScore=0&sourceOfFunds.provided.card.brand=MASTERCARD&sourceOfFunds.provided.card.expiry.month=5&sourceOfFunds.provided.card.expiry.year=21&sourceOfFunds.provided.card.fundingMethod=CREDIT&sourceOfFunds.provided.card.issuer=BANCO+DEL+PICHINCHA%2C+C.A.&sourceOfFunds.provided.card.number=512345xxxxxx0008&sourceOfFunds.provided.card.scheme=MASTERCARD&sourceOfFunds.type=CARD&timeOfRecord=2018-01-28T03%3A46%3A52.961Z&transaction.acquirer.batch=20180128&transaction.acquirer.date=0128&transaction.acquirer.id=CBA_S2I&transaction.acquirer.merchantId=1234567890&transaction.acquirer.settlementDate=2018-01-28&transaction.acquirer.timeZone=%2B1100&transaction.acquirer.transactionId=123456789&transaction.amount=50.00&transaction.authorizationCode=237436&transaction.currency=USD&transaction.frequency=SINGLE&transaction.id=nhbh6ldkJo&transaction.receipt=802803237436&transaction.source=INTERNET&transaction.terminal=CBAS2I01&transaction.type=PAYMENT&version=45";
//        Map<String, String> map = ApiResponseService.parseNVPResponse(data);
//    }

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
