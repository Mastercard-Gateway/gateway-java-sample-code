/*
 * Copyright (c) 2019 MasterCard. All rights reserved.
 */

package com.gateway.app;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {
        "gateway.base.url=https://test-gateway.com",
        "gateway.merchant.id=testMerchant",
        "gateway.api.password=testP4ssword"
})
public class MainTest {
    @Autowired
    private ApiController apiController;
    @Autowired
    private WebController webController;

    @Test
    public void main() {
        assertThat(apiController).isNotNull();
        assertThat(webController).isNotNull();

    }
}