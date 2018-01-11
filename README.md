# Gateway Java Sample Code [![Build Status](https://travis-ci.org/simplifycom/gateway-java-sample-code.svg?branch=master)](https://travis-ci.org/simplifycom/gateway-java-sample-code)
This is a sample application to help developers start building Java applications using the Gateway Java SDK.

## Prerequisites 
1. Java 8
1. Maven
1. Registered account with MPGS Gateway system

## Steps for running in Heroku
1. Register with [Heroku](https://www.heroku.com) (if you don't have an account already). A free account will be enough. 
1. Click here to -> [![Deploy](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy)
1. Fill in the details such as Merchant Id, API Password & Gateway Base URL

## Authentication
1. You can authenticate in one of two ways:
- Using your private API key (available from the merchant portal). To use this method, set the environment variable gateway.api.password to your private key.
- Using a certificate, which can be downloaded from the merchant portal. You then need to take the following steps:
    * Convert the PEM file to a PKCS12 file. You can do this using the [OpenSSL tool](https://www.openssl.org/source/) and the following command: *openssl pkcs12 -export -out server.p12 -inkey test.key -in test.crt*. The password for the test certificate is the identifier of the merchant's test profile. For example, if the test merchant profile ID is TESTTNSDEMO01 then the password for the certificate is TESTTNSDEMO01.
    * Import the PKCS12 file into the Java keystore:
        - Run *keytool -importkeystore -deststorepass TESTAB2894354 -destkeystore test.jks -srckeystore server.p12 -srcstoretype PKCS12*
        - Pass the following as environment variables: *-Djavax.net.ssl.trustStore=test.ks -Djavax.net.ssl.trustStorePassword=PASSWORD*


## Steps for running locally
1. Download code
1. Run *mvn clean install*
1. Run *java -jar target/gateway-java-sample-code-1.0.jar
    --gateway.merchant.id="YOUR_MERCHANT_ID"
    --gateway.api.password="YOUR_API_PASSWORD"
    --gateway.base.url="YOUR_GATEWAY_BASE_URL"*
1. Navigate to *http://localhost:5000* to test locally

## Disclaimer
This software is intended for **TEST/REFERENCE** purposes **ONLY** and is not intended to be used in a production environment.

PLEASE NOTE: **Heroku** is not affiliated with, maintained by or endorsed by Mastercard. The use of this code is at your own risk.

 
