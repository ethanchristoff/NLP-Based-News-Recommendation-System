package com.example.ethan_perera_2331419;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

class OllamaContent {
    private String prompt = " ";
    private final String constraints = "(Just select one of the genres, there is no need for an explanation or any other text whatsoever, and give your answer)Which of the following genres is the given text: Society & Culture, News & Current Events, Lifestyle & Well-being, Entertainment & Leisure";

    public String sendRequest() throws IOException {
        URL url = new URL("http://localhost:11434/api/generate");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        String modelName = "llama3.1";
        String jsonInputString;

        jsonInputString = String.format(
                "{\"model\":\"%s\",\"prompt\":\"%s\",\"stream\": false}", modelName, prompt+" Constraint:"+constraints
        );

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int code = conn.getResponseCode();
        System.out.println("Response code: " + code);

        if (code == 400) {
            conn.disconnect();
            return "There was an issue connecting to 'http://localhost:11434/api/generate'";
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        JSONObject jsonResponse = new JSONObject(response.toString());
        conn.disconnect();
        return jsonResponse.getString("response");
    }

    public String getRespones(String filename) throws IOException {
        StringBuilder likedArticles = new StringBuilder();

        try {
            List<String> lines = Files.readAllLines(Paths.get(filename));
            for (String line : lines) {
                if (!Objects.equals(line.trim(), "-------------------")) {
                    likedArticles.append(line).append(" ");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        prompt = likedArticles.toString().replace("\"", "").trim();// Removes any quotation marks due to colliding characters

        return sendRequest();
    }

    public static void main(String[] args) throws IOException {
        OllamaContent olc = new OllamaContent();
        String response = olc.getRespones("ethan_perera_liked_articles.txt");
        System.out.println(response);
    }
}