package com.mastercard.gateway;

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

public final class Connection {

    private Merchant merchant;

    Connection(Merchant merchant) {
        this.merchant = merchant;
    }

    String sendTransaction(String data) throws Exception {
        HttpClient httpClient = new HttpClient();

        // Set the API Username and Password in the header authentication field.
        httpClient.getState().setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(merchant.getApiUsername(), merchant.getPassword()));

        PutMethod putMethod = new PutMethod(merchant.getGatewayUrl());

        putMethod.setDoAuthentication(true);

        // Set the charset to UTF-8
        StringRequestEntity entity = new StringRequestEntity(data, "application/json", "UTF-8");
        putMethod.setRequestEntity(entity);

        HostConfiguration hostConfig = new HostConfiguration();
        hostConfig.setHost(merchant.getGatewayHost());
        configureProxy(httpClient);
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

    String postTransaction(String data) throws Exception {
        HttpClient httpClient = new HttpClient();

        // Set the API Username and Password in the header authentication field.
        httpClient.getState().setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(merchant.getApiUsername(), merchant.getPassword()));

        PostMethod postMethod = new PostMethod(merchant.getGatewayUrl());

        postMethod.setDoAuthentication(true);

        // Set the charset to UTF-8
        StringRequestEntity entity = new StringRequestEntity(data, "application/json", "UTF-8");
        postMethod.setRequestEntity(entity);

        HostConfiguration hostConfig = new HostConfiguration();
        hostConfig.setHost(merchant.getGatewayHost());
        configureProxy(httpClient);
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

    String getTransaction() throws Exception {
        HttpClient httpClient = new HttpClient();

        // Set the API Username and Password in the header authentication field.
        httpClient.getState().setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(merchant.getApiUsername(), merchant.getPassword()));

        GetMethod getMethod = new GetMethod(merchant.getGatewayUrl());

        getMethod.setDoAuthentication(true);

        HostConfiguration hostConfig = new HostConfiguration();
        hostConfig.setHost(merchant.getGatewayHost());
        configureProxy(httpClient);
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
     *
     * Check if proxy config is defined; if so configure the host and http client to tunnel through
     *
     * @param httpClient
     * @return void
     */
    private void configureProxy(HttpClient httpClient) {
        // If proxy server is defined, set the host configuration.
        if (merchant.getProxyServer() != null) {
            HostConfiguration hostConfig = httpClient.getHostConfiguration();
            hostConfig.setHost(merchant.getGatewayHost());
            hostConfig.setProxy(merchant.getProxyServer(), merchant.getProxyPort());

        }
        // If proxy authentication is defined, set proxy credentials
        if (merchant.getProxyUsername() != null) {
            NTCredentials proxyCredentials =
                    new NTCredentials(merchant.getProxyUsername(),
                            merchant.getProxyPassword(), merchant.getGatewayHost(),
                            merchant.getNtDomain());
            httpClient.getState().setProxyCredentials(merchant.getProxyAuthType(),
                    merchant.getProxyServer(), proxyCredentials);
        }
    }
}