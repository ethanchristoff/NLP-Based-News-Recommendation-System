package com.example.ethan_perera_2331419;

import com.example.ethan_perera_2331419.db.SQL_Driver;
import com.example.ethan_perera_2331419.recommendation_engine.OllamaDriver;
import com.example.ethan_perera_2331419.services.JSON_Reader;
import com.example.ethan_perera_2331419.services.Web_Content;
import com.example.ethan_perera_2331419.services.fundamental_tools;
import com.example.ethan_perera_2331419.services.store_details;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.Initializable;

import java.io.*;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import javafx.scene.web.WebView;

public class recommended_article_viewer_controller extends fundamental_tools implements Initializable{
    //------Variable Initialization------
    private store_details new_user = new store_details();
    private Stage stage;
    private Scene scene;
    private Parent root;
    private int count;
    private String summarized_genre;
    private final String global_username = new_user.getInstance().getGlobalDetails();

    //------Object Initialization------
    private final OllamaDriver genre_specifier = new OllamaDriver();
    private final SQL_Driver SQL_obj = new SQL_Driver();
    private final JSON_Reader news_obj = new JSON_Reader();
    private final JsonArray articles = news_obj.readJsonFile();
    private final Web_Content web_instance = new Web_Content();

    //------FXML loaders------

    @FXML
    Label recommended_article_header;
    @FXML
    Label recommended_article_category;
    @FXML
    Label recommended_article_release_date;
    @FXML
    ScrollPane recommended_article_content;
    @FXML
    WebView recommended_news_webview;
    @FXML
    Hyperlink recommended_article_link;
    //------Scene Switchers------

    public void switchToHome_Not_Validated(ActionEvent event) throws IOException{
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("home_page.fxml")));

        if (root == null) {
            showAlert("Error", "Failed to load Home page!", Alert.AlertType.ERROR);
            return;
        }

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    //------Exit Program------

    public void exit_program() {
        remove_from_logged_in_users(global_username,SQL_obj);
        clearSessionCredentials(global_username);
        System.exit(1);
    }

    //------Main Content------

    public void displayRecommendedArticles(int index, boolean forward) {
        String fileName = "user_cache/" + global_username + "_skipped_articles.txt";
        String[] skippedHeadlines;
        if (file_exists(fileName)){
            skippedHeadlines = readHeadersFromTextFile(fileName);
        }else{
            skippedHeadlines = new String[]{};// Initiate an empty array
        }

        Set<String> skippedHeadlinesSet = new HashSet<>(Arrays.asList(skippedHeadlines));

        String[] preferred_genre = switch (summarized_genre.toLowerCase()) {
            case "society & culture" -> new String[]{
                    "LATINO VOICES", "BLACK VOICES", "QUEER VOICES", "STYLE & BEAUTY",
                    "STYLE", "CULTURE & ARTS", "ARTS", "ARTS & CULTURE",
                    "PARENTING", "PARENTS", "WEDDINGS", "DIVORCE",
                    "WOMEN", "GREEN", "ENVIRONMENT"
            };
            case "news & current events" -> new String[]{
                    "WORLDPOST", "U.S. NEWS", "WORLD NEWS", "POLITICS",
                    "CRIME", "MEDIA", "EDUCATION", "TECH", "SCIENCE"
            };
            case "lifestyle & well-being" -> new String[]{
                    "HEALTHY LIVING", "WELLNESS", "HOME & LIVING", "GOOD NEWS",
                    "FOOD & DRINK", "TASTE", "TRAVEL", "IMPACT"
            };
            case "entertainment & leisure" -> new String[]{
                    "COMEDY", "WEIRD NEWS", "ENTERTAINMENT", "SPORTS",
                    "BUSINESS", "MONEY", "COLLEGE", "FIFTY", "RELIGION"
            };
            default -> new String[]{};
        };

        int originalIndex = index;

        do {
            JsonObject article = articles.get(index).getAsJsonObject();
            String category = article.get("category").getAsString();
            String headline = article.get("headline").getAsString();

            if (skippedHeadlinesSet.contains(headline)) {
                index = forward ? (index + 1) % articles.size() : (index - 1 + articles.size()) % articles.size();
                continue;
            }

            for (String genre : preferred_genre) {
                if (category.equalsIgnoreCase(genre)) {
                    updateRecommendedArticleUI(article);
                    return;
                }
            }

            index = forward ? (index + 1) % articles.size() : (index - 1 + articles.size()) % articles.size();
        } while (index != originalIndex);

        showAlert("No Articles", "No articles available for the selected filter.", Alert.AlertType.INFORMATION);
    }


    private void updateRecommendedArticleUI(JsonObject article) {
        recommended_article_header.setText(article.get("headline").getAsString());
        recommended_article_category.setText(article.get("category").getAsString());
        recommended_article_release_date.setText(article.get("date").getAsString());

        // Vbox used to enable scrolling in large descriptions

        VBox articleBox = new VBox(10); // Sets the spacing set directly in constructor
        Label contentLabel = new Label(article.get("short_description").getAsString());
        contentLabel.setWrapText(true);

        articleBox.getChildren().add(contentLabel);
        recommended_article_content.setContent(articleBox);
        recommended_article_content.setFitToWidth(true);

        String url = article.get("link").getAsString();
        setupRecommendedArticleLinkAction(url);
        web_instance.load_page(url);
    }

    // Article link disabled until an article is linked to it

    private void setupRecommendedArticleLinkAction(String url) {
        SQL_obj.open_connection();
        recommended_article_link.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            clipboard.setContent(new ClipboardContent() {{ putString(url); }});
            showAlert("Information", "Link copied!", Alert.AlertType.INFORMATION);
        });
    }

    public void set_preferred_genre(String preferred_genre) {
        String sql = "UPDATE users SET Preferred_Genres = ? WHERE username = ?";
        SQL_obj.set_query(sql);
        PreparedStatement pstmt = SQL_obj.getPreparedStatement();

        try {
            String username = readSessionCredentials(global_username)[0];

            pstmt.setString(1, preferred_genre);
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


    //------Recommended Article Switcher------

    public void recommended_next_article() {
        if (!articles.isEmpty()) {
            count = (count + 1) % articles.size();
            displayRecommendedArticles(count, true);
            recommended_news_webview.setManaged(true);
        } else {
            showAlert("No Articles", "No articles to display.", Alert.AlertType.INFORMATION);
        }
    }

    public void recommended_previous_article() {
        if (!articles.isEmpty()) {
            count = (count - 1 + articles.size()) % articles.size();
            displayRecommendedArticles(count, false); // Backward direction
        } else {
            showAlert("No Articles", "No articles to display.", Alert.AlertType.INFORMATION);
        }
    }

    //------Web Page Re-Loader------

    public void reload_page(){
        web_instance.reload_page();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showAlert("Loading Genre", "Please wait as the model is summarizing your genre. This may take some time!", Alert.AlertType.INFORMATION);
        CountDownLatch latch = new CountDownLatch(1);

        runInBackground(() -> {
            try {
                // Perform the background operation
                summarized_genre = genre_specifier.getResponse("user_cache/" + global_username + "_liked_articles.txt");
                set_preferred_genre(summarized_genre);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                // Decrement the latch to signal the task completion
                latch.countDown();
            }
        });

        // Wait for the task to complete
        try {
            latch.await(); // Blocks until the latch count reaches 0
            // Proceed with further actions after the background task is done
            showAlert("Preferred Genre", "Your preferred Genre is: '" + summarized_genre + "'", Alert.AlertType.INFORMATION);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore the interrupted status
            System.err.println("Task interrupted: " + e.getMessage());
        }

        if (recommended_news_webview != null) {
            web_instance.initialize_engine(recommended_news_webview);
        } else {
            System.err.println("WebView is not yet initialized! Check your FXML.");
        }
    }
}