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
import org.springframework.test.context.TestPropertySource;
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
@TestPropertySource(properties = {
        "gateway.base.url=https://test-gateway.com",
        "gateway.merchant.id=testMerchant",
        "gateway.api.password=testP4ssword",
        "gateway.api.version=51",
        "gateway.apm.api.version=1.1.0"
})
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
        config.setTransactionMode(TransactionMode.AUTHORIZE_CAPTURE);
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

    @Test
    public void showPay_thenRetrievedStatusAndViewNameAndAttributeAreCorrect() throws Exception {
        mockMvc.perform(get("/pay"))
                .andExpect(status().isOk())
                .andExpect(view().name("pay"))
                .andExpect(model().attributeExists("request"))
                .andExpect(model().attributeExists("config"))
                .andDo(print());
    }

    @Test
    public void showPayWithToken_thenRetrievedStatusAndViewNameAndAttributeAreCorrect() throws Exception {
        mockMvc.perform(get("/payWithToken"))
                .andExpect(status().isOk())
                .andExpect(view().name("payWithToken"))
                .andExpect(model().attributeExists("request"))
                .andExpect(model().attributeExists("config"))
                .andDo(print());
    }

    @Test
    public void showAuthorize_thenRetrievedStatusAndViewNameAndAttributeAreCorrect() throws Exception {
        mockMvc.perform(get("/authorize"))
                .andExpect(status().isOk())
                .andExpect(view().name("authorize"))
                .andExpect(model().attributeExists("request"))
                .andExpect(model().attributeExists("config"))
                .andDo(print());
    }

    @Test
    public void showCapture_thenRetrievedStatusAndViewNameAndAttributeAreCorrect() throws Exception {
        mockMvc.perform(get("/capture"))
                .andExpect(status().isOk())
                .andExpect(view().name("capture"))
                .andExpect(model().attributeExists("apiRequest"))
                .andDo(print());
    }

    @Test
    public void showRefund_thenRetrievedStatusAndViewNameAndAttributeAreCorrect() throws Exception {
        mockMvc.perform(get("/refund"))
                .andExpect(status().isOk())
                .andExpect(view().name("refund"))
                .andExpect(model().attributeExists("apiRequest"))
                .andDo(print());
    }

    @Test
    public void showVoid_thenRetrievedStatusAndViewNameAndAttributeAreCorrect() throws Exception {

        mockMvc.perform(get("/void"))
                .andExpect(status().isOk())
                .andExpect(view().name("void"))
                .andExpect(model().attributeExists("apiRequest"))
                .andDo(print());
    }

    @Test
    public void showVerify_thenRetrievedStatusAndViewNameAndAttributeAreCorrect() throws Exception {
        mockMvc.perform(get("/verify"))
                .andExpect(status().isOk())
                .andExpect(view().name("verify"))
                .andExpect(model().attributeExists("request"))
                .andExpect(model().attributeExists("config"))
                .andDo(print());
    }

    @Test
    public void showRetrieve_thenRetrievedStatusAndViewNameAndAttributeAreCorrect() throws Exception {
        mockMvc.perform(get("/retrieve"))
                .andExpect(status().isOk())
                .andExpect(view().name("retrieve"))
                .andExpect(model().attributeExists("apiRequest"))
                .andDo(print());
    }

    /* essentials_exclude_start */
    /* targeted_exclude_start */
    @Test
    public void showPaypal_thenRetrievedStatusAndViewNameAndAttributeAreCorrect() throws Exception {
        mockMvc.perform(get("/paypal"))
                .andExpect(status().isOk())
                .andExpect(view().name("paypal"))
                .andExpect(model().attributeExists("apiRequest"))
                .andExpect(model().attributeExists("config"))
                .andDo(print());
    }
    /* targeted_exclude_end */
    /* essentials_exclude_end */

    /* essentials_exclude_start */
    @Test
    public void showUnionPay_thenRetrievedStatusAndViewNameAndAttributeAreCorrect() throws Exception {
        mockMvc.perform(get("/unionpay"))
                .andExpect(status().isOk())
                .andExpect(view().name("unionpay"))
                .andExpect(model().attributeExists("apiRequest"))
                .andExpect(model().attributeExists("config"))
                .andDo(print());
    }
    /* essentials_exclude_end */

    /* essentials_exclude_start */
    @Test
    public void showMasterPass_thenRetrievedStatusAndViewNameAndAttributeAreCorrect() throws Exception {
        mockMvc.perform(get("/masterpass"))
                .andExpect(status().isOk())
                .andExpect(view().name("masterpass"))
                .andExpect(model().attributeExists("apiRequest"))
                .andExpect(model().attributeExists("config"))
                .andDo(print());
    }
    /* essentials_exclude_end */

    @Test
    public void showPayThroughNVP_thenRetrievedStatusAndViewNameAndAttributeAreCorrect() throws Exception {
        mockMvc.perform(get("/payThroughNVP"))
                .andExpect(status().isOk())
                .andExpect(view().name("payThroughNVP"))
                .andExpect(model().attributeExists("request"))
                .andExpect(model().attributeExists("config"))
                .andDo(print());
    }
}
