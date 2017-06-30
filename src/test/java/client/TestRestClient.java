package client;

import gaguh.annotations.Get;

/**
 * Created by User on 29.06.2017.
 */
public interface TestRestClient {

    @Get(path = "http://samples.openweathermap.org/data/2.5/weather?zip={zip},us&appid={appid}")
    String getWeather(String zip, String appid);
}
