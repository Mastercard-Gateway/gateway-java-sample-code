## Authenticating to the Gateway using a certficate
1. Download the test certificate from the merchant admin portal. Your merchant must be configured to use SSL certificate authentication.
2. Convert the test.crt and test.key files included in the download to PKCS12 format using the [OpenSSL tool](https://www.openssl.org/source/).

        openssl pkcs12 -export -out certificate.p12 -inkey test.key -in test.crt

3. Add the cert to your default JVM truststore.

        keytool -import -alias gateway-cert -file test.crt -keystore $JAVA_HOME/jre/lib/security/cacerts -storepass changeit

4. Pass the keystore information and the certificate hostname as VM options and environment variables.

NOTE: The following parameters are required for certificate authentication: keystore path, keystore password, merchant ID, gateway base URL, currency, and gateway certificate host URL.

#### Environment variables:
1. Set the environment variables
    - On Mac/Linux: Use the ```export``` command:

            prompt> export GATEWAY_MERCHANT_ID=YOUR_MERCHANT_ID
            prompt> export GATEWAY_BASE_URL=YOUR_GATEWAY_BASE_URL
            prompt> export GATEWAY_CERT_HOST_URL=YOUR_GATEWAY_CERT_HOST_URL
            prompt> export GATEWAY_KEYSTORE_PASSWORD=YOUR_KEYSTORE_PASSWORD (the keystore password is the same as the test merchant ID, e.g. TESTMERCHANTID)
            prompt> export GATEWAY_KEYSTORE_PATH=PATH_TO_PKCS12
            prompt> export GATEWAY_CURRENCY=YOUR_CURRENCY (optional - default is USD)
            prompt> export GATEWAY_VERSION=YOUR_VERSION (optional - default is version 45)
    - On Windows, use the ```set``` command:

            prompt> set GATEWAY_MERCHANT_ID=YOUR_MERCHANT_ID
            prompt> set GATEWAY_BASE_URL=YOUR_GATEWAY_BASE_URL
            prompt> set GATEWAY_CERT_HOST_URL=YOUR_GATEWAY_CERT_HOST_URL
            prompt> set GATEWAY_KEYSTORE_PASSWORD=YOUR_KEYSTORE_PASSWORD (the keystore password is the same as the test merchant ID, e.g. TESTMERCHANTID)
            prompt> set GATEWAY_KEYSTORE_PATH=PATH_TO_PKCS12
            prompt> set GATEWAY_CURRENCY=YOUR_CURRENCY (optional - default is USD)
            prompt> set GATEWAY_VERSION=YOUR_VERSION (optional - default is version 45)

5. Run the following:

        java -jar target/gateway-java-sample-code-1.0.jar

6. Navigate to *http://localhost:5000* to test locally