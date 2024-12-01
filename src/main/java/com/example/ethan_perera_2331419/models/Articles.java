package com.example.ethan_perera_2331419.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Articles {
    //------Variable Initialization------
    private static final String FILE_PATH = "src/main/java/com/example/ethan_perera_2331419/Articles/News_Category_Dataset_v3.json";

    //------Articles Functions------
    public static JsonArray readJsonFile() {
        JsonArray jsonArray = new JsonArray();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                JsonObject jsonObject = JsonParser.parseString(line).getAsJsonObject();
                jsonArray.add(jsonObject);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    // Returns the categories of the news json file
    public static String[] get_categories() {
        JsonArray array = readJsonFile();
        Set<String> categorySet = new HashSet<>();
        for (int i = 0; i < array.size(); i++) {
            JsonObject arrayObject = array.get(i).getAsJsonObject();
            String category = arrayObject.get("category").getAsString();
            categorySet.add(category);
        }
        return categorySet.toArray(new String[0]);
    }

    public JsonArray getPreferredArticles(JsonArray articles, String[] preferredGenres) {
        JsonArray preferredArticles = new JsonArray();

        for (int i = 0; i < articles.size(); i++) {
            JsonObject article = articles.get(i).getAsJsonObject();
            for (String genre : preferredGenres) {
                if (article.get("category").getAsString().equalsIgnoreCase(genre)) {
                    preferredArticles.add(article);
                }
            }
        }

        return preferredArticles;
    }

}
