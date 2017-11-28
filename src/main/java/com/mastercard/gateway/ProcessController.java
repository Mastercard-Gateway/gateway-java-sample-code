package com.mastercard.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

@Controller
public class ProcessController {

    private static final String DEFAULT_MERCHANT_ID = "TESTSIMPLIFYDEV1";
    private static final String DEFAULT_API_PASSWORD = "0af6b287057c4705f4f1d4da8581c646";
    private static final String DEFAULT_GATEWAY_URL = "https://test-gateway.mastercard.com";

    private static final String MERCHANT_ID = (System.getenv("GATEWAY_MERCHANT_ID") != null && !System.getenv("GATEWAY_MERCHANT_ID").equals("")) ? System.getenv("GATEWAY_MERCHANT_ID") : DEFAULT_MERCHANT_ID;
    private static final String API_PASSWORD = (System.getenv("GATEWAY_API_PASSWORD") != null && !System.getenv("GATEWAY_API_PASSWORD").equals("")) ? System.getenv("GATEWAY_API_PASSWORD") : DEFAULT_API_PASSWORD;
    private static final String GATEWAY_URL = (System.getenv("GATEWAY_BASE_URL") != null && !System.getenv("GATEWAY_BASE_URL").equals("")) ? System.getenv("GATEWAY_BASE_URL") : DEFAULT_GATEWAY_URL;

    @GetMapping("/authorize")
    public ModelAndView showAuthorize() {
        ModelAndView mav = new ModelAndView("authorize");
        ApiRequest req = ApiRequest.createTestRequest("AUTHORIZE");
        req.setTransactionId(randomNumber());
        req.setOrderId(randomNumber());
        mav.addObject("apiRequest", req);
        return mav;
    }

    @GetMapping("/capture")
    public ModelAndView showCapture() {
        ModelAndView mav = new ModelAndView("capture");
        ApiRequest req = ApiRequest.createTestRequest("CAPTURE");
        req.setTransactionId(randomNumber());
        mav.addObject("apiRequest", req);
        return mav;
    }

    @GetMapping("/confirm")
    public ModelAndView showConfirm() {
        ModelAndView mav = new ModelAndView("confirm");
        ApiRequest req = ApiRequest.createTestRequest("CONFIRM_BROWSER_PAYMENT");
        req.setTransactionId(randomNumber());
        req.setOrderId(randomNumber());
        mav.addObject("apiRequest", req);
        return mav;
    }

    @GetMapping("/initiate")
    public ModelAndView showInitiate() {
        ModelAndView mav = new ModelAndView("initiate");
        ApiRequest req = ApiRequest.createTestRequest("INITIATE_BROWSER_PAYMENT");
        req.setTransactionId(randomNumber());
        req.setOrderId(randomNumber());
        req.setSourceType(null);
        mav.addObject("apiRequest", req);
        return mav;
    }

    @GetMapping("/pay")
    public ModelAndView showPay() {
        ModelAndView mav = new ModelAndView("pay");
        ApiRequest req = ApiRequest.createTestRequest("PAY");
        req.setOrderId(randomNumber());
        req.setTransactionId(randomNumber());
        mav.addObject("apiRequest", req);
        return mav;
    }

    @GetMapping("/refund")
    public ModelAndView showRefund() {
        ModelAndView mav = new ModelAndView("refund");
        ApiRequest req = ApiRequest.createTestRequest("REFUND");
        req.setTransactionId(randomNumber());
        mav.addObject("apiRequest", req);
        return mav;
    }

    @GetMapping("/retrieve")
    public ModelAndView showRetrieve() {
        ModelAndView mav = new ModelAndView("retrieve");
        ApiRequest req = ApiRequest.createTestRequest("RETRIEVE_TRANSACTION");
        req.setMethod("GET");
        mav.addObject("apiRequest", req);
        return mav;
    }

    @GetMapping("/update")
    public ModelAndView showUpdate() {
        ModelAndView mav = new ModelAndView("update");
        ApiRequest req = ApiRequest.createTestRequest("UPDATE_AUTHORIZATION");
        req.setTransactionId(randomNumber());
        mav.addObject("apiRequest", req);
        return mav;
    }

    @GetMapping("/verify")
    public ModelAndView showVerify() {
        ModelAndView mav = new ModelAndView("verify");
        ApiRequest req = ApiRequest.createTestRequest("VERIFY");
        req.setOrderId(randomNumber());
        req.setTransactionId(randomNumber());
        mav.addObject("apiRequest", req);
        return mav;
    }

    @GetMapping("/void")
    public ModelAndView showVoid() {
        ModelAndView mav = new ModelAndView("void");
        ApiRequest req = ApiRequest.createTestRequest("VOID");
        req.setTransactionId(randomNumber());
        mav.addObject("apiRequest", req);
        return mav;
    }

    @GetMapping("/hostedCheckout")
    public ModelAndView showHostedCheckout() {
        ModelAndView mav = new ModelAndView("hostedCheckout");
        ApiRequest req = new ApiRequest();
        req.setApiOperation("CREATE_CHECKOUT_SESSION");
        req.setOrderId(randomNumber());
        req.setOrderCurrency("USD");
        //req.setReturnUrl("http://localhost:5000/hostedCheckoutReceipt");

        Merchant merchant = createMerchant();
        Parser parser = new Parser(merchant);
        String requestUrl = parser.sessionRequestUrl();

        String data = parser.parse(req);

        try {
            Connection connection = new Connection(merchant);
            String resp = connection.postTransaction(data);

            JsonObject json = new Gson().fromJson(resp, JsonObject.class);
            JsonObject jsonSession = json.get("session").getAsJsonObject();

            Session session = new Session();
            session.setId(jsonSession.get("id").getAsString());
            session.setVersion(jsonSession.get("version").getAsString());

            mav.addObject("session", session);
            mav.addObject("merchantId", merchant.getMerchantId());
            mav.addObject("apiPassword", merchant.getPassword());
        }
        catch(Exception e) {
            e.printStackTrace();
            mav.addObject("error", e.getMessage());
        }

        return mav;
    }

    @GetMapping("/browserPaymentReceipt")
    public ModelAndView browserPaymentReceipt(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("browserPaymentReceipt");
        String data = "";
        try {
            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            data = buffer.toString();
            mav.addObject("request", data);
        }
        catch(IOException e) {
            e.printStackTrace();
            mav.addObject("error", e.getMessage());
        }

        return mav;
    }

    @GetMapping("/hostedCheckoutReceipt")
    public ModelAndView hostedCheckoutReceipt() {
        ModelAndView mav = new ModelAndView("hostedCheckoutReceipt");
        return mav;
    }

    @PostMapping("/process")
    public ModelAndView process(ApiRequest request) {

        Merchant merchant = createMerchant();
        Parser parser = new Parser(merchant);

        String requestUrl = parser.formRequestUrl(request);
        String data = parser.parse(request);

        String resp = "";

        ModelAndView mav = new ModelAndView("receipt");

        try {
            Connection connection = new Connection(merchant);
            if(request.getMethod().equals("PUT")) {
                resp = connection.sendTransaction(data);
            }
            else if(request.getMethod().equals("GET")) {
                resp = connection.getTransaction();
            }
            ObjectMapper mapper = new ObjectMapper();
            Object prettyResp = mapper.readValue(resp, Object.class);
            Object prettyPayload = mapper.readValue(data, Object.class);
            mav.addObject("resp", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(prettyResp));
            mav.addObject("operation", request.getApiOperation());
            mav.addObject("method", request.getMethod());
            mav.addObject("request", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(prettyPayload));
            mav.addObject("requestUrl", requestUrl);
        }
        catch(Exception e) {
            e.printStackTrace();
            mav.addObject("error", e.getMessage());
        }

        return mav;
    }

    private Merchant createMerchant() {
        Merchant merchant = new Merchant();
        merchant.setMerchantId(MERCHANT_ID);
        merchant.setPassword(API_PASSWORD);
        merchant.setGatewayHost(GATEWAY_URL);
        merchant.setGatewayUrl(GATEWAY_URL + "/api/rest");
        merchant.setApiUsername("merchant." + merchant.getMerchantId());
        return merchant;
    }

    private String randomNumber() {
        return RandomStringUtils.random(10, true, true);
    }

}