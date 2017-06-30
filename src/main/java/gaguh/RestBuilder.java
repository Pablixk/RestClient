package gaguh;

import gaguh.annotations.Get;
import gaguh.annotations.Post;
import com.google.gson.Gson;
import gaguh.annotations.SSL;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gaguh.restclient.RestHttpClient;

import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by User on 29.06.2017.
 */
public class RestBuilder<T> {

    private Logger logger = LoggerFactory.getLogger(RestBuilder.class);

    private Class restClientType;
    private RestHttpClient restHttpClient;

    public RestBuilder(Class restClientType) {
        this.restClientType = restClientType;
        this.restHttpClient = RestHttpClient.getRestHttpClient();
    }

    public T build() {
        return (T)Proxy.newProxyInstance(restClientType.getClassLoader(),
                new Class[]{restClientType}, new ProxyListener(restHttpClient));
    }

    private class ProxyListener implements InvocationHandler {

        private RestHttpClient restHttpClient;

        public ProxyListener(RestHttpClient restHttpClient) {
            this.restHttpClient = restHttpClient;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // If SSL auth
            if (method.isAnnotationPresent(SSL.class)) {
                SSL ssl = method.getAnnotation(SSL.class);
                String pathToKeyStore = ssl.pathToKeyStore();
                String keyStorePassword = ssl.keyStorePassword();
                restHttpClient = RestHttpClient.getRestHttpClient(pathToKeyStore, keyStorePassword);
            }

            if (method.isAnnotationPresent(Get.class)) {
                Get get = method.getAnnotation(Get.class);

                String path = get.path();
                for (Object arg : args) {
                    path = path.replaceFirst("\\{\\w*\\}", (String) arg);
                }

                if (method.getReturnType().equals(String.class)) {
                    return IOUtils.toString(restHttpClient.get(path), RestHttpClient.UTF_8);
                }
                if (method.getReturnType().equals(InputStream.class)) {
                    return restHttpClient.get(path);
                } else {
                    String response = IOUtils.toString(restHttpClient.get(path), RestHttpClient.UTF_8);
                    if (null != response) {
                        return new Gson().fromJson(response, method.getReturnType());
                    }
                }

            } else if (method.isAnnotationPresent(Post.class)) {
                Post post = method.getAnnotation(Post.class);
                String path = post.path();

                if (method.getReturnType().equals(String.class)) {
                    return IOUtils.toString(restHttpClient.post(path, args), RestHttpClient.UTF_8);
                }
                if (method.getReturnType().equals(InputStream.class)) {
                    return restHttpClient.post(path, args);
                } else {
                    String response = IOUtils.toString(restHttpClient.post(path, args), RestHttpClient.UTF_8);
                    if (null != response) {
                        return new Gson().fromJson(response, method.getReturnType());
                    }
                }
            }
            return null;
        }
    }
}
