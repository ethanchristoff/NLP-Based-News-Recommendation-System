package com.example.ethan_perera_2331419.interfaces;

import com.example.ethan_perera_2331419.db.SQL_Driver;
import com.example.ethan_perera_2331419.models.user;
import com.example.ethan_perera_2331419.scene_switcher_service;
import com.example.ethan_perera_2331419.services.fundamental_tools;
import com.example.ethan_perera_2331419.services.store_user_details;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class sign_in_controller extends fundamental_tools implements Initializable {
    //------FXML Loaders------
    @FXML
    private TextField username_input;
    @FXML
    private TextField password_input;
    @FXML
    private Label logged_in_user_label;
    @FXML
    private Button logged_in_user_btn;
    //------Variable Loaders------
    private final store_user_details current_user = new store_user_details();
    private final store_user_details temp_creds = new store_user_details();
    private final String global_username = current_user.getInstance().getGlobalDetails();
    //------Object Initializers------
    private final SQL_Driver SQL_obj = new SQL_Driver();
    private final scene_switcher_service scene_switcher = new scene_switcher_service();
    //------Scene Switchers------
    public void switchToSignUp(ActionEvent event) throws IOException {
        scene_switcher.switch_scene(event, "sign_up.fxml");
    }

    public void switchToResetPassword(ActionEvent event) throws IOException {
        scene_switcher.switch_scene(event, "reset_password.fxml");
    }

    public void switchToHome_Temp_Creds(ActionEvent event) throws IOException{
        temp_creds.getInstance().setGlobalDetails("temp");
        scene_switcher.switch_scene(event, "home_page.fxml");
    }

    public void switchToHome_Validated(ActionEvent event) throws IOException {
        String inputUsername = username_input.getText();
        String inputPassword = password_input.getText();

        if (!Objects.equals(current_user.getInstance().getGlobalDetails(), "Null")){
            remove_from_logged_in_users(global_username,SQL_obj);
            clearSessionCredentials(global_username);
        }

        current_user.getInstance().setGlobalDetails(inputUsername);

        user new_user = new user(inputUsername, inputPassword,true);

        if (Objects.equals(inputUsername, "") || Objects.equals(inputPassword, "")) {
            showAlert("Error", "Input Fields Empty!", Alert.AlertType.ERROR);
        }else if(check_logged_in_users(inputUsername,SQL_obj)){
            showAlert("Session Error","That user is already logged in!", Alert.AlertType.ERROR);
        }else {
            if (new_user.authenticateUser()) {
                add_to_logged_in_users(inputUsername,SQL_obj);
                saveSessionCredentials(inputUsername, inputPassword);
                scene_switcher.switch_scene(event, "home_page.fxml");
            }
        }
    }
    public void switchToHome_Logged_In(ActionEvent event) throws IOException{
        scene_switcher.switch_scene(event, "home_page.fxml");
    }
    //------Exit Program------
    public void exit_program() {
        remove_from_logged_in_users(global_username,SQL_obj);
        clearSessionCredentials(global_username);
        System.exit(1);
    }
    @Override
    public void initialize(URL url, ResourceBundle rb){
        if (!Objects.equals(current_user.getInstance().getGlobalDetails(), "Null")){
            logged_in_user_label.setText("Logged in as: " + current_user.getInstance().getGlobalDetails());
            logged_in_user_btn.setVisible(true);
        }else{
            logged_in_user_btn.setVisible(false);
        }
    }
}
