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
- Using your API key and password (available from the merchant portal). To do this, see instructions below. The following fields are required for password authentication: merchant ID, API password, currency, and gateway base URL.
- Using a certificate, which can be downloaded from the merchant portal. To do this, see instructions [here](CERT_AUTH.md). Certificate authentication is not supported on Heroku.

## Steps for running locally
1. Download code
1. Run *mvn clean install*
1. Set the environment variables
- On Mac/Linux: Use the ```export``` command:

        prompt> export GATEWAY_MERCHANT_ID=YOUR_MERCHANT_ID
        prompt> export GATEWAY_API_PASSWORD=YOUR_API_PASSWORD
        prompt> export GATEWAY_BASE_URL=YOUR_GATEWAY_BASE_URL
        prompt> export GATEWAY_CURRENCY=YOUR_CURRENCY
        prompt> export GATEWAY_VERSION=YOUR_VERSION (optional)
- On Windows, use the ```set``` command:

        prompt> set GATEWAY_MERCHANT_ID=YOUR_MERCHANT_ID
        prompt> set GATEWAY_API_PASSWORD=YOUR_API_PASSWORD
        prompt> set GATEWAY_BASE_URL=YOUR_GATEWAY_BASE_URL
        prompt> set GATEWAY_CURRENCY=YOUR_CURRENCY
        prompt> set GATEWAY_VERSION=YOUR_VERSION (optional)

1. Run the following:

        java -jar target/gateway-java-sample-code-1.0.jar

1. Navigate to *http://localhost:5000* to test locally

## Disclaimer
This software is intended for **TEST/REFERENCE** purposes **ONLY** and is not intended to be used in a production environment.

PLEASE NOTE: **Heroku** is not affiliated with, maintained by or endorsed by Mastercard. The use of this code is at your own risk.
