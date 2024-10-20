package com.example.ethan_perera_2331419;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.HostServices;
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
import javafx.stage.Stage;
import javafx.fxml.Initializable;

import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    public void restoreConsole() {
        System.setOut(consoleStream);
        System.setErr(consoleStream);
    }
}

public class news_app_controller implements Initializable {

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

    public void switchToMain(ActionEvent event) {
        String inputUsername = username_input.getText();
        String inputPassword = password_input.getText();

        if (Objects.equals(inputUsername, "") || Objects.equals(inputPassword, "")) {
            showAlert("Error", "Input Fields Empty!", Alert.AlertType.ERROR);
        } else {
            if (authenticateUser(inputUsername, inputPassword)) {
                saveSession(inputUsername, inputPassword);
                try {
                    root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("text_area_test.fxml")));

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

    public void switchToSignUp(ActionEvent event) throws IOException{
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("sign_up.fxml")));

        if (root == null) {
            showAlert("Error", "Failed to load introduction page!", Alert.AlertType.ERROR);
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
            showAlert("Error", "Failed to load introduction page!", Alert.AlertType.ERROR);
            return;
        }

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToHome(ActionEvent event) throws IOException{
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("text_area_test.fxml")));

        if (root == null) {
            showAlert("Error", "Failed to load introduction page!", Alert.AlertType.ERROR);
            return;
        }

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void exit_program() {
        clearSession();
        System.exit(1);
    }

    private boolean authenticateUser(String inputUsername, String inputPassword) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            final String dbUrl = "jdbc:mysql://localhost:3306/personalized_news_system";
            conn = DriverManager.getConnection(dbUrl);

            String sql = "SELECT username, password FROM users WHERE username = ? AND password = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, inputUsername);
            pstmt.setString(2, inputPassword);
            rs = pstmt.executeQuery();

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
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    TextField new_username_input;
    @FXML
    TextField new_password_input;
    @FXML
    TextField new_re_password_input;

    public void sign_up(ActionEvent event) throws IOException {
        String new_username = new_username_input.getText();
        String new_password = new_password_input.getText();
        String re_entered_password = new_re_password_input.getText();
        if (Objects.equals(new_username, "") || Objects.equals(new_password, "")){
            showAlert("Missing Information", "Make sure you fill in the username and password field", Alert.AlertType.INFORMATION);
        } else if (Objects.equals(re_entered_password, "")) {
            showAlert("Missing Information","Ensure that you re-enter your password",Alert.AlertType.INFORMATION);
        } else if (!Objects.equals(new_password, re_entered_password)) {
            showAlert("Incorrect Password!","Ensure that the password you re-entered matches the other", Alert.AlertType.ERROR);
        } else {
            Connection conn = null;
            PreparedStatement pstmt = null;

            try {
                final String dbUrl = "jdbc:mysql://localhost:3306/personalized_news_system";
                conn = DriverManager.getConnection(dbUrl);

                String sql = "INSERT INTO users (username, password) VALUES (?, ?);\n";

                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, new_username);
                pstmt.setString(2, new_password);

                pstmt.executeUpdate();

                showAlert("Success","Data Entered",Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Database connection error.", Alert.AlertType.ERROR);
            }
            switchToIntro(event);
        }
    }

    private void saveSession(String username, String password) {
        String filename = "session_logs.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("username=" + username + "\n");
            writer.write("password=" + password + "\n");
            System.out.println("Session saved to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save session.", Alert.AlertType.ERROR);
        }
    }

    private void clearSession() {
        String filename = "session_logs.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("");
            System.out.println("Session log cleared.");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to clear session.", Alert.AlertType.ERROR);
        }
    }

    private String[] readSession() {
        String filename = "session_logs.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            String[] sessionData = new String[2];  // [username, password]
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("username=")) {
                    sessionData[0] = line.split("=")[1];
                } else if (line.startsWith("password=")) {
                    sessionData[1] = line.split("=")[1];
                }
            }
            return sessionData;
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to read session.", Alert.AlertType.ERROR);
            return null;
        }
    }

    public void print_user_details() {
        String[] sessionData = readSession();
        if (sessionData == null || sessionData[0] == null || sessionData[1] == null) {
            showAlert("Error", "Session not found. Please login first.", Alert.AlertType.ERROR);
            return;
        }

        final String dbUrl = "jdbc:mysql://localhost:3306/personalized_news_system";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(dbUrl);
            String sql = "SELECT Read_Articles, Liked_Articles, Preferred_Genres  FROM users WHERE username = ? AND password = ?";
            pstmt = conn.prepareStatement(sql);
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
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private final news_api_content news_obj = new news_api_content();
    JsonArray articles = news_obj.test_news_api();
    @FXML
    Label article_header;
    @FXML
    Label article_description;
    @FXML
    Hyperlink article_link;

    int count = 0;
    public void next_article() {
        if (articles != null && articles.size() > 0) {
            count = (count + 1) % articles.size();
            displayArticle(count);
        } else {
            showAlert("No Articles", "No articles to display.", Alert.AlertType.INFORMATION);
        }
    }

    private void displayArticle(int index) {
        JsonObject obj = articles.get(index).getAsJsonObject();
        article_header.setText(obj.get("title").getAsString());
        article_description.setText(obj.get("description").getAsString());
        String url = obj.get("url").getAsString();
        article_link.setText(url);
        article_link.setOnAction(event -> {
            // Copies the link onto the users keyboard
            ClipboardContent content = new ClipboardContent();

            content.putString(url);

            Clipboard clipboard = Clipboard.getSystemClipboard();
            clipboard.setContent(content);

            showAlert("Information","Link copied!", Alert.AlertType.INFORMATION);
        });
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (text_output_area != null) {
            staticTxtArea = text_output_area;
            new ConsoleRedirect(text_output_area);
        } else {
            System.err.println("Text output area is not initialized!");
        }
    }
}