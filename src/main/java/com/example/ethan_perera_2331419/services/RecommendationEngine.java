package com.example.ethan_perera_2331419.services;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class RecommendationEngine {

    private final String constraints = "(Just select one of the genres, there is no need for an explanation or any other text whatsoever, and give your answer) Which of the following genres is the given text: Society & Culture, News & Current Events, Lifestyle & Well-being, Entertainment & Leisure";

    public String sendRequest(String prompt) throws IOException {
        URL url = new URL("http://localhost:11434/api/generate");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        String modelName = "llama3.1";
        String jsonInputString = String.format(
                "{\"model\":\"%s\",\"prompt\":\"%s Constraint:%s\",\"stream\": false}", modelName, prompt, constraints
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

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getString("response");
        } finally {
            conn.disconnect();
        }
    }

    public String getResponse(String filename) throws IOException {
        StringBuilder likedArticles = new StringBuilder();

        try {
            List<String> lines = Files.readAllLines(Paths.get(filename));
            for (String line : lines) {
                if (!Objects.equals(line.trim(), "-------------------")) {
                    likedArticles.append(line).append(" ");
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filename);
            e.printStackTrace();
            return "File reading error.";
        }

        String prompt = likedArticles.toString().replace("\"", "").trim(); // Remove any quotation marks
        return sendRequest(prompt);
    }
}
