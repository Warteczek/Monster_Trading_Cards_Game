package at.fhtw.mtcg_app.service.cards;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class CardsTest {
    @Test
    void testShowCards() throws IOException {
        URL url = new URL("http://localhost:10001/cards");
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
    void testConfigureDeck() throws IOException {
        URL url = new URL("http://localhost:10001/deck");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("PUT");
        urlConnection.setRequestProperty("Authorization", "Basic User1-mtcgToken");
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("[\"a1618f1e-4f4c-4e09-9647-87e16f1edd2d\", \"ce6bcaee-47e1-4011-a49e-5a4d7d4245f3\", \"74635fae-8ad3-4295-9139-320ab89c2844\", \"70962948-2bf7-44a9-9ded-8c68eeac7793\"]");
        printWriter.close();

        InputStream inputStream = urlConnection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        Assertions.assertEquals(bufferedReader.readLine(), "The deck has been successfully configured");

        bufferedReader.close();
    }

    @Test
    void testConfigureDeckButCardDoesNotFromUser() throws IOException {
        URL url = new URL("http://localhost:10001/deck");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("PUT");
        urlConnection.setRequestProperty("Authorization", "Basic User1-mtcgToken");
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("[\"notFromU-serC-ard9-9647-87e16f1edd2d\", \"ce6bcaee-47e1-4011-a49e-5a4d7d4245f3\", \"74635fae-8ad3-4295-9139-320ab89c2844\", \"70962948-2bf7-44a9-9ded-8c68eeac7793\"]");
        printWriter.close();

        if(urlConnection.getResponseCode()==403){
            Assertions.assertFalse(false);
        }else{
            Assertions.assertFalse(true);
        }
    }

    @Test
    void testShowDeckFromUserPlain() throws IOException {
        URL url = new URL("http://localhost:10001/deck?format=plain");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Authorization", "Basic User1-mtcgToken");

        int responseCode= urlConnection.getResponseCode();

        if(responseCode== 200){
            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                System.out.println(inputLine);
            }

            bufferedReader.close();
        }else{
            System.out.println(responseCode);
            Assertions.assertFalse(true);
        }
    }
    @Test
    void testShowDeckFromUserJSON() throws IOException {
        URL url = new URL("http://localhost:10001/deck");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Authorization", "Basic User1-mtcgToken");

        int responseCode=urlConnection.getResponseCode();

        if(responseCode== 200){
            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                System.out.println(inputLine);
            }

            bufferedReader.close();
        }else{
            System.out.println(responseCode);
            Assertions.assertFalse(true);
        }
    }

    @Test
    void testUserWithoutDeck() throws IOException {
        URL url = new URL("http://localhost:10001/deck");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Authorization", "Basic kienboec-mtcgToken");

        int responseCode=urlConnection.getResponseCode();

        if(responseCode== 204){
            System.out.println(responseCode);
            Assertions.assertFalse(false);
        }else{
            System.out.println(responseCode);
            Assertions.assertFalse(true);
        }
    }
}
