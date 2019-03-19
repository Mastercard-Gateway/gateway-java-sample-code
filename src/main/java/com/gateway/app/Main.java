/*
 * Copyright (c) 2019 MasterCard. All rights reserved.
 */

package com.gateway.app;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SpringBootApplication
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        initWebhooksNotificationsFolder();
        SpringApplication.run(Main.class, args);
    }

    @RequestMapping("/")
    ModelAndView index() {
        return new ModelAndView("redirect:/config");
    }

    private static void initWebhooksNotificationsFolder() {
        File webhooksNotificationsFolder = new File(Config.WEBHOOKS_NOTIFICATION_FOLDER);
        if (!webhooksNotificationsFolder.exists()) {
            logger.info("Creating Webhooks Notifications folder... ");
            webhooksNotificationsFolder.mkdir();
        } else {
            logger.info("Webhooks Notifications folder already exists!");
            //delete all json files from notifications folder
            File[] files = webhooksNotificationsFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }
}
