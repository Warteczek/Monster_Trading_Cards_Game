package at.fhtw.mtcg_app.service.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {

    @Test
    void testCreateUser() throws IOException {
        URL url = new URL("http://localhost:10001/users");
        URLConnection urlConnection = url.openConnection();
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("{\"Username\": \"User1\", \r\n \"Password\":\"12345678\"}");
        printWriter.close();
        InputStream inputStream = urlConnection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        Assertions.assertEquals(bufferedReader.readLine(), "User successfully created");
    }

    @Test
    void testUserExistsAlready() throws IOException {
        URL url = new URL("http://localhost:10001/users");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("{\"Username\": \"User1\", \r\n \"Password\":\"12345678\"}");
        printWriter.close();

        if(urlConnection.getResponseCode()== HttpURLConnection.HTTP_CONFLICT){
            System.out.println(urlConnection.getResponseCode());
            Assertions.assertFalse(false);
        }

    }

    @Test
    void testGetUserdataFromExisting() throws IOException {
        URL url = new URL("http://localhost:10001/users/User1");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Authorization", "Basic User1-mtcgToken");
        int responseCode = urlConnection.getResponseCode();

        if(responseCode== 200){
            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                System.out.println(inputLine);
            }

            bufferedReader.close();
        }
    }
    @Test
    void testGetUserdataNotFound() throws IOException {
        URL url = new URL("http://localhost:10001/users/NotFound");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Authorization", "Basic NotFound-mtcgToken");

        if(urlConnection.getResponseCode()== HttpURLConnection.HTTP_NOT_FOUND){
            System.out.println(urlConnection.getResponseCode());
            Assertions.assertFalse(false);
        }
    }

    @Test
    void testUpdateUserdata() throws IOException {
        URL url = new URL("http://localhost:10001/users/User1");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("PUT");
        urlConnection.setRequestProperty("Authorization", "Basic User1-mtcgToken");
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("{\"Name\": \"Horst\", \r\n \"Image\":\":'(\"}");
        printWriter.close();

        InputStream inputStream = urlConnection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        Assertions.assertEquals(bufferedReader.readLine(), "User successfully updated");

        bufferedReader.close();
    }

}
