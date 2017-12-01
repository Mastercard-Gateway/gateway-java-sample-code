package com.gateway.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public final class ClientUtil {

    public static String getRequestUrl(Merchant merchant, ApiRequest request) {
        String url = merchant.getGatewayUrl() + "/version/" +
                request.getApiVersion() +
                "/merchant/" +
                merchant.getMerchantId() +
                "/order/" +
                request.getOrderId();
        if(notNullOrEmpty(request.getTransactionId())) {
            url += "/transaction/" + request.getTransactionId();
        }
        merchant.setGatewayUrl(url);
        return merchant.getGatewayUrl();
    }

    public static String getSessionRequestUrl(Merchant merchant, ApiRequest request) {
        String url = merchant.getGatewayUrl() + "/version/" +
                request.getApiVersion() +
                "/merchant/" +
                merchant.getMerchantId() +
                "/session";
        merchant.setGatewayUrl(url);
        return merchant.getGatewayUrl();
    }

    public static String buildJSONPayload(ApiRequest request) {
        JsonObject order = new JsonObject();
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
        if (notNullOrEmpty(request.getSourceType())) addPaymentConfirmation(browserPayment, request);

        JsonObject interaction = new JsonObject();
        if (notNullOrEmpty(request.getReturnUrl())) {
            // Return URL needs to be added differently for browser payments and hosted checkout payments
            if (request.getApiOperation().equals("CREATE_CHECKOUT_SESSION")) {
                interaction.addProperty("returnUrl", request.getReturnUrl());
            } else if (request.getApiOperation().equals("INITIATE_BROWSER_PAYMENT") || request.getApiOperation().equals("CONFIRM_BROWSER_PAYMENT")) {
                browserPayment.addProperty("returnUrl", request.getReturnUrl());
            }
        }

        // Add all the elements to the main JSON object we'll return from this method
        JsonObject data = new JsonObject();
        if (notNullOrEmpty(request.getApiOperation())) data.addProperty("apiOperation", request.getApiOperation());
        if (!order.entrySet().isEmpty()) data.add("order", order);
        if (!transaction.entrySet().isEmpty()) data.add("transaction", transaction);
        if (!sourceOfFunds.entrySet().isEmpty()) data.add("sourceOfFunds", sourceOfFunds);
        if (!browserPayment.entrySet().isEmpty()) data.add("browserPayment", browserPayment);
        if (!interaction.entrySet().isEmpty()) data.add("interaction", interaction);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        return gson.toJson(data);
    }

    private static boolean notNullOrEmpty(String value) {
        return (value != null && !value.equals(""));
    }

    private static void addPaymentConfirmation(JsonObject browserPayment, ApiRequest request) {
        switch (request.getSourceType().toUpperCase()) {
            case "ALIPAY":
                JsonObject alipay = new JsonObject();
                alipay.addProperty("paymentConfirmation", "CONFIRM_AT_PROVIDER");
                browserPayment.add("alipay", alipay);
                break;
            case "BANCANET":
                JsonObject bancanet = new JsonObject();
                bancanet.addProperty("paymentConfirmation", "CONFIRM_AT_PROVIDER");
                browserPayment.add("bancanet", bancanet);
                break;
            case "GIROPAY":
                JsonObject giropay = new JsonObject();
                giropay.addProperty("paymentConfirmation", "CONFIRM_AT_PROVIDER");
                browserPayment.add("giropay", giropay);
                break;
            case "IDEAL":
                JsonObject ideal = new JsonObject();
                ideal.addProperty("paymentConfirmation", "CONFIRM_AT_PROVIDER");
                browserPayment.add("ideal", ideal);
                break;
            case "MULTIBANCO":
                JsonObject multibanco = new JsonObject();
                multibanco.addProperty("paymentConfirmation", "CONFIRM_AT_PROVIDER");
                browserPayment.add("multibanco", multibanco);
                break;
            case "PAYPAL":
                JsonObject paypal = new JsonObject();
                paypal.addProperty("paymentConfirmation", "CONFIRM_AT_PROVIDER");
                browserPayment.add("paypal", paypal);
                break;
            case "SOFORT":
                JsonObject sofort = new JsonObject();
                sofort.addProperty("paymentConfirmation", "CONFIRM_AT_PROVIDER");
                browserPayment.add("sofort", sofort);
                break;
            case "UNION_PAY":
                JsonObject unionpay = new JsonObject();
                unionpay.addProperty("paymentConfirmation", "CONFIRM_AT_PROVIDER");
                browserPayment.add("unionpay", unionpay);
                break;
        }
    }
}
