package com.example.ethan_perera_2331419;

import com.example.ethan_perera_2331419.db.SQL_Driver;
import com.example.ethan_perera_2331419.models.Articles;
import com.example.ethan_perera_2331419.models.rate;
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
import java.util.ResourceBundle;

public class view_article_controller extends fundamental_tools implements Initializable {
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
    //------Variable Loaders------
    private final store_user_details new_user = new store_user_details();
    private String global_username = "";
    private int count = 0;
    //------SQL Based Variables------
    private PreparedStatement pstmt;
    private String sql;
    //------Object Initializers------
    private final SQL_Driver SQL_obj = new SQL_Driver();
    private final Articles news_obj = new Articles();
    private final Web_Content web_instance = new Web_Content();
    private final rate rateService = new rate();
    //------Variables Continued------
    private final JsonArray articles = news_obj.readJsonFile();
    private final String[] categories_array = news_obj.get_categories();
    //------Scene Switchers------
    public void switchToHome_Not_Validated(ActionEvent event) throws IOException {
        scene_switcher(event, "home_page.fxml");
    }
    //------Controller Functions------
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
            rateService.skipArticle(global_username, skippedArticle, index, forward, global_username);
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
            runInBackground(()->add_to_read(url));
        });
    }

    private void setupLikeButtonAction(String description, Button likeBtn, String globalUsername) {
        likeBtn.setOnAction(event -> rateService.likeArticle(description, globalUsername, likeBtn));
    }

    public void reload_page(){
        web_instance.reload_page();
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
        }else
            filtered_articles_choicebox.getItems().addAll(categories_array);
        if (news_webview != null) {
            web_instance.initialize_engine(news_webview);
        } else {
            System.err.println("WebView is not yet initialized! Check your FXML.");
        }
    }
}
