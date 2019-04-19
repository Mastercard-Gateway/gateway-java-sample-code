# Gateway Java Sample Code
[![Build Status](https://travis-ci.org/Mastercard-Gateway/gateway-java-sample-code.svg?branch=master)](https://travis-ci.org/Mastercard-Gateway/gateway-java-sample-code)

This is a sample application to help developers start building Java applications using the Gateway Java SDK.

## Prerequisites 
1. Java 8
1. Maven
1. Registered account with MPGS Gateway system

## Authentication
1. You can authenticate in one of two ways:
- Using your API key and password (available from the merchant portal). To do this, see instructions below. The following fields are required for password authentication: merchant ID, API password, currency, and gateway base URL.
- Using a certificate, which can be downloaded from the merchant portal. To do this, see instructions [here](CERT_AUTH.md).

## Steps for running locally
1. Download code
1. Run *mvn clean install*
1. Set the environment variables
    - On Mac/Linux: Use the ```export``` command:

            prompt> export GATEWAY_MERCHANT_ID=YOUR_MERCHANT_ID
            prompt> export GATEWAY_API_PASSWORD=YOUR_API_PASSWORD
            prompt> export GATEWAY_BASE_URL=YOUR_GATEWAY_BASE_URL
            prompt> export GATEWAY_CURRENCY=YOUR_CURRENCY (optional - default is USD)
            prompt> export GATEWAY_VERSION=YOUR_VERSION (optional - default is version 45)
    - On Windows, use the ```set``` command:

            prompt> set GATEWAY_MERCHANT_ID=YOUR_MERCHANT_ID
            prompt> set GATEWAY_API_PASSWORD=YOUR_API_PASSWORD
            prompt> set GATEWAY_BASE_URL=YOUR_GATEWAY_BASE_URL
            prompt> set GATEWAY_CURRENCY=YOUR_CURRENCY (optional - default is USD)
            prompt> set GATEWAY_VERSION=YOUR_VERSION (optional - default is version 45)

1. Run the following:

        java -jar dist/gateway-java-sample-code-1.1.0.jar

1. Navigate to *http://localhost:5000* to test locally

## Proxy Configuration
If you are behind a web proxy, you'll need to add a couple of flags before running the .jar (in Step 4)  

`java -Dhttp.proxyHost=YOUR_PROXY_URL -Dhttp.proxyPort=YOUR_PROXY_PORT -jar dist/gateway-java-sample-code-1.0.jar`

## Disclaimer
This software is intended for **TEST/REFERENCE** purposes **ONLY** and is not intended to be used in a production environment.
