package com.gateway.client;

import com.gateway.app.Config;
import com.google.gson.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

public class ApiService {

    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);


    /**
     * Execute HTTP method for the HTTP client and Host configuration
     * Configure for either API password or certificate authentication
     *
     * @param httpMethod PUT, POST, or GET
     * @return body from API response
     * @throws Exception
     */
    public static String executeHTTPMethod(HttpRequestBase httpMethod, Config config, ApiProtocol protocol) throws Exception {
        String body = "";
        try {
            // Set the proper authentication type - username/password or certificate authentication
            if(config.getAuthenticationType().equals(Config.AuthenticationType.PASSWORD)) {
                CloseableHttpClient httpClient = HttpClients.createDefault();
                HttpClientContext httpClientContext = HttpClientContext.create();
                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

                // Load credentials
                credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(config.getApiUsername(), config.getApiPassword()));
                httpClientContext.setCredentialsProvider(credentialsProvider);

                // Execute the request
                HttpResponse response = httpClient.execute(httpMethod, httpClientContext);
                HttpEntity entity = response.getEntity();
                body = EntityUtils.toString(entity);
            }
            else if(config.getAuthenticationType().equals(Config.AuthenticationType.CERTIFICATE)) {
                KeyStore keyStore = KeyStore.getInstance("pkcs12");

                // Read keystore
                InputStream keyStoreInput = new FileInputStream(config.getKeyStore());
                keyStore.load(keyStoreInput, config.getKeyStorePassword().toCharArray());

                // Create SSL context
                SSLContext sslContext = SSLContexts.custom()
                        .loadKeyMaterial(keyStore, config.getKeyStorePassword().toCharArray())
                        .build();

                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
                CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

                //Execute request
                HttpResponse response = httpClient.execute(httpMethod);
                HttpEntity entity = response.getEntity();
                body = EntityUtils.toString(entity);
            }
            if(protocol.equals(ApiProtocol.REST)) {
                checkForRESTErrorResponse(body);
            }
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
     * Checks if the API response contains an error (handles JSON response from REST call)
     *
     * @param response from the API call
     * @throws ApiException
     */
    private static void checkForRESTErrorResponse(String response) throws ApiException {

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
