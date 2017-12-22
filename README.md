# Gateway Java Sample Code [![Build Status](https://travis-ci.org/simplifycom/gateway-java-sample-code.svg?branch=master)](https://travis-ci.org/simplifycom/gateway-java-sample-code)
This is a sample application to help developers start building Java applications using the Gateway Java SDK.

## Prerequisites 
1. Java 8
1. Maven
1. Test account with MPGS Gateway system

## Steps for testing with Heroku
1. Obtain an account with your Gateway provider
1. Register with [Heroku](https://www.heroku.com)
1. Update the .env file at the root level of the project with your merchant ID, API password, and gateway URL.
1. Deploy to Heroku using the following steps:

## Steps for running locally
1. Download code
1. Run "mvn clean install"
1. Run "java -jar target/gateway-java-sample-code-1.0.jar \\<br/>
    --gateway.merchant.id="YOUR_MERCHANT_ID" \\<br/>
    --gateway.api.password="YOUR_API_PASSWORD" \\<br/>
    --gateway.base.url="YOUR_GATEWAY_BASE_URL""
1. Navigate to http://localhost:5000 to test locally

## Disclaimer
This software is intended for **TEST/REFERENCE** purposes **ONLY** and is not intended to be used in a production environment.

PLEASE NOTE: **Heroku** is not affiliated with, maintained by or endorsed by Mastercard. The use of this code is at your own risk.

 
