package com.gateway.client;

import com.gateway.app.Config;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import java.io.IOException;

/**
 * Service client class for making API requests using REST protocol using JSON
 */

public final class RESTApiClient {


    private static final String UTF8_ENCODING = "UTF-8";
    private static final String CONTENT_TYPE = "application/json";

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
        return executeHTTPMethod(httpClient, putMethod, hostConfig);
    }

    /**
     * Performs a POST operation (required for the following API operations: PROCESS_ACS_RESULT, CREATE_CHECKOUT_SESSION)
     *
     * @param data   JSON payload
     * @param config contains frequently used information like Merchant ID, API password, etc.
     * @return body
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
        return executeHTTPMethod(httpClient, postMethod, hostConfig);
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
        HttpClient httpClient = new HttpClient();

        // Set the API Username and Password in the header authentication field.
        httpClient.getState().setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(config.getApiUsername(), config.getApiPassword()));

        PostMethod postMethod = new PostMethod(requestUrl);

        postMethod.setDoAuthentication(true);

        HostConfiguration hostConfig = new HostConfiguration();
        hostConfig.setHost(config.getGatewayHost());
        return executeHTTPMethod(httpClient, postMethod, hostConfig);
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
        HttpClient httpClient = new HttpClient();

        // Set the API Username and Password in the header authentication field.
        httpClient.getState().setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(config.getApiUsername(), config.getApiPassword()));

        GetMethod getMethod = new GetMethod(requestUrl);

        getMethod.setDoAuthentication(true);

        HostConfiguration hostConfig = new HostConfiguration();
        hostConfig.setHost(config.getGatewayHost());
        return executeHTTPMethod(httpClient, getMethod, hostConfig);
    }

    /**
     * Execute HTTP method for the HTTP client and Host configuration
     *
     * @param httpClient
     * @param httpMethod
     * @param hostConfig
     * @return
     * @throws Exception
     */
    private String executeHTTPMethod(HttpClient httpClient, HttpMethod httpMethod, HostConfiguration hostConfig) throws Exception {
        String body;
        try {
            // send the transaction
            httpClient.executeMethod(hostConfig, httpMethod);
            body = httpMethod.getResponseBodyAsString();
            checkForErrorResponse(body);
        }
        catch (ApiException apiException) {
            throw apiException;
        }
        catch (IOException ioe) {
            // we can replace a specific exception that suits your application
            throw new Exception(ioe);
        } finally {
            httpMethod.releaseConnection();
        }
        return body;
    }

    /**
     * Checks if the API response contains an error
     *
     * @param response from the API call
     * @return either throw an exception or return null
     */
    private static void checkForErrorResponse(String response) throws ApiException {

        JsonObject json = new Gson().fromJson(response, JsonObject.class);

        if (json.has("error")) {
            JsonObject errorJson = json.get("error").getAsJsonObject();
            ApiException apiException = new ApiException("The API returned an error");
            if(errorJson.has("cause")) apiException.setErrorCode(errorJson.get("cause").getAsString());
            if(errorJson.has("explanation")) apiException.setExplanation(errorJson.get("explanation").getAsString());
            if(errorJson.has("field")) apiException.setField(errorJson.get("field").getAsString());
            if(errorJson.has("validationType")) apiException.setValidationType(errorJson.get("validationType").getAsString());
            throw apiException;
        }
    }
}