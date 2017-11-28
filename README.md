# Gateway Java Sample Code [![Build Status](https://travis-ci.org/simplifycom/gateway-java-sample-code.svg?branch=master)](https://travis-ci.org/simplifycom/gateway-java-sample-code)
This is a sample application to help developers start building Java applications using the Gateway Java SDK.

## Prerequisites 
1. Java 8
1. Maven
1. Test account with MPGS Gateway system

## Steps for testing with Heroku
1. Obtain an account with your Gateway provider
1. Register with [Heroku](https://www.heroku.com)
1. Click this button [![Deploy](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy)
1. Configure the app with your TEST merchant ID, API password, and the regional API endpoint.
1. Visit the landing page of the newly deployed app for more details

## Steps for running locally
1. Download code
1. Run "mvn clean spring-boot:run"
1. Navigate to http://localhost:5000 to test locally

## Disclaimer
All service calls responsible for handling payment information should use best-in-class security practices. This software is intended for **TEST** / **DEVELOPMENT** purposes **ONLY** and is not intended to be used in a production environment. 
