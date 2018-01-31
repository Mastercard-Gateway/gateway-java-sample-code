## Authenticating to the Gateway using a certficate
1. Download the test certificate from the merchant admin portal. Your merchant must be configured to use SSL certificate authentication.
2. Convert the test.crt and test.key files included in the download to PKCS12 format using the [OpenSSL tool](https://www.openssl.org/source/).

        openssl pkcs12 -export -out certificate.p12 -inkey test.key -in test.crt

3. Add the cert to your default JVM truststore.

        keytool -import -alias gateway-cert -file test.crt -keystore $JAVA_HOME/jre/lib/security/cacerts -storepass changeit

4. Pass the keystore information and the certificate hostname as VM options and environment variables.

NOTE: The following parameters are required for certificate authentication: keystore path, keystore password, merchant ID, gateway base URL, currency, and gateway certificate host URL.

#### VM options:
-Djavax.net.ssl.keyStore - Path to the PKCS12 file you created <br>
-Djavax.net.ssl.keyStorePassword - The certificate password (which is the same as your test merchant ID, for instance, TESTMERCHANT123)

#### Environment variables:
gateway.merchant.id - Your merchant ID <br>
gateway.base.url - The base URL of the gateway <br>
gateway.api.version - The gateway version you're targeting <br>
gateway.certificate.url - The URL for certificate authentication

5. Run the following:

        java -Djavax.net.ssl.keyStore="PATH_TO_PKCS12" -Djavax.net.ssl.keyStorePassword="YOUR_MERCHANT_ID" -jar
                target/gateway-java-sample-code-1.0.jar
               --gateway.merchant.id="YOUR_MERCHANT_ID"
               --gateway.currency="YOUR_CURRENCY"
               --gateway.api.password="YOUR_API_PASSWORD"
               --gateway.base.url="YOUR_GATEWAY_BASE_URL"
               --gateway.certificate.url="YOUR_GATEWAY_CERTIFICATE_URL"

6. Navigate to *http://localhost:5000* to test locally