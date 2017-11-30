package com.gateway.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gateway.client.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

@Controller
public class WebController {


    @Autowired
    private Config config;

    @GetMapping("/authorize")
    public ModelAndView showAuthorize() {
        ModelAndView mav = new ModelAndView("authorize");
        ApiRequest req = createTestRequest("AUTHORIZE");
        req.setTransactionId(randomNumber());
        req.setOrderId(randomNumber());
        mav.addObject("apiRequest", req);
        return mav;
    }

    @GetMapping("/capture")
    public ModelAndView showCapture() {
        ModelAndView mav = new ModelAndView("capture");
        ApiRequest req = createTestRequest("CAPTURE");
        req.setTransactionId(randomNumber());
        mav.addObject("apiRequest", req);
        return mav;
    }

    @GetMapping("/confirm")
    public ModelAndView showConfirm() {
        ModelAndView mav = new ModelAndView("confirm");
        ApiRequest req = createTestRequest("CONFIRM_BROWSER_PAYMENT");
        req.setTransactionId(randomNumber());
        req.setOrderId(randomNumber());
        mav.addObject("apiRequest", req);
        return mav;
    }

    @GetMapping("/initiate")
    public ModelAndView showInitiate() {
        ModelAndView mav = new ModelAndView("initiate");
        ApiRequest req = createTestRequest("INITIATE_BROWSER_PAYMENT");
        req.setTransactionId(randomNumber());
        req.setOrderId(randomNumber());
        req.setSourceType(null);
        mav.addObject("apiRequest", req);
        return mav;
    }

    @GetMapping("/pay")
    public ModelAndView showPay() {
        ModelAndView mav = new ModelAndView("pay");
        ApiRequest req = createTestRequest("PAY");
        req.setOrderId(randomNumber());
        req.setTransactionId(randomNumber());
        mav.addObject("apiRequest", req);
        return mav;
    }

    @GetMapping("/refund")
    public ModelAndView showRefund() {
        ModelAndView mav = new ModelAndView("refund");
        ApiRequest req = createTestRequest("REFUND");
        req.setTransactionId(randomNumber());
        mav.addObject("apiRequest", req);
        return mav;
    }

    @GetMapping("/retrieve")
    public ModelAndView showRetrieve() {
        ModelAndView mav = new ModelAndView("retrieve");
        ApiRequest req = createTestRequest("RETRIEVE_TRANSACTION");
        req.setMethod("GET");
        mav.addObject("apiRequest", req);
        return mav;
    }

    @GetMapping("/update")
    public ModelAndView showUpdate() {
        ModelAndView mav = new ModelAndView("update");
        ApiRequest req = createTestRequest("UPDATE_AUTHORIZATION");
        req.setTransactionId(randomNumber());
        mav.addObject("apiRequest", req);
        return mav;
    }

    @GetMapping("/verify")
    public ModelAndView showVerify() {
        ModelAndView mav = new ModelAndView("verify");
        ApiRequest req = createTestRequest("VERIFY");
        req.setOrderId(randomNumber());
        req.setTransactionId(randomNumber());
        mav.addObject("apiRequest", req);
        return mav;
    }

    @GetMapping("/void")
    public ModelAndView showVoid() {
        ModelAndView mav = new ModelAndView("void");
        ApiRequest req = createTestRequest("VOID");
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

        Merchant merchant = createMerchant();
        String requestUrl = ClientUtil.getSessionRequestUrl(merchant, req);

        String data = ClientUtil.buildJSONPayload(req);

        try {
            ApiClient connection = new ApiClient(merchant);
            String resp = connection.postTransaction(data);

            JsonObject json = new Gson().fromJson(resp, JsonObject.class);
            JsonObject jsonSession = json.get("session").getAsJsonObject();

<<<<<<< HEAD:src/main/java/com/mastercard/gateway/ProcessController.java
            Session session = new Session();
            session.setId(jsonSession.get("id").getAsString());
            session.setVersion(jsonSession.get("version").getAsString());
            session.setSuccessIndicator(json.get("successIndicator").getAsString());

            mav.addObject("orderId", req.getOrderId());
            mav.addObject("sessionId", session.getId());
            mav.addObject("sessionVersion", session.getVersion());
            mav.addObject("successIndicator", session.getSuccessIndicator());
=======
            CheckoutSession checkoutSession = new CheckoutSession();
            checkoutSession.setId(jsonSession.get("id").getAsString());
            checkoutSession.setVersion(jsonSession.get("version").getAsString());

            mav.addObject("session", checkoutSession);
>>>>>>> refactoring:src/main/java/com/gateway/app/WebController.java
            mav.addObject("merchantId", merchant.getMerchantId());
            mav.addObject("apiPassword", merchant.getPassword());
        } catch (Exception e) {
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
        } catch (IOException e) {
            e.printStackTrace();
            mav.addObject("error", e.getMessage());
        }

        return mav;
    }

    @GetMapping("/hostedCheckoutReceipt/{orderId}/{result}")
    public ModelAndView hostedCheckoutReceipt(@PathVariable(value="orderId") String orderId, @PathVariable(value="result") String result) {

        ModelAndView mav = new ModelAndView("hostedCheckoutReceipt");

        if(result.equals("success")) {
            // Retrieve order details
            ApiRequest req = new ApiRequest();
            req.setApiOperation("RETRIEVE_ORDER");
            req.setOrderId(orderId);

            Merchant merchant = createMerchant();
            Parser parser = new Parser(merchant);
            String requestUrl = parser.formRequestUrl(req);

            try {
                Connection connection = new Connection(merchant);
                String resp = connection.getTransaction();
                mav.addObject("orderDetails", resp);
            }
            catch(Exception e) {
                e.printStackTrace();
                mav.addObject("error", e.getMessage());
            }
        }

        mav.addObject("result", result);
        return mav;
    }

    @PostMapping("/process")
    public ModelAndView process(ApiRequest request) {

        Merchant merchant = createMerchant();
        String requestUrl = ClientUtil.getRequestUrl(merchant, request);
        String jsonPayload = ClientUtil.buildJSONPayload(request);

        String resp = "";

        ModelAndView mav = new ModelAndView("receipt");

        try {
            ApiClient connection = new ApiClient(merchant);
            if (request.getMethod().equals("PUT")) {
                resp = connection.sendTransaction(jsonPayload);
            } else if (request.getMethod().equals("GET")) {
                resp = connection.getTransaction();
            }
            ObjectMapper mapper = new ObjectMapper();
            Object prettyResp = mapper.readValue(resp, Object.class);
            Object prettyPayload = mapper.readValue(jsonPayload, Object.class);
            mav.addObject("resp", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(prettyResp));
            mav.addObject("operation", request.getApiOperation());
            mav.addObject("method", request.getMethod());
            mav.addObject("request", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(prettyPayload));
            mav.addObject("requestUrl", requestUrl);
        } catch (Exception e) {
            e.printStackTrace();
            mav.addObject("error", e.getMessage());
        }

        return mav;
    }

    private Merchant createMerchant() {
        Merchant merchant = new Merchant();
        merchant.setMerchantId(config.getMerchantId());
        merchant.setPassword(config.getApiPassword());
        merchant.setGatewayHost(config.getApiBaseURL());
        merchant.setGatewayUrl(config.getApiBaseURL() + "/api/rest");
        merchant.setApiUsername("merchant." + merchant.getMerchantId());
        return merchant;
    }

    private String randomNumber() {
        return RandomStringUtils.random(10, true, true);
    }

    public static ApiRequest createTestRequest(String apiOperation) {
        ApiRequest req = new ApiRequest();
        req.setApiOperation(apiOperation);
        req.setMethod("PUT");
        req.setSourceType("CARD");
        req.setCardNumber("5123450000000008");
        req.setExpiryMonth("5");
        req.setExpiryYear("21");
        req.setSecurityCode("100");
        req.setOrderAmount("5000");
        req.setTransactionAmount("5000");
        req.setOrderCurrency("USD");
        req.setTransactionCurrency("USD");
        //TODO: This URL should come from the client dynamically
        req.setReturnUrl("http://localhost:5000/browserPaymentReceipt");
        return req;
    }

}