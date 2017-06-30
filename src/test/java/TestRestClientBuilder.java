import client.TestRestClient;
import gaguh.RestBuilder;
import org.junit.Test;

/**
 * Created by User on 29.06.2017.
 */
public class TestRestClientBuilder {

    @Test
    public void testGet() {
        RestBuilder<TestRestClient> restBuilder = new RestBuilder(TestRestClient.class);
        TestRestClient testRestClient = restBuilder.build();
        String result = testRestClient.getWeather("94040", "b1b15e88fa797225412429c1c50c122a1");
        System.out.println(result);
    }
}
