package com.gateway.client;

import com.gateway.app.Config;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service client class for making API requests using REST protocol using JSON
 */

public final class RESTApiClient {

    private static final Logger logger = LoggerFactory.getLogger(RESTApiClient.class);
    private static final String UTF8_ENCODING = "UTF-8";

    /**
     * Performs a PUT operation (required for the following API operations: AUTHORIZE, CAPTURE, PAY, REFUND, UPDATE_AUTHORIZATION, VERIFY, VOID, CHECK_3DS_ENROLLMENT, INITIATE_BROWSER_PAYMENT)
     *
     * @param data       JSON payload
     * @param requestUrl API endpoint
     * @param config     contains frequently used information like Merchant ID, API password, etc.
     * @return body
     * @throws Exception
     */
    public String sendTransaction(String data, String requestUrl, Config config) throws Exception {
            HttpPut httpPut = new HttpPut(requestUrl);
            httpPut.setEntity(new StringEntity(data, UTF8_ENCODING));

            return ApiService.executeHTTPMethod(httpPut, config, ApiProtocol.REST);
    }

    /**
     * Performs a POST operation (required for the following API operations: PROCESS_ACS_RESULT, CREATE_CHECKOUT_SESSION)
     *
     * @param data   JSON payload
     * @param requestUrl API endpoint
     * @param config contains frequently used information like Merchant ID, API password, etc.
     * @return body
     * @throws Exception
     */
    public String postTransaction(String data, String requestUrl, Config config) throws Exception {
        HttpPost httpPost = new HttpPost(requestUrl);
        httpPost.setEntity(new StringEntity(data, UTF8_ENCODING));

        return ApiService.executeHTTPMethod(httpPost, config, ApiProtocol.REST);
    }

    /**
     * Performs a POST operation without a body (required for creating a generic gateway session)
     *
     * @param requestUrl API endpoint
     * @param config     contains frequently used information like Merchant ID, API password, etc.
     * @return body
     * @throws Exception
     */
    public String postTransaction(String requestUrl, Config config) throws Exception {
        HttpPost httpPost = new HttpPost(requestUrl);

        return ApiService.executeHTTPMethod(httpPost, config, ApiProtocol.REST);
    }

    /**
     * Performs a GET operation (required for the following API operations: Retrieve session, Retrieve transaction, Retrieve order)
     *
     * @param requestUrl API endpoint
     * @param config     contains frequently used information like Merchant ID, API password, etc.
     * @return body
     * @throws Exception
     */
    public String getTransaction(String requestUrl, Config config) throws Exception {
        HttpGet httpGet = new HttpGet(requestUrl);

        return ApiService.executeHTTPMethod(httpGet, config, ApiProtocol.REST);
    }
}