package com.example.ethan_perera_2331419.models;

import com.example.ethan_perera_2331419.db.SQL_Driver;
import com.example.ethan_perera_2331419.scene_switcher_service;
import com.example.ethan_perera_2331419.services.Web_Content;
import com.example.ethan_perera_2331419.services.fundamental_tools;
import com.example.ethan_perera_2331419.services.store_user_details;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class view_articles extends fundamental_tools implements Initializable {
    //------FXML Loaders------
    @FXML
    private Label article_header;
    @FXML
    private Label article_category;
    @FXML
    private Label article_release_date;
    @FXML
    private ScrollPane article_content;
    @FXML
    private Hyperlink article_link;
    @FXML
    private Button like_btn;
    @FXML
    private WebView news_webview;
    @FXML
    private ChoiceBox<String> filtered_articles_choicebox;
    @FXML
    private Label recommended_article_header;
    @FXML
    private Label recommended_article_category;
    @FXML
    private Label recommended_article_release_date;
    @FXML
    private ScrollPane recommended_article_content;
    @FXML
    private Hyperlink recommended_article_link;
    //------Variable Loaders------
    private String global_username = "";
    private int count = 0;
    private String summarized_genre;
    //------SQL Based Variables------
    private PreparedStatement pstmt;
    private String sql;
    //------Object Initializers------
    private final store_user_details new_user = new store_user_details();
    private final SQL_Driver SQL_obj = new SQL_Driver();
    private final Articles news_obj = new Articles();
    private final Web_Content web_instance = new Web_Content();
    private final recommend recommend_articles = new recommend();
    private final scene_switcher_service scene_switcher = new scene_switcher_service();
    private final user current_user = new user(global_username,true);
    //------Variables Continued------
    private final JsonArray articles = news_obj.readJsonFile();
    private final String[] categories_array = news_obj.get_categories();
    private String[] preferred_genre;
    private JsonArray recommended_articles;
    //------Scene Switchers------
    public void switchToHome_Not_Validated(ActionEvent event) throws IOException {
        scene_switcher.switch_scene(event, "home_page.fxml");
    }
    //------view_article------
    public void add_to_read(String url) {
        sql = "UPDATE users SET Read_Articles = ? WHERE username = ?";
        SQL_obj.set_query(sql);
        pstmt = SQL_obj.getPreparedStatement();

        try {
            String username = readSessionCredentials(global_username)[0];

            pstmt.setString(1, url);
            pstmt.setString(2, username);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database connection error.", Alert.AlertType.ERROR);
        } finally {
            SQL_obj.closeResources();
            SQL_obj.close_connection();
        }
    }
    public void next_article() {
        if (articles != null && articles.size() > 0) {
            count = (count + 1) % articles.size();
            displayArticle(count, true);
            like_btn.setDisable(false);
            news_webview.setManaged(true);
        } else {
            showAlert("No Articles", "No articles to display.", Alert.AlertType.INFORMATION);
        }
    }

    public void previous_article() {
        if (articles != null && articles.size() > 0) {
            count = (count - 1 + articles.size()) % articles.size(); // Move to the previous article
            displayArticle(count, false); // Backward direction
            like_btn.setDisable(false);
        } else {
            showAlert("No Articles", "No articles to display.", Alert.AlertType.INFORMATION);
        }
    }

    public void skip_next_article() {
        if (articles != null && articles.size() > 0) {
            skip_article(count, true);
            count = (count + 1) % articles.size();
            like_btn.setDisable(false);
            news_webview.setManaged(true);
        } else {
            showAlert("No Articles", "No articles to display.", Alert.AlertType.INFORMATION);
        }
    }

    private void skip_article(int index, boolean forward) {
        JsonObject skippedArticle = displayArticle(index, forward);
        if (skippedArticle != null) {
            current_user.skipsArticle(global_username, skippedArticle, global_username);
        }
    }

    private JsonObject displayArticle(int index, boolean forward) {
        String ignoredGenre = filtered_articles_choicebox.getValue();
        int originalIndex = index;

        do {
            JsonObject article = articles.get(index).getAsJsonObject();
            String category = article.get("category").getAsString();

            if (!category.equalsIgnoreCase(ignoredGenre)) {
                updateArticleUI(article);
                return article;
            }

            index = forward ? (index + 1) % articles.size() : (index - 1 + articles.size()) % articles.size();
        } while (index != originalIndex);

        showAlert("No Articles", "No articles available for the selected filter.", Alert.AlertType.INFORMATION);
        return null;
    }

    private void updateArticleUI(JsonObject article) {
        article_header.setText(article.get("headline").getAsString());
        article_category.setText(article.get("category").getAsString());
        article_release_date.setText(article.get("date").getAsString());

        VBox articleBox = new VBox(10); // Sets the spacing set directly in constructor
        Label contentLabel = new Label(article.get("short_description").getAsString());
        contentLabel.setWrapText(true);

        articleBox.getChildren().add(contentLabel);
        article_content.setContent(articleBox);
        article_content.setFitToWidth(true);

        String url = article.get("link").getAsString();
        setupArticleLinkAction(url);
        web_instance.load_page(url); // Loads the article page

        setupLikeButtonAction(article.get("short_description").getAsString(),like_btn,global_username);
    }

    private void setupArticleLinkAction(String url) {
        SQL_obj.open_connection();
        article_link.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            clipboard.setContent(new ClipboardContent() {{ putString(url); }});
            showAlert("Information", "Link copied!", Alert.AlertType.INFORMATION);

            add_to_read(url);
        });
    }

    private void setupLikeButtonAction(String description, Button likeBtn, String globalUsername) {
        likeBtn.setOnAction(event -> current_user.likesArticle(description, globalUsername, likeBtn));
    }

    public void reload_page(){
        web_instance.reload_page();
    }

    // ------Recommended Article Switcher------
    public void recommended_next_article() {
        if (!recommended_articles.isEmpty()) {
            count = (count + 1) % recommended_articles.size();
            displayRecommendedArticles(count, true);
        } else {
            showAlert("No Articles", "No articles to display.", Alert.AlertType.INFORMATION);
        }
    }

    public void recommended_previous_article() {
        if (!recommended_articles.isEmpty()) {
            count = (count - 1 + recommended_articles.size()) % recommended_articles.size();
            displayRecommendedArticles(count, false);
        } else {
            showAlert("No Articles", "No articles to display.", Alert.AlertType.INFORMATION);
        }
    }

    // ------Recommend Articles Main Content------
    public void displayRecommendedArticles(int index, boolean forward) {
        String fileName = "user_cache/" + global_username + "_skipped_articles.txt";
        String[] skippedHeadlines = file_exists(fileName) ? readHeadersFromTextFile(fileName) : new String[]{};
        Set<String> skippedHeadlinesSet = new HashSet<>(Arrays.asList(skippedHeadlines));

        int originalIndex = index;

        do {
            JsonObject article = recommended_articles.get(index).getAsJsonObject();
            String category = article.get("category").getAsString();
            String headline = article.get("headline").getAsString();

            if (skippedHeadlinesSet.contains(headline)) {
                index = forward ? (index + 1) % recommended_articles.size() : (index - 1 + recommended_articles.size()) % recommended_articles.size();
                continue;
            }

            for (String genre : preferred_genre) {
                if (category.equalsIgnoreCase(genre)) {
                    updateRecommendedArticleUI(article);
                    return;
                }
            }

            index = forward ? (index + 1) % recommended_articles.size() : (index - 1 + recommended_articles.size()) % recommended_articles.size();
        } while (index != originalIndex);

        showAlert("No Articles", "No articles available for the selected filter.", Alert.AlertType.INFORMATION);
    }

    private void updateRecommendedArticleUI(JsonObject article) {
        recommended_article_header.setText(article.get("headline").getAsString());
        recommended_article_category.setText(article.get("category").getAsString());
        recommended_article_release_date.setText(article.get("date").getAsString());

        VBox articleBox = new VBox(10);
        Label contentLabel = new Label(article.get("short_description").getAsString());
        contentLabel.setWrapText(true);

        articleBox.getChildren().add(contentLabel);
        recommended_article_content.setContent(articleBox);
        recommended_article_content.setFitToWidth(true);

        String url = article.get("link").getAsString();
        setupRecommendedArticleLinkAction(url);
    }

    private void setupRecommendedArticleLinkAction(String url) {
        recommended_article_link.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(url);
            clipboard.setContent(content);
            showAlert("Information", "Link copied!", Alert.AlertType.INFORMATION);
        });
    }

    //------Exit Program------
    public void exit_program() {
        remove_from_logged_in_users(global_username,SQL_obj);
        clearSessionCredentials(global_username);
        System.exit(1);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        global_username = new_user.getInstance().getGlobalDetails();
        if (filtered_articles_choicebox == (null)){
            System.err.println("ChoiceBox not yet initialized");
            summarized_genre = recommend_articles.get_preferred_genre(global_username);
            preferred_genre = recommend_articles.getPreferredGenreArray(summarized_genre.toLowerCase());
            recommended_articles = news_obj.getPreferredArticles(news_obj.readJsonFile(), preferred_genre);
        }else
            filtered_articles_choicebox.getItems().addAll(categories_array);
        if (news_webview != null) {
            web_instance.initialize_engine(news_webview);
        } else {
            System.err.println("WebView is not yet initialized! Check your FXML.");
        }
    }
}
