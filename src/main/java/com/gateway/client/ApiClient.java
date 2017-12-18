package com.gateway.client;

import com.gateway.app.Config;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import java.io.IOException;

public final class ApiClient {


    private static final String UTF8_ENCODING = "UTF-8";
    private static final String CONTENT_TYPE = "application/json";

    /**
     * Performs a PUT operation (required for the following API operations: AUTHORIZE, CAPTURE, PAY, REFUND, UPDATE_AUTHORIZATION, VERIFY, VOID, CHECK_3DS_ENROLLMENT, INITIATE_BROWSER_PAYMENT)
     * @param data  JSON payload
     * @param requestUrl  API endpoint
     * @param config  Common config object that contains frequently used information like Merchant ID, API password, etc.
     * @return body  Returns a String response
     * @throws Exception
     */
    public String sendTransaction(String data, String requestUrl, Config config) throws Exception {
        HttpClient httpClient = new HttpClient();

        // Set the API Username and Password in the header authentication field.
        httpClient.getState().setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(config.getApiUsername(), config.getApiPassword()));

        PutMethod putMethod = new PutMethod(requestUrl);

        putMethod.setDoAuthentication(true);

        // Set the charset to UTF-8
        StringRequestEntity entity = new StringRequestEntity(data, CONTENT_TYPE, UTF8_ENCODING);
        putMethod.setRequestEntity(entity);

        HostConfiguration hostConfig = new HostConfiguration();
        hostConfig.setHost(config.getGatewayHost());
        configureProxy(httpClient, config);
        String body = null;

        try {
            // send the transaction
            httpClient.executeMethod(hostConfig, putMethod);
            body = putMethod.getResponseBodyAsString();
        } catch (IOException ioe) {
            // we can replace a specific exception that suits your application
            throw new Exception(ioe);
        } finally {
            putMethod.releaseConnection();
        }

        return body;
    }

    /**
     * Performs a POST operation (required for the following API operations: PROCESS_ACS_RESULT, CREATE_CHECKOUT_SESSION)
     * @param data  JSON payload
     * @param requestUrl  API endpoint
     * @param config  Common config object that contains frequently used information like Merchant ID, API password, etc.
     * @return body  Returns a String response
     * @throws Exception
     */
    public String postTransaction(String data, String requestUrl, Config config) throws Exception {
        HttpClient httpClient = new HttpClient();

        // Set the API Username and Password in the header authentication field.
        httpClient.getState().setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(config.getApiUsername(), config.getApiPassword()));

        PostMethod postMethod = new PostMethod(requestUrl);

        postMethod.setDoAuthentication(true);

        // Set the charset to UTF-8
        StringRequestEntity entity = new StringRequestEntity(data, "application/json", UTF8_ENCODING);
        postMethod.setRequestEntity(entity);

        HostConfiguration hostConfig = new HostConfiguration();
        hostConfig.setHost(config.getGatewayHost());
        configureProxy(httpClient, config);
        String body = null;

        try {
            // send the transaction
            httpClient.executeMethod(hostConfig, postMethod);
            body = postMethod.getResponseBodyAsString();
        } catch (IOException ioe) {
            // we can replace a specific exception that suits your application
            throw new Exception(ioe);
        } finally {
            postMethod.releaseConnection();
        }

        return body;
    }

    /**
     * Performs a GET operation (required for the following API operations: Retrieve session, Retrieve transaction, Retrieve order)
     * @param requestUrl  API endpoint
     * @param config  Common config object that contains frequently used information like Merchant ID, API password, etc.
     * @return body  Returns a String response
     * @throws Exception
     */
    public String getTransaction(String requestUrl, Config config) throws Exception {
        HttpClient httpClient = new HttpClient();

        // Set the API Username and Password in the header authentication field.
        httpClient.getState().setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(config.getApiUsername(), config.getApiPassword()));

        GetMethod getMethod = new GetMethod(requestUrl);

        getMethod.setDoAuthentication(true);

        HostConfiguration hostConfig = new HostConfiguration();
        hostConfig.setHost(config.getGatewayHost());
        configureProxy(httpClient, config);
        String body = null;

        try {
            // send the transaction
            httpClient.executeMethod(hostConfig, getMethod);
            body = getMethod.getResponseBodyAsString();
        } catch (IOException ioe) {
            // we can replace a specific exception that suits your application
            throw new Exception(ioe);
        } finally {
            getMethod.releaseConnection();
        }

        return body;
    }

    /**
     * configureProxy
     * <p/>
     * Check if proxy config is defined; if so configure the host and http client to tunnel through
     *
     * @param httpClient
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