## Running localhost in SSL mode

In order to properly test the APM flow end-to-end, we need to run localhost in SSL mode.

Follow the directions[here](README.md)to create a new keystore and configure it in application.properties as below:

```
server.ssl.key-store: keystore.p12
server.ssl.key-store-password: password
server.ssl.keyStoreType: PKCS12
server.ssl.keyAlias: tomcat
```

After restarting, you should be able to access https://localhost:5000/apm.