package com.gateway.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gateway.client.WebhookNotification;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class WebHooksController {


    private ObjectMapper jsonMapper = new ObjectMapper();

    @GetMapping("/webhooks")
    public ModelAndView showWebhooks() {
        ModelAndView mav = new ModelAndView("webhooks");
        return mav;
    }

    @GetMapping("/list-webhook-notifications")
    public List<WebhookNotification> listWebhooks() throws IOException {
        System.out.println("Listing Webhooks Notifications...");

        File notificationsFolder = new File(Config.WEBHOOKS_NOTIFICATION_FOLDER);

        File[] files = notificationsFolder.listFiles();

        List<WebhookNotification> notifications = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                WebhookNotification notification = jsonMapper.readValue(file, WebhookNotification.class);
                notifications.add(notification);
            }
        } else {
            System.out.println("No webhook notifications files found!");
        }
        return notifications;
    }

    @PostMapping("/process-webhook")
    public ModelAndView processWebhook(@RequestBody String payload) throws IOException {
        System.out.println("Processing Webhook Notications....");
        JsonObject payloadJSON = new Gson().fromJson(payload, JsonObject.class);
        JsonObject order = (JsonObject) payloadJSON.get("order");
        JsonObject transaction = (JsonObject) payloadJSON.get("transaction");

        writeWebhookNotification(order.get("id").getAsString(), transaction.get("id").getAsString(), order.get("status").getAsString(), order.get("amount").getAsString());

        ModelAndView mav = new ModelAndView("webhooks");
        return mav;
    }

    private void writeWebhookNotification(String orderId, String transactionId, String orderStatus, String orderAmount) throws IOException {
        FileWriter fileWriter = null;
        try {
            System.out.println("Webhook Notification - orderId = " + orderId + ", transactionId = " + transactionId + ", orderStatus = " + orderStatus + ", Amount = " + orderAmount);

            WebhookNotification notification = new WebhookNotification(orderId, transactionId, orderStatus, orderAmount);

            File jsonFile = new File(Config.WEBHOOKS_NOTIFICATION_FOLDER, "WebHookNotifications_" + notification.getTimestamp() + ".json");

            System.out.println("Writing webhook notification file - " + jsonFile.getAbsolutePath() + "...");

            jsonMapper.writeValue(jsonFile, WebhookNotification.class);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                fileWriter.close();
            }
        }
    }


}