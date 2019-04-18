/*
 * Copyright (c) 2019 MasterCard. All rights reserved.
 */

package com.gateway.app;

import com.gateway.client.ApiRequestService;
import com.gateway.client.ApiResponseService;
import com.gateway.model.TransactionMode;
import org.junit.Before;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class WebControllerTest {
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

    @Before
    public void setUp() {
        config.setMerchantId("TESTMERCHANTID");
        config.setApiPassword("APIPASSWORD1234");
        config.setApiBaseURL("https://test-gateway.com");
        config.setGatewayHost("https://test-gateway.com");
        config.setCurrency("USD");
        config.setApiVersion(51);
        config.setTransactionMode(TransactionMode.AUTHORIZE_CAPTURE);
        config.setApmVersion("1.1.0");
    }

    @Test
    public void showRoot_thenRedirectIsFound() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/config"))
                .andExpect(status().isFound());

    }

    @Test
    public void showConfig_thenRetrievedStatusAndViewNameAndAttributeAreCorrect() throws Exception {

        mockMvc.perform(get("/config"))
                .andExpect(status().isOk())
                .andExpect(view().name("config"))
                .andExpect(model().attributeExists("config"))
                .andExpect(model().attributeExists("apmApiVersion"))
                .andExpect(model().attributeExists("baseUrl"))
                .andDo(print());
    }
}
