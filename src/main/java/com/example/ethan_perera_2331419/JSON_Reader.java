package com.example.ethan_perera_2331419;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class JSON_Reader {

    private static final String FILE_PATH = "src/main/java/com/example/ethan_perera_2331419/Articles/News_Category_Dataset_v3.json";

    public JsonArray readJsonFile() {
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

    public static void main(String[] args) {
        JSON_Reader reader = new JSON_Reader();
        JsonArray articles = reader.readJsonFile();

        System.out.println("Total Articles in JsonArray: " + articles.size());
    }
}
