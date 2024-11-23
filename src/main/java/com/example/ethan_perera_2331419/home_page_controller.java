package com.example.ethan_perera_2331419;

import com.example.ethan_perera_2331419.db.SQL_Driver;
import com.example.ethan_perera_2331419.services.fundamental_tools;
import com.example.ethan_perera_2331419.services.store_details;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    private Label logged_in_user;

    //------Variable Loaders------
    private String global_username = "";
    private boolean is_temp_user;

    //------SQL Based Variables------
    private ResultSet rs;
    private String sql;
    private PreparedStatement pstmt;
    //------Object Initializers------
    private final SQL_Driver SQL_obj = new SQL_Driver();
    private final store_details current_user = new store_details();
    private final store_details temp_creds = new store_details();

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

    public void switchToReommendedArticleViewer(ActionEvent event) throws IOException{
        if (is_temp_user) {
            showAlert("Temp User","Ensure that you login to use this feature", Alert.AlertType.ERROR);
            recommended_articles_btn.setDisable(true);
            return;
        }
        if (file_exists("user_cache/"+global_username+"_liked_articles.txt")) {
            scene_switcher(event, "view_recommended_articles_page.fxml");
        } else {
            recommended_articles_btn.setDisable(true);
            showAlert("File Not Found","Ensure that you've liked some articles first before you run this!", Alert.AlertType.INFORMATION);
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

        try {

            pstmt.setString(1, sessionData[0]);
            pstmt.setString(2, sessionData[1]);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                String read_articles = rs.getString("Read_Articles");
                String preferred_genres = rs.getString("Preferred_Genres");
                text_output_area.setText("Most Recently Read Article: "+read_articles+"\nPreferred Genres: "+preferred_genres+"\n\nTo view your liked articles view your personal details page!");
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
