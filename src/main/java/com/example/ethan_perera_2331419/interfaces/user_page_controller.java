package com.example.ethan_perera_2331419.interfaces;

import com.example.ethan_perera_2331419.db.SQL_Driver;
import com.example.ethan_perera_2331419.models.user;
import com.example.ethan_perera_2331419.scene_switcher_service;
import com.example.ethan_perera_2331419.services.fundamental_tools;
import com.example.ethan_perera_2331419.services.store_user_details;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class user_page_controller extends fundamental_tools implements Initializable {
    //------FXML Loaders------
    @FXML
    private Button clear_articles_btn;
    @FXML
    private Button view_liked_articles_btn;
    @FXML
    private ScrollPane user_details_content;
    //------Variable Loaders------
    private String global_username = "";
    //------Object Initializers------
    private final SQL_Driver SQL_obj = new SQL_Driver();
    private final store_user_details active_user_stored_details = new store_user_details();
    private final user active_user = new user(global_username,true);
    private final scene_switcher_service scene_switcher = new scene_switcher_service();
    //------Scene Switchers------
    public void switchToHome_Not_Validated(ActionEvent event) throws IOException{
        scene_switcher.switch_scene(event, "home_page.fxml");
    }
    //------Controller Functions------
    public void clear_details(){
        clear_articles_btn.setDisable(false);
        String filePath = "user_cache/"+global_username+"_liked_articles.txt";
        if (active_user.clear_user_details(filePath)){
            showAlert("File CLeared","Contents of the previously liked articles successfully cleared", Alert.AlertType.INFORMATION);
        }
    }

    public void print_liked_articles() {
        view_liked_articles_btn.setDisable(false);

        String filePath = "user_cache/" + global_username + "_liked_articles.txt";
        String likedArticlesContent = active_user.readLikedArticlesFromFile(filePath);

        if (likedArticlesContent != null && !likedArticlesContent.isEmpty()) {
            displayContentInScrollPane(likedArticlesContent);
            view_liked_articles_btn.setDisable(true);
        } else {
            showAlert("No Liked Articles", "No liked articles found. Maybe like a couple and come back?", Alert.AlertType.INFORMATION);
            view_liked_articles_btn.setDisable(true);
        }
    }

    private void displayContentInScrollPane(String content) {
        VBox articleBox = new VBox(10); // Sets the spacing set directly in the constructor
        Label contentLabel = new Label(content);
        contentLabel.setWrapText(true);

        articleBox.getChildren().add(contentLabel);
        user_details_content.setContent(articleBox);
        user_details_content.setFitToWidth(true);
    }
    //------Exit Program------
    public void exit_program() {
        remove_from_logged_in_users(global_username,SQL_obj);
        clearSessionCredentials(global_username);
        System.exit(1);
    }
    @Override
    public void initialize(URL url, ResourceBundle rb){
        global_username = active_user_stored_details.getInstance().getGlobalDetails();
    }
}