package at.fhtw.mtcg_app.service.game;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GameTest {
    @Test
    void testGetStats() throws IOException {
        URL url = new URL("http://localhost:10001/stats");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Authorization", "Basic User1-mtcgToken");
        urlConnection.setDoOutput(true);
        int responseCode= urlConnection.getResponseCode();
        System.out.println(responseCode);

        if(responseCode== HttpURLConnection.HTTP_OK){
            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                System.out.println(inputLine);
            }

            bufferedReader.close();
            Assertions.assertFalse(false);
        }else{
            Assertions.assertFalse(true);
        }
    }

    @Test
    void testGetScoreboard() throws IOException {
        URL url = new URL("http://localhost:10001/scoreboard");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Authorization", "Basic User1-mtcgToken");
        urlConnection.setDoOutput(true);
        int responseCode= urlConnection.getResponseCode();
        System.out.println(responseCode);

        if(responseCode== HttpURLConnection.HTTP_OK){
            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                System.out.println(inputLine);
            }

            bufferedReader.close();
            Assertions.assertFalse(false);
        }else{
            Assertions.assertFalse(true);
        }
    }

    @Test
    void testGameFirstPlayer() throws IOException {
        URL url = new URL("http://localhost:10001/battles");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Authorization", "Basic User1-mtcgToken");
        urlConnection.setDoOutput(true);
        int responseCode= urlConnection.getResponseCode();
        System.out.println(responseCode);

        if(responseCode== HttpURLConnection.HTTP_OK){
            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                System.out.println(inputLine);
            }

            bufferedReader.close();
            Assertions.assertFalse(false);
        }else{
            Assertions.assertFalse(true);
        }
    }

    @Test
    void testGameSecondPlayer() throws IOException {
        URL url = new URL("http://localhost:10001/battles");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Authorization", "Basic kienboec-mtcgToken");
        urlConnection.setDoOutput(true);
        int responseCode= urlConnection.getResponseCode();
        System.out.println(responseCode);

        if(responseCode== HttpURLConnection.HTTP_OK){
            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                System.out.println(inputLine);
            }

            bufferedReader.close();
            Assertions.assertFalse(false);
        }else{
            Assertions.assertFalse(true);
        }
    }

}
