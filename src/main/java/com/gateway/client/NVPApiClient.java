package com.gateway.client;

import com.gateway.app.Config;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.util.Map;

/**
 * Service client class for making API requests using Name Value Pair (NVP) protocol
 */

public final class NVPApiClient {


    private static final String FORM_URL_ENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded; charset=UTF-8";
    private static final String CONTENT_TYPE_HEADER = "Content-type";


    private void populateData(PostMethod postMethod, Map<String, String> data) {
        for (String key : data.keySet()) {
            if (data.get(key) != null) {
                postMethod.addParameter(key, data.get(key));
            }
        }
    }

    /**
     * Performs a POST operation (required for the following API operations: PROCESS_ACS_RESULT, CREATE_CHECKOUT_SESSION)
     *
     * @param data       JSON payload
     * @param requestUrl API endpoint
     * @param config     contains frequently used information like Merchant ID, API password, etc.
     * @return body
     * @throws Exception
     */
    public String postData(Map<String, String> data, String requestUrl, Config config) throws Exception {


        HttpClient httpClient = new HttpClient();

        PostMethod postMethod = new PostMethod(requestUrl);

        postMethod.setDoAuthentication(true);

        postMethod.addRequestHeader(CONTENT_TYPE_HEADER, FORM_URL_ENCODED_CONTENT_TYPE);

        populateAuthenticationData(postMethod, config);

        data.put("merchant", config.getMerchantId());

        populateData(postMethod, data);

        HostConfiguration hostConfig = new HostConfiguration();
        hostConfig.setHost(config.getGatewayHost());
        configureProxy(httpClient, config);
        String body = null;

        try {
            //make POST call
            System.out.println("Making POST call...");
            httpClient.executeMethod(hostConfig, postMethod);

            body = postMethod.getResponseBodyAsString();

            System.out.println("Response body = " + body);
        } catch (IOException ioe) {
            throw new Exception(ioe);
        } finally {
            postMethod.releaseConnection();
        }

        return body;
    }

    private void populateAuthenticationData(PostMethod postMethod, Config config) {
        postMethod.addParameter("apiUsername", "merchant." + config.getMerchantId());
        postMethod.addParameter("apiPassword", config.getApiPassword());
    }

    /**
     * Check if proxy config is defined; if so configure the host and http client to tunnel through
     *
     * @param httpClient
     * @param config     object that contains frequently used information like Merchant ID, API password, etc.
     * @return void
     */
    private void configureProxy(HttpClient httpClient, Config config) {
        // If proxy server is defined, set the host configuration.
        if (config.getProxyServer() != null) {
            HostConfiguration hostConfig = httpClient.getHostConfiguration();
            hostConfig.setHost(config.getGatewayHost());
            hostConfig.setProxy(config.getProxyServer(), config.getProxyPort());

        }
        // If proxy authentication is defined, set proxy credentials
        if (config.getProxyUsername() != null) {
            NTCredentials proxyCredentials =
                    new NTCredentials(config.getProxyUsername(),
                            config.getProxyPassword(), config.getGatewayHost(),
                            config.getNtDomain());
            httpClient.getState().setProxyCredentials(config.getProxyAuthType(),
                    config.getProxyServer(), proxyCredentials);
        }
    }
}