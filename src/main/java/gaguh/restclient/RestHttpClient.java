package gaguh.restclient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * Created by User on 28.06.2017.
 */
public class RestHttpClient {

    public static final Charset UTF_8 = Charset.forName("UTF-8");

    private static final Logger logger = LoggerFactory.getLogger(RestHttpClient.class);

    private HttpClient httpClient;

    private RestHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public static RestHttpClient getRestHttpClient() {
         return new RestHttpClient(HttpClientBuilder.create().build());
    }

    public static RestHttpClient getRestHttpClient(String pathToKeystore, String storePassword) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
        KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream instream = new FileInputStream( pathToKeystore)) {
            trustStore.load(instream, storePassword.toCharArray());
            File keyStoreFile = new File(pathToKeystore);
            SSLContext sslcontext = SSLContexts.custom()
                    .loadTrustMaterial(keyStoreFile, storePassword.toCharArray()) /* this key store must contain the certs needed & trusted to verify the servers cert */
                    .loadKeyMaterial(trustStore, storePassword.toCharArray()) /* this keystore must contain the key/cert of the client */
                    .build();

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);
            return new RestHttpClient(HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .build());
        }
    }

    public InputStream get(String url) throws IOException {
        logger.debug("Create GET request on: {}", url);
        HttpGet getRequest = new HttpGet(url);
        //getRequest.addHeader("Content-Type", "application/json");
        logger.debug("Send GET request: {}", getRequest);
        HttpResponse response = httpClient.execute(getRequest);
        logger.debug("Response GET: {}", response);
        // Check for HTTP response code: 200 = success
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
        }

        return response.getEntity().getContent();
    }

    public InputStream post(String url, Object... objects) throws IOException {
        logger.debug("Create POST request on: {}", url);
        HttpPost postRequest = new HttpPost(url);
        //postRequest.addHeader("Content-Type", "application/json");

        for (Object obj: objects) {
            EntityFactory entityFactory = new EntityFactory(obj);
            HttpEntity httpEntity = entityFactory.getHttpEntity();
            postRequest.setEntity(httpEntity);
        }

        logger.debug("Send POST request: {}", postRequest);
        HttpResponse response = httpClient.execute(postRequest);

        if (response.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
        }
        logger.debug("Response POST: {}", response);
        return response.getEntity().getContent();
    }
}
