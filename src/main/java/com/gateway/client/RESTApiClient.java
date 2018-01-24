package com.gateway.client;

import com.gateway.app.Config;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.ssl.SSLContexts;

import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Enumeration;

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

            return executeHTTPMethod(httpPut, config);
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

        return executeHTTPMethod(httpPost, config);
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

        return executeHTTPMethod(httpPost, config);
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

        return executeHTTPMethod(httpGet, config);
    }

    /**
     * Execute HTTP method for the HTTP client and Host configuration
     *
     * @param httpMethod PUT, POST, or GET
     * @return body from API response
     * @throws Exception
     */
    private String executeHTTPMethod(HttpRequestBase httpMethod, Config config) throws Exception {
        String body = "";
        try {
            // Set the proper authentication type, username/password or certificate authentication
            if(config.getAuthenticationType().equals(Config.AuthenticationType.PASSWORD)) {
                CloseableHttpClient httpClient = HttpClients.createDefault();
                HttpClientContext httpClientContext = HttpClientContext.create();
                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

                // Load credentials
                credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(config.getApiUsername(), config.getApiPassword()));
                httpClientContext.setCredentialsProvider(credentialsProvider);

                // Execute the request
                HttpResponse response = httpClient.execute(httpMethod, httpClientContext);
                body = new BasicResponseHandler().handleResponse(response);
            }
            else if(config.getAuthenticationType().equals(Config.AuthenticationType.CERTIFICATE)) {
                KeyStore keyStore = KeyStore.getInstance("pkcs12");

                // Read keystore
                InputStream keyStoreInput = new FileInputStream(config.getKeyStore());
                keyStore.load(keyStoreInput, config.getKeyStorePassword().toCharArray());

                Enumeration enumeration = keyStore.aliases();
                while(enumeration.hasMoreElements()) {
                    String alias = (String)enumeration.nextElement();
                    System.out.println("alias name: " + alias);
                    Certificate certificate = keyStore.getCertificate(alias);
                    System.out.println(certificate.toString());

                }

                System.out.println("Key store has " + keyStore.size() + " keys");

                // Create SSL context
                SSLContext sslContext = SSLContexts.custom()
                        .loadKeyMaterial(keyStore, config.getKeyStorePassword().toCharArray())
                        .build();

                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
                CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

                //Execute request
                HttpResponse response = httpClient.execute(httpMethod);
                body = new BasicResponseHandler().handleResponse(response);
            }
            checkForErrorResponse(body);
        }
        catch (ApiException apiException) {
            logger.error("The API returned an error", apiException);
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
     * @throws ApiException
     */
    private void checkForErrorResponse(String response) throws ApiException {

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

    /**
     * Read from keystore
     *
     * @param config contains frequently used information like Merchant ID, API password, etc.
     * @return keystore
     * @throws Exception
     */
    private KeyStore readStore(Config config) throws Exception {
        try (InputStream keyStoreStream = this.getClass().getResourceAsStream(config.getKeyStore())) {
            KeyStore keyStore = KeyStore.getInstance("JKS"); // or "PKCS12"
            keyStore.load(keyStoreStream, config.getKeyStorePassword().toCharArray());
            return keyStore;
        }
    }
}