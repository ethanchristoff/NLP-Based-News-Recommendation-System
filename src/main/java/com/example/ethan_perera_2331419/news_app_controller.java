package com.example.ethan_perera_2331419;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Platform;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.scene.web.WebView;


class ConsoleRedirect {
    private final TextArea textArea;
    private final PrintStream consoleStream;

    public ConsoleRedirect(TextArea textArea) {
        this.textArea = textArea;
        consoleStream = System.out;
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) {
                appendText(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) {
                appendText(new String(b, off, len));
            }

            @Override
            public void write(byte[] b) {
                write(b, 0, b.length);
            }
        };
        PrintStream customStream = new PrintStream(out, true);
        System.setOut(customStream);
        System.setErr(customStream);
    }

    private void appendText(String text) {
        Platform.runLater(() -> textArea.appendText(text));
    }
}

public class news_app_controller extends fundamental_tools implements Initializable{

    public static TextArea staticTxtArea;
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TextField username_input;
    @FXML
    private TextField password_input;

    @FXML
    private TextArea text_output_area;

    //------Scene Switchers------

    public void switchToHome_Validated(ActionEvent event) {
        String inputUsername = username_input.getText();
        String inputPassword = password_input.getText();

        if (Objects.equals(inputUsername, "") || Objects.equals(inputPassword, "")) {
            showAlert("Error", "Input Fields Empty!", Alert.AlertType.ERROR);
        } else {
            if (authenticateUser(inputUsername, inputPassword)) {
                saveSessionCredentials(inputUsername, inputPassword);
                try {
                    root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("home_page.fxml")));

                    if (root == null) {
                        showAlert("Error", "FXML file not found!", Alert.AlertType.ERROR);
                        return;
                    }

                    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Error", "Failed to load the main page.", Alert.AlertType.ERROR);
                }
            }
        }
    }

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

    public void switchToSignUp(ActionEvent event) throws IOException{
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("sign_up.fxml")));

        if (root == null) {
            showAlert("Error", "Failed to load Sign up page!", Alert.AlertType.ERROR);
            return;
        }

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToIntro(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("sign_in.fxml")));

        if (root == null) {
            showAlert("Error", "Failed to load introduction page!", Alert.AlertType.ERROR);
            return;
        }

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToArticleViewer(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("view_article_page.fxml")));

        if (root == null) {
            showAlert("Error", "Failed to load Article Viewer page!", Alert.AlertType.ERROR);
            return;
        }

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    //------Exit Program---

    public void exit_program() {
        clearSessionCredentials();
        System.exit(1);
    }

    //------Authenticate User------

    private final SQL_Content SQL_obj = new SQL_Content();
    private PreparedStatement pstmt;
    private ResultSet rs;
    private String sql;

    private boolean authenticateUser(String inputUsername, String inputPassword) {

        sql = "SELECT username, password FROM users WHERE username = ? AND password = ?";
        SQL_obj.set_query(sql);
        pstmt = SQL_obj.getPreparedStatement();

        try {
            pstmt.setString(1, inputUsername);
            pstmt.setString(2, inputPassword);
            rs = SQL_obj.get_ResultSet(pstmt);

            if (rs.next()) {
                String authenticatedUsername = rs.getString("username");
                System.out.println("User authenticated: " + authenticatedUsername);
                showAlert("Success", "User authenticated: " + authenticatedUsername, Alert.AlertType.INFORMATION);
                return true;
            } else {
                showAlert("Error", "Invalid username or password.", Alert.AlertType.ERROR);
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database connection error.", Alert.AlertType.ERROR);
            return false;
        } finally {
            SQL_obj.closeResources();
            SQL_obj.close_connection();
        }
    }

    //------Sign Up Function for the sign up pages------

    @FXML
    TextField new_username_input;
    @FXML
    TextField new_password_input;
    @FXML
    TextField new_re_password_input;

    public void sign_up(ActionEvent event) throws IOException {
        SQL_obj.open_connection();// Ensures that the connection is always opened in case an error closes it
        String new_username = new_username_input.getText();
        String new_password = new_password_input.getText();
        String re_entered_password = new_re_password_input.getText();

        if (Objects.equals(new_username, "") || Objects.equals(new_password, "")) {
            showAlert("Missing Information", "Make sure you fill in the username and password field", Alert.AlertType.INFORMATION);
        } else if (Objects.equals(re_entered_password, "")) {
            showAlert("Missing Information", "Ensure that you re-enter your password", Alert.AlertType.INFORMATION);
        } else if (!Objects.equals(new_password, re_entered_password)) {
            showAlert("Incorrect Password!", "Ensure that the password you re-entered matches the other", Alert.AlertType.ERROR);
        } else {
            sql = "SELECT COUNT(*) FROM users WHERE username = ?;";
            SQL_obj.set_query(sql);
            PreparedStatement selectPstmt = SQL_obj.getPreparedStatement();

            try {
                selectPstmt.setString(1, new_username);
                rs = selectPstmt.executeQuery();

                if (rs.next() && rs.getInt(1) > 0) {
                    showAlert("Error", "Username already exists. Please choose another.", Alert.AlertType.ERROR);
                } else {
                    sql = "INSERT INTO users (username, password) VALUES (?, ?);";
                    SQL_obj.set_query(sql);
                    PreparedStatement insertPstmt = SQL_obj.getPreparedStatement();

                    try {
                        insertPstmt.setString(1, new_username);
                        insertPstmt.setString(2, new_password);
                        insertPstmt.executeUpdate();

                        showAlert("Success", "User registered successfully.", Alert.AlertType.INFORMATION);
                        switchToIntro(event);
                    } finally {
                        insertPstmt.close(); // Close the INSERT PreparedStatement after use
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Database connection error.", Alert.AlertType.ERROR);
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (selectPstmt != null) selectPstmt.close();
                    SQL_obj.closeResources();
                    SQL_obj.close_connection();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //------Display User Details function to present previously liked articles------

    @FXML
    Button see_details_btn;

    public void print_user_details() {
        String[] sessionData = readSessionCredentials();
        if (sessionData == null || sessionData[0] == null || sessionData[1] == null) {
            showAlert("Error", "Session not found. Please login first.", Alert.AlertType.ERROR);
            return;
        }

        sql = "SELECT Read_Articles, Liked_Articles, Preferred_Genres  FROM users WHERE username = ? AND password = ?";
        SQL_obj.set_query(sql);
        pstmt = SQL_obj.getPreparedStatement();

        try {

            pstmt.setString(1, sessionData[0]);
            pstmt.setString(2, sessionData[1]);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                String read_articles = rs.getString("Read_Articles");
                String liked_articles = rs.getString("Liked_Articles");
                String preferred_genres = rs.getString("Preferred_Genres");
                System.out.printf("Read Articles: %s\nLiked Articles: %s\nPreferred Genres: %s",read_articles,liked_articles,preferred_genres);
            } else {
                showAlert("Error", "Invalid username or password.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database connection error.", Alert.AlertType.ERROR);
        } finally {
            SQL_obj.closeResources();
            SQL_obj.close_connection();
            see_details_btn.setDisable(true);
        }
    }

    // The following functon simply creates a prepared statement that pushes a query into the database to add
    // the most recently read article to a column in the users table

    private final JSON_Reader news_obj = new JSON_Reader();// Instantiated object for JSON File reader
    private final JsonArray articles = news_obj.readJsonFile();// JsonArray for news articles
    @FXML
    Label article_header;
    @FXML
    Label article_category;
    @FXML
    Label article_release_date;
    @FXML
    TextField filtered_articles;
    @FXML
    ScrollPane article_content;
    @FXML
    Hyperlink article_link;
    @FXML
    Button like_btn;
    @FXML
    WebView news_webview;

    public void add_to_read(String url) {
        sql = " UPDATE users SET Read_Articles = CONCAT(IFNULL(Read_Articles, ''), ?, ', ') WHERE username = ? ";
        SQL_obj.set_query(sql);
        pstmt = SQL_obj.getPreparedStatement();

        try {
            String username = readSessionCredentials()[0];

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

    //------Article Switcher------

    int count = 0;
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


    //------Display Article Function------

    private final Web_Content web_instance = new Web_Content();

    private void displayArticle(int index, boolean forward) {
        String ignoredGenre = filtered_articles.getText().trim();
        int originalIndex = index;

        do {
            JsonObject article = articles.get(index).getAsJsonObject();
            String category = article.get("category").getAsString();

            if (!category.equalsIgnoreCase(ignoredGenre)) {
                updateArticleUI(article);
                return;
            }

            index = forward ? (index + 1) % articles.size() : (index - 1 + articles.size()) % articles.size();
        } while (index != originalIndex); // Loop until it has cycled through all articles

        showAlert("No Articles", "No articles available for the selected filter.", Alert.AlertType.INFORMATION);
    }

    // The following functions are sub-functions of the displayArticle function to minimize the strain on the application
    // when running it. It's broken down by having each function execute a smaller task like so

    private void updateArticleUI(JsonObject article) {
        article_header.setText(article.get("headline").getAsString());
        article_category.setText(article.get("category").getAsString());
        article_release_date.setText(article.get("date").getAsString());

        // Vbox used to enable scrolling in large descriptions

        VBox articleBox = new VBox(10); // Sets the spacing set directly in constructor
        Label contentLabel = new Label(article.get("short_description").getAsString());
        contentLabel.setWrapText(true);

        articleBox.getChildren().add(contentLabel);
        article_content.setContent(articleBox);
        article_content.setFitToWidth(true);

        String url = article.get("link").getAsString();
        setupArticleLinkAction(url);
        web_instance.load_page(url); // Loads the article page

        setupLikeButtonAction(article.get("short_description").getAsString());
    }

    // Article link disabled until an article is linked to it

    private void setupArticleLinkAction(String url) {
        SQL_obj.open_connection();
        article_link.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            clipboard.setContent(new ClipboardContent() {{ putString(url); }});
            showAlert("Information", "Link copied!", Alert.AlertType.INFORMATION);
            add_to_read(url);
        });
    }

    private void setupLikeButtonAction(String description) {
        like_btn.setOnAction(event -> like_article(description, readSessionCredentials()[0]));// Inputs username and description of article that was liked
        like_btn.setDisable(false);
    }

    public void like_article(String description, String userName) {
        showAlert("Article Liked", "The article was liked!", Alert.AlertType.INFORMATION);
        like_btn.setDisable(true);

        String fileName = userName + "_liked_articles.txt";
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.append(description).append("\n-------------------\n");
        } catch (IOException e) {
            showAlert("Error", "Failed to save the article description.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    public void reload_page(){
        web_instance.reload_page();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Error handling to prevent application from NOT running
        if (news_webview != null) {
            web_instance.initialize_engine(news_webview);
        } else {
            System.err.println("WebView is not yet initialized! Check your FXML.");
        }

        if (text_output_area != null) {
            staticTxtArea = text_output_area;
            new ConsoleRedirect(text_output_area);
        } else {
            System.err.println("Text output area is not initialized!");
        }
    }
}