package at.fhtw.mtcg_app.service.login;

import at.fhtw.mtcg_app.model.Weather;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginTest {
    @Test
    void testLogin() throws Exception {
        URL url = new URL("http://localhost:10001/weather/1");
        URLConnection urlConnection = url.openConnection();
        InputStream inputStream = urlConnection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        try {
            Weather weather = new ObjectMapper().readValue(bufferedReader.readLine(), Weather.class);
            assertEquals(1, weather.getId());
            assertEquals("Vienna", weather.getCity());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        bufferedReader.close();
    }
}
