package com.gateway.app;

import com.gateway.client.WebhookNotification;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class WebHooksController {

    private static final Logger logger = LoggerFactory.getLogger(WebHooksController.class);


    @Autowired
    private Config config;


    @GetMapping("/webhooks")
    public ModelAndView showWebhooks() {
        ModelAndView mav = new ModelAndView("webhooks");
        return mav;
    }

    @GetMapping("/list-webhook-notifications")
    public @ResponseBody List<WebhookNotification> listWebhooks() throws IOException {

        File notificationsFolder = new File(Config.WEBHOOKS_NOTIFICATION_FOLDER);

        File[] files = notificationsFolder.listFiles();

        List<WebhookNotification> notifications = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                Gson gson = new Gson();
                WebhookNotification notification = gson.fromJson(new FileReader(file), WebhookNotification.class);
                notifications.add(notification);
            }
        } else {
            logger.info("No webhook notifications files found!");
        }
        return notifications;
    }

    @PostMapping("/process-webhook")
    @ResponseStatus(HttpStatus.OK)
    public void processWebhook(@RequestBody String payload, @RequestHeader("X-Notification-Secret") String notificationSecret) throws IOException {

        if (config.getWebhooksNotificationSecret() != null && notificationSecret != null && !config.getWebhooksNotificationSecret().equalsIgnoreCase(notificationSecret)) {
            logger.error("Web hooks notification secret doesn't match, so not processing the incoming request!");
            return;
        }

        JsonObject payloadJSON = new Gson().fromJson(payload, JsonObject.class);
        JsonObject order = (JsonObject) payloadJSON.get("order");
        JsonObject transaction = (JsonObject) payloadJSON.get("transaction");

        writeWebhookNotification(order.get("id").getAsString(), transaction.get("id").getAsString(), order.get("status").getAsString(), order.get("amount").getAsString());
    }

    private void writeWebhookNotification(String orderId, String transactionId, String orderStatus, String orderAmount) throws IOException {
        FileWriter fileWriter = null;
        try {
            logger.info("Webhook Notification - orderId = " + orderId + ", transactionId = " + transactionId + ", orderStatus = " + orderStatus + ", Amount = " + orderAmount);

            long timeInMillis = System.currentTimeMillis();
            File jsonFile = new File(Config.WEBHOOKS_NOTIFICATION_FOLDER, "WebHookNotifications_" + timeInMillis + ".json");

            logger.info("Writing webhook notification file - " + jsonFile.getAbsolutePath() + "...");

            fileWriter = new FileWriter(jsonFile);
            Gson gson = new GsonBuilder().create();

            WebhookNotification notification = new WebhookNotification(orderId, transactionId, orderStatus, orderAmount);
            gson.toJson(notification, fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                fileWriter.close();
            }
        }
    }

}