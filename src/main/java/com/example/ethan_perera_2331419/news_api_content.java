package com.example.ethan_perera_2331419;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class news_api_content {
    private static String category;
    public news_api_content(String category){
        this.category = category;
    }
    private static final String API_KEY = System.getenv("apikey");
    //private static final String API_URL = "https://newsapi.org/v2/top-headlines?country=us&category=" + category + "&apiKey=" + API_KEY;

    private static final String API_URL = "https://newsapi.org/v2/everything?q=USA&apiKey=" + API_KEY;

    public JsonArray get_news_api() {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) { // HTTP OK
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                in.close();
                connection.disconnect();

                JsonObject jsonObject = JsonParser.parseString(content.toString()).getAsJsonObject();

                return jsonObject.getAsJsonArray("articles");
            } else {
                System.out.println("Error: API responded with code " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}