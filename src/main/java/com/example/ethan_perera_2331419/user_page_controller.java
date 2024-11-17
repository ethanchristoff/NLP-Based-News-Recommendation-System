package com.example.ethan_perera_2331419;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

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
    private TextArea text_output_area;
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
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
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
        if (text_output_area != null) {
            text_output_area.clear(); // Ensures that warnings are cleared when the page is loaded
            staticTxtArea = text_output_area;
            new ConsoleRedirect(text_output_area);
        } else {
            System.err.println("Text output area is not initialized!");
        }
    }
}