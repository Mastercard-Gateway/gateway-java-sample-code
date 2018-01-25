package com.gateway.client;

import com.gateway.app.Config;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Service client class for making API requests using Name Value Pair (NVP) protocol
 */

public final class NVPApiClient {

    private static final Logger logger = LoggerFactory.getLogger(NVPApiClient.class);

    private static final String FORM_URL_ENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded; charset=UTF-8";
    private static final String CONTENT_TYPE_HEADER = "Content-type";

    /**
     * Performs a POST operation (required for the following API operations: PROCESS_ACS_RESULT, CREATE_CHECKOUT_SESSION)
     *
     * @param data       JSON payload
     * @param requestUrl API endpoint
     * @param config     contains frequently used information like Merchant ID, API password, etc.
     * @return body
     * @throws Exception
     */
    public String postTransaction(Map<String, String> data, String requestUrl, Config config) throws Exception {

        HttpPost httpPost = new HttpPost(requestUrl);
        httpPost.setHeader(CONTENT_TYPE_HEADER, FORM_URL_ENCODED_CONTENT_TYPE);

        data.put("merchant", config.getMerchantId());
        configureRequest(httpPost, data, config);

        return ApiService.executeHTTPMethod(httpPost, config, ApiProtocol.NVP);
    }

    private void configureRequest(HttpPost httpPost, Map<String, String> data, Config config) throws UnsupportedEncodingException {

        try {
            ArrayList<NameValuePair> postParameters = new ArrayList<>();

            // Add username/password to request
            postParameters.add(new BasicNameValuePair("apiUsername", config.getApiUsername()));
            postParameters.add(new BasicNameValuePair("apiPassword", config.getApiPassword()));

            // Add data to request
            for (String key : data.keySet()) {
                if (data.get(key) != null) {
                    postParameters.add(new BasicNameValuePair(key, data.get(key)));
                }
            }
            httpPost.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
        }
        catch (UnsupportedEncodingException uee) {
            logger.error("Error adding POST parameters to request object");
            throw uee;
        }

    }
}