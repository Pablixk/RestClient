package gaguh.restclient;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.GzipCompressingEntity;
import org.apache.http.entity.*;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by User on 29.06.2017.
 */
public class EntityFactory {

    public static final Charset UTF_8 = Charset.forName("UTF-8");

    private Object entity;

    public EntityFactory(Object object) {
        this.entity = object;
    }

    public HttpEntity getHttpEntity() throws UnsupportedEncodingException {
        if (entity instanceof String) {
            return new StringEntity((String) entity, UTF_8);
        } else if (entity instanceof InputStream) {
            return new InputStreamEntity((InputStream) entity);
        } else if (entity instanceof File) {
            return new FileEntity((File) entity);
        } else if (entity instanceof byte[]) {
            return new ByteArrayEntity((byte[]) entity);
        } else {
            return new StringEntity(new Gson().toJson(entity), UTF_8);
        }
    }
}
