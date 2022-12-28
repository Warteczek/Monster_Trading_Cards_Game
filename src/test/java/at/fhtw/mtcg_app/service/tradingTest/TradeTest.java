package at.fhtw.mtcg_app.service.tradingTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class TradeTest {
    @Test
    void testAddTradeDeals() throws IOException {
        URL url = new URL("http://localhost:10001/tradings");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Authorization", "Basic User1-mtcgToken");
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("{\"Id\":\"tradeID1-37d0-426e-994e-43fc3ac83c08\", \"CardToTrade\":\"jbr56bjq-649c-40f9-ba3a-8657f4b3439f\", \"Type\": \"monster\", \"MinimumDamage\": 15.0}");
        printWriter.close();

        InputStream inputStream = urlConnection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        if(urlConnection.getResponseCode()==201){
            Assertions.assertEquals(bufferedReader.readLine(), "Trading deal successfully created");
        }else{
            Assertions.assertFalse(true);
        }

        bufferedReader.close();
    }

    @Test
    void testAddTradeDealsCardLockedInDeck() throws IOException {
        URL url = new URL("http://localhost:10001/tradings");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Authorization", "Basic User1-mtcgToken");
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("{\"Id\":\"tradeID2-37d0-426e-994e-43fc3ac83c08\", \"CardToTrade\":\"jbr56bjq-37d0-426e-994e-43fc3ac83c08\", \"Type\": \"monster\", \"MinimumDamage\": 15.0}");
        printWriter.close();

        if(urlConnection.getResponseCode()==403){
            Assertions.assertFalse(false);
        }else{
            Assertions.assertFalse(true);
        }

    }

    @Test
    void testAddTradeDealsCardNotOwnedByUser() throws IOException {
        URL url = new URL("http://localhost:10001/tradings");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Authorization", "Basic User1-mtcgToken");
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("{\"Id\":\"tradeID3-37d0-426e-994e-43fc3ac83c08\", \"CardToTrade\":\"1cb6ab86-bdb2-47e5-b6e4-68c5ab389334\", \"Type\": \"monster\", \"MinimumDamage\": 15.0}");
        printWriter.close();

        if(urlConnection.getResponseCode()==403){
            Assertions.assertFalse(false);
        }else{
            Assertions.assertFalse(true);
        }
    }

    @Test
    void testAddTradeDealAlreadyExists() throws IOException {
        URL url = new URL("http://localhost:10001/tradings");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Authorization", "Basic User1-mtcgToken");
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("{\"Id\":\"tradeID1-37d0-426e-994e-43fc3ac83c08\", \"CardToTrade\":\"jbr56bjq-649c-40f9-ba3a-8657f4b3439f\", \"Type\": \"monster\", \"MinimumDamage\": 15.0}");
        printWriter.close();

        if(urlConnection.getResponseCode()==409){
            Assertions.assertFalse(false);
        }else{
            Assertions.assertFalse(true);
        }
    }

    @Test
    void testGetTradingDeals() throws IOException {
        URL url = new URL("http://localhost:10001/tradings");
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
    void testExecuteTradingDealsWithYourself() throws IOException {
        URL url = new URL("http://localhost:10001/tradings/tradeID1-37d0-426e-994e-43fc3ac83c08");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Authorization", "Basic User1-mtcgToken");
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("171f6076-4eb5-4a7d-b3f2-2d650cc3d237");
        printWriter.close();

        if(urlConnection.getResponseCode()==403){
            Assertions.assertFalse(false);
        }else{
            System.out.println(urlConnection.getResponseCode());
            Assertions.assertFalse(true);
        }
    }

    @Test
    void testExecuteTradingDeal() throws IOException {
        URL url = new URL("http://localhost:10001/tradings/tradeID1-37d0-426e-994e-43fc3ac83c08");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Authorization", "Basic kienboec-mtcgToken");
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("2272ba48-6662-404d-a9a1-41a9bed316d9");
        printWriter.close();

        InputStream inputStream = urlConnection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        if(urlConnection.getResponseCode()==200){
            Assertions.assertEquals(bufferedReader.readLine(), "Trading deal successfully executed.");

        }else{
            Assertions.assertFalse(true);
        }

        bufferedReader.close();
    }

    @Test
    void testExecuteTradingDealLockedInDeck() throws IOException {
        URL url = new URL("http://localhost:10001/tradings/tradeID1-37d0-426e-994e-43fc3ac83c08");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Authorization", "Basic kienboec-mtcgToken");
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("166c1fd5-4dcb-41a8-91cb-f45dcd57cef3");
        printWriter.close();

        if(urlConnection.getResponseCode()==403){
            Assertions.assertFalse(false);
        }else{
            System.out.println(urlConnection.getResponseCode());
            Assertions.assertFalse(true);
        }
    }

    @Test
    void testDeleteTradingDeals() throws IOException {
        URL url = new URL("http://localhost:10001/tradings/tradeID1-37d0-426e-994e-43fc3ac83c08");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("DELETE");
        urlConnection.setRequestProperty("Authorization", "Basic User1-mtcgToken");
        urlConnection.setDoOutput(true);
        int responseCode= urlConnection.getResponseCode();
        System.out.println(responseCode);

        if(responseCode== HttpURLConnection.HTTP_OK) {
            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            Assertions.assertEquals(bufferedReader.readLine(), "Trading deal successfully deleted");
        }
        else {
            Assertions.assertFalse(true);
        }
    }
}
