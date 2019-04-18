/*
 * Copyright (c) 2019 MasterCard. All rights reserved.
 */

package com.gateway.app;

import com.gateway.client.ApiRequestService;
import com.gateway.client.ApiResponseService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class WebHooksControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApiRequestService apiRequestService;
    @MockBean
    private ApiResponseService apiResponseService;
    @Autowired
    private WebController webController;

    @Autowired
    private Config config;


    @Test
    public void showWebhooks() throws Exception {
        mockMvc.perform(get("/webhooks"))
                .andExpect(status().isOk())
                .andExpect(view().name("webhooks"))
                .andDo(print());
    }
}