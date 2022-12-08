package at.fhtw.mtcg_app.service.login;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginTest {
    @Test
    void testLogin() throws Exception {
        URL url = new URL("http://localhost:10001/session");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoOutput(true);


        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("{\"Username\": \"User1\", \r\n \"Password\":\"12345678\"}");
        printWriter.close();

        // TODO Test fixen

        InputStream inputStream = urlConnection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        Assertions.assertEquals(bufferedReader.readLine(), "User login successful");

        bufferedReader.close();
    }
}
