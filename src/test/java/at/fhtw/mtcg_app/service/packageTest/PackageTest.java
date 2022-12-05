package at.fhtw.mtcg_app.service.packageTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class PackageTest {
    @Test
    void testCreatePackages() throws IOException {
        URL url = new URL("http://localhost:10001/packages");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Authorization", "Basic admin-mtcgToken");
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("[{\"Id\":\"jbr56bjq-37d0-426e-994e-43fc3ac83c08\", \"Name\":\"Pickachu\", \"Damage\": 19.0}, {\"Id\":\"jbr56bjq-e25e-4a95-aa2c-782823f36e2a\", \"Name\":\"Dragon\", \"Damage\": 50.0}, {\"Id\":\"jbr56bjq-7c86-4d06-9a80-641c2019a79f\", \"Name\":\"WaterSpell\", \"Damage\": 20.0}, {\"Id\":\"jbr56bjq-bdb2-47e5-b6e4-68c5ab389334\", \"Name\":\"Ork\", \"Damage\": 45.0}, {\"Id\":\"jbr56bjq-649c-40f9-ba3a-8657f4b3439f\", \"Name\":\"FireSpell\", \"Damage\": 25.0}]");
        printWriter.close();

        InputStream inputStream = urlConnection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        Assertions.assertEquals(bufferedReader.readLine(), "Package and cards successfully created");

        bufferedReader.close();
    }

    @Test
    void testCardExistsAlready() throws IOException {
        URL url = new URL("http://localhost:10001/packages");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Authorization", "Basic admin-mtcgToken");
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("[{\"Id\":\"jbr56bjq-37d0-426e-994e-43fc3ac83c08\", \"Name\":\"Pickachu\", \"Damage\": 19.0}, {\"Id\":\"jbr56bjq-e25e-4a95-aa2c-782823f36e2a\", \"Name\":\"Dragon\", \"Damage\": 50.0}, {\"Id\":\"jbr56bjq-7c86-4d06-9a80-641c2019a79f\", \"Name\":\"WaterSpell\", \"Damage\": 20.0}, {\"Id\":\"jbr56bjq-bdb2-47e5-b6e4-68c5ab389334\", \"Name\":\"Ork\", \"Damage\": 45.0}, {\"Id\":\"jbr56bjq-649c-40f9-ba3a-8657f4b3439f\", \"Name\":\"FireSpell\", \"Damage\": 25.0}]");
        printWriter.close();

        if(urlConnection.getResponseCode()== HttpURLConnection.HTTP_CONFLICT){
            System.out.println(urlConnection.getResponseCode());
            Assertions.assertFalse(false);
        }
    }
}
