# RestClient
Simple REST client

#### Example:

Initialized REST client:
```java
public class RestClientBuilder {
    @Test
    public void testGet() {
        RestBuilder<RestClient> restBuilder = new RestBuilder(RestClient.class);
        TestRestClient testRestClient = restBuilder.build();
        String result = testRestClient.getWeather("94040", "b1b15e88fa797225412429c1c50c122a1");
        System.out.println(result);
    }
}
```

REST Client:
```java
public interface RestClient {
    @Get(path = "http://samples.openweathermap.org/data/2.5/weather?zip={zip},us&appid={appid}")
    String getWeather(String zip, String appid);
}
```
