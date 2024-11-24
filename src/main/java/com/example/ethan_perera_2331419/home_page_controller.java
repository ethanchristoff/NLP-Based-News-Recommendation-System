package com.example.ethan_perera_2331419;

import com.example.ethan_perera_2331419.db.SQL_Driver;
import com.example.ethan_perera_2331419.models.user;
import com.example.ethan_perera_2331419.models.recommend;
import com.example.ethan_perera_2331419.services.RecommendationEngine;
import com.example.ethan_perera_2331419.services.fundamental_tools;
import com.example.ethan_perera_2331419.services.store_user_details;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.concurrent.Task;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;
import java.util.ResourceBundle;

public class home_page_controller extends fundamental_tools implements Initializable {
    //------FXML Loaders------
    @FXML
    private Label text_output_area;
    @FXML
    private Button see_details_btn;
    @FXML
    private Button recommended_articles_btn;
    @FXML
    private Button personal_details_btn;
    @FXML
    private Button article_viewer_btn;
    @FXML
    private Button previous_page_btn;
    @FXML
    private Label logged_in_user;

    //------Variable Loaders------
    private String global_username = "";
    private boolean is_temp_user;
    private String summarized_genre;
    //------SQL Based Variables------
    private String sql;
    private PreparedStatement pstmt;
    //------Object Initializers------
    private final SQL_Driver SQL_obj = new SQL_Driver();
    private final store_user_details current_user = new store_user_details();
    private final store_user_details temp_creds = new store_user_details();
    private final RecommendationEngine genre_specifier = new RecommendationEngine();

    //------Scene Switchers------
    public void switchToSignIn(ActionEvent event) throws IOException {
        scene_switcher(event, "sign_in.fxml");
    }

    public void switchToUserPage(ActionEvent event) throws IOException{
        if (is_temp_user) {
            showAlert("Temp User","Ensure that you login to use this feature", Alert.AlertType.ERROR);showAlert("Temp User","Ensure that you login to use this feature", Alert.AlertType.ERROR);showAlert("Temp User","Ensure that you login to use this feature", Alert.AlertType.ERROR);
            personal_details_btn.setDisable(true);
            return;
        }
        scene_switcher(event, "user_page.fxml");
    }

    public void switchToArticleViewer(ActionEvent event) throws IOException {
        scene_switcher(event, "view_article_page.fxml");
    }

    public void switchToReommendedArticleViewer(ActionEvent event) {
        if (is_temp_user) {
            showAlert("Temp User", "Ensure that you login to use this feature", Alert.AlertType.ERROR);
            recommended_articles_btn.setDisable(true);
            return;
        }

        if (file_exists("user_cache/" + global_username + "_liked_articles.txt")) {
            showAlert("Loading Genre", "Please wait as the model is summarizing your genre. This may take some time!", Alert.AlertType.INFORMATION);

            Task<Void> ollamaTask = new Task<>() {
                @Override
                protected Void call() {
                    try {
                        summarized_genre = genre_specifier.getResponse("user_cache/" + global_username + "_liked_articles.txt");
                        recommend new_recommended_article = new recommend();
                        new_recommended_article.set_preferred_genre(summarized_genre, global_username);
                    } catch (IOException e) {
                        e.printStackTrace();
                        updateMessage("Error in processing genre.");
                    }
                    return null;
                }
            };

            ollamaTask.setOnSucceeded(event1 -> {
                try {
                    showAlert("Preferred Genre", "Your preferred Genre is: '" + summarized_genre + "'", Alert.AlertType.INFORMATION);
                    scene_switcher(event, "view_recommended_articles_page.fxml");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            ollamaTask.setOnFailed(event1 -> {
                showAlert("Error", "Failed to summarize genre.", Alert.AlertType.ERROR);
            });

            ollamaTask.setOnRunning(event1 -> {
                // Prevent the user from switching stages but enables them to interact with page they are in
                recommended_articles_btn.setDisable(true);
                personal_details_btn.setDisable(true);
                article_viewer_btn.setDisable(true);
                previous_page_btn.setDisable(true);
            });

            // Start the task asynchronously
            new Thread(ollamaTask).start();

        } else {
            recommended_articles_btn.setDisable(true);
            showAlert("File Not Found", "Ensure that you've liked some articles first before you run this!", Alert.AlertType.INFORMATION);
        }
    }

    //------Controller Functions------
    public void print_user_details() {
        if (is_temp_user){
            showAlert("Temp User","Ensure that you login to use this feature", Alert.AlertType.ERROR);
            see_details_btn.setDisable(true);
            return;
        }
        String[] sessionData = readSessionCredentials(global_username);

        sql = "SELECT Read_Articles, Preferred_Genres  FROM users WHERE username = ? AND password = ?";
        SQL_obj.set_query(sql);
        pstmt = SQL_obj.getPreparedStatement();

        user User = new user(sessionData[0],sessionData[1]);
        text_output_area.setText(User.show_personal_details());

        SQL_obj.closeResources();
        SQL_obj.close_connection();
        see_details_btn.setDisable(true);
    }
    //------Exit Program------

    public void exit_program() {
        remove_from_logged_in_users(global_username,SQL_obj);
        clearSessionCredentials(global_username);
        System.exit(1);
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        global_username = current_user.getInstance().getGlobalDetails();
        is_temp_user = Objects.equals(temp_creds.getInstance().getGlobalDetails(), "temp");
        if (!is_temp_user) {
            logged_in_user.setText("Logged in as: " + global_username);
        }else{
            logged_in_user.setText("Logged in as temp user");
        }
    }
}
