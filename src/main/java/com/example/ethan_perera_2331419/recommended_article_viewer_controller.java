package com.example.ethan_perera_2331419;

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
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.scene.web.WebView;

public class recommended_article_viewer_controller extends fundamental_tools implements Initializable{

    permanant_details new_user = new permanant_details();

    private String username = new_user.getInstance().getGlobalDetails();

    private Stage stage;
    private Scene scene;
    private Parent root;


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
        clearSessionCredentials(username);
        System.exit(1);
    }

    //------Variable Initialization------

    private final OllamaContent genre_specifier = new OllamaContent();
    private final SQL_Content SQL_obj = new SQL_Content();
    private final JSON_Reader news_obj = new JSON_Reader();// Instantiated object for JSON File reader
    private final JsonArray articles = news_obj.readJsonFile();// JsonArray for news articles
    private final Web_Content web_instance = new Web_Content();
    private int count;
    private String summarized_genre;

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

    //------Main Content------

    private void displayRecommendedArticles(int index, boolean forward) {
        String[] preferred_genre;
        preferred_genre = switch (summarized_genre.toLowerCase()) {
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
            default -> new String[]{}; // Empties array for unrecognized genre
        };

        int originalIndex = index;

        do {
            JsonObject article = articles.get(index).getAsJsonObject();
            String category = article.get("category").getAsString();

            for (String genre : preferred_genre) {
                if (category.equalsIgnoreCase(genre)) {
                    updateRecommendedArticleUI(article);
                    return;
                }
            }

            index = forward ? (index + 1) % articles.size() : (index - 1 + articles.size()) % articles.size();
        } while (index != originalIndex); // Loop until it has cycled through all articles

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

    //------Recommended Article Switcher------

    public void recommended_next_article() throws IOException {
        if (!articles.isEmpty()) {
            count = (count + 1) % articles.size();
            displayRecommendedArticles(count, true);
            recommended_news_webview.setManaged(true);
        } else {
            showAlert("No Articles", "No articles to display.", Alert.AlertType.INFORMATION);
        }
    }

    public void recommended_previous_article() throws IOException {
        if (!articles.isEmpty()) {
            count = (count - 1 + articles.size()) % articles.size();
            displayRecommendedArticles(count, false); // Backward direction
        } else {
            showAlert("No Articles", "No articles to display.", Alert.AlertType.INFORMATION);
        }
    }

    public void reload_page(){
        web_instance.reload_page();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            summarized_genre = genre_specifier.getRespones(username + "_liked_articles.txt");// Ollama initialized here and genre summarized before page loaded
            showAlert("Preferred Genre","Your preferred Genre is: '"+summarized_genre+"'", Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (recommended_news_webview != null) {
            web_instance.initialize_engine(recommended_news_webview);
        } else {
            System.err.println("WebView is not yet initialized! Check your FXML.");
        }
    }
}