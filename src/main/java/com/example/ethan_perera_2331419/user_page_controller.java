package com.example.ethan_perera_2331419;

import com.example.ethan_perera_2331419.db.SQL_Driver;
import com.example.ethan_perera_2331419.services.fundamental_tools;
import com.example.ethan_perera_2331419.services.store_details;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
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
    public static TextArea staticTxtArea;
    private String global_username = "";
    private final SQL_Driver SQL_obj = new SQL_Driver();
    //------Object Initializers------
    private final store_details current_user = new store_details();
    //------Scene Switchers------
    public void switchToHome_Not_Validated(ActionEvent event) throws IOException{
        scene_switcher(event, "home_page.fxml");
    }
    //------Controller Functions------
    public void clear_details(){
        clear_articles_btn.setDisable(false);
        String filePath = "user_cache/"+global_username+"_liked_articles.txt";
        try (FileWriter writer = new FileWriter(filePath, false)) {
            writer.write("");
            showAlert("File CLeared","Contents of the previously liked articles successfully cleared", Alert.AlertType.INFORMATION);
            clear_articles_btn.setDisable(true);
        } catch (IOException e) {
            showAlert("File Error","An error occurred while clearing the file: " + e.getMessage(), Alert.AlertType.INFORMATION);
        }
    }

    public void print_liked_articles(){
        view_liked_articles_btn.setDisable(false);
        String filePath = "user_cache/"+global_username+"_liked_articles.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            VBox articleBox = new VBox(10); // Sets the spacing set directly in constructor
            Label contentLabel = new Label(content.toString());
            contentLabel.setWrapText(true);

            articleBox.getChildren().add(contentLabel);
            user_details_content.setContent(articleBox);
            user_details_content.setFitToWidth(true);

            view_liked_articles_btn.setDisable(true);
    } catch (IOException e) {
            showAlert("File Error","There was an issue getting your liked articles, maybe like a couple and come back?", Alert.AlertType.INFORMATION);
            view_liked_articles_btn.setDisable(true);
        }
    }
    //------Exit Program------
    public void exit_program() {
        remove_from_logged_in_users(global_username,SQL_obj);
        clearSessionCredentials(global_username);
        System.exit(1);
    }
    @Override
    public void initialize(URL url, ResourceBundle rb){
        global_username = current_user.getInstance().getGlobalDetails();
    }
}