package com.example.ethan_perera_2331419.models;

import com.example.ethan_perera_2331419.services.fundamental_tools;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import com.google.gson.JsonObject;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

public class rate extends fundamental_tools {

    public void likeArticle(String description, String userName, Button likeButton) {
        if (userName.equalsIgnoreCase("temp")) {
            showAlert("Temp User", "Ensure that you login to use this feature", Alert.AlertType.ERROR);
            likeButton.setDisable(true);
            return;
        }

        String fileName = "user_cache/" + userName + "_liked_articles.txt";

        if (isTextInFile(fileName, description)) {
            showAlert("Already Liked", "You have already liked this article.", Alert.AlertType.WARNING);
        } else {
            write_to_text_file(fileName, description);
            showAlert("Article Liked", "The article was liked!", Alert.AlertType.INFORMATION);
            likeButton.setDisable(true);
        }
    }

    public void skipArticle(String userName, JsonObject article, String globalUsername) {
        if (userName.equalsIgnoreCase("temp")) {
            showAlert("Temp User", "Ensure that you login to use this feature", Alert.AlertType.ERROR);
            return;
        }

        String skippedArticleTitle = article.get("headline").getAsString();
        String fileName = "user_cache/" + globalUsername + "_skipped_articles.txt";

        if (isTextInFile(fileName, skippedArticleTitle)) {
            showAlert("Already Skipped", "You have already skipped this article.", Alert.AlertType.WARNING);
        } else {
            write_to_text_file(fileName, skippedArticleTitle);
            showAlert("Article Skipped", "'" + skippedArticleTitle + "' was skipped", Alert.AlertType.INFORMATION);
        }
    }

    private boolean isTextInFile(String fileName, String text) {
        File file = new File(fileName);
        if (!file.exists()) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals(text)) {
                    return true;
                }
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to read the file.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
        return false;
    }
}