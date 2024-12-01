package com.example.ethan_perera_2331419.models;

import com.example.ethan_perera_2331419.services.fundamental_tools;
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

        showAlert("Article Liked", "The article was liked!", Alert.AlertType.INFORMATION);
        likeButton.setDisable(true);

        String fileName = "user_cache/" + userName + "_liked_articles.txt";
        write_to_text_file(fileName, description);
    }

    public void skipArticle(String userName, JsonObject article, String globalUsername) {
        if (userName.equalsIgnoreCase("temp")) {
            showAlert("Temp User", "Ensure that you login to use this feature", Alert.AlertType.ERROR);
            return;
        }

        String skippedArticleTitle = article.get("headline").getAsString();
        String fileName = "user_cache/" + globalUsername + "_skipped_articles.txt";

        write_to_text_file(fileName, skippedArticleTitle);
        showAlert("Article Skipped", "'" + skippedArticleTitle + "' was skipped", Alert.AlertType.INFORMATION);
    }
}