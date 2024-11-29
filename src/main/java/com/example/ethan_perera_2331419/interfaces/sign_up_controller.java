package com.example.ethan_perera_2331419.interfaces;

import com.example.ethan_perera_2331419.db.SQL_Driver;
import com.example.ethan_perera_2331419.models.user;
import com.example.ethan_perera_2331419.scene_switcher_service;
import com.example.ethan_perera_2331419.services.fundamental_tools;
import com.example.ethan_perera_2331419.services.store_user_details;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class sign_up_controller extends fundamental_tools {
    //------FXML Loaders------
    @FXML
    private TextField new_username_input;
    @FXML
    private TextField new_password_input;
    @FXML
    private TextField new_re_password_input;
    //------Object Initializers------
    private final store_user_details current_user = new store_user_details();
    private final SQL_Driver SQL_obj = new SQL_Driver();
    private final scene_switcher_service scene_switcher = new scene_switcher_service();
    //------Variable Initializer------
    private final String global_username = current_user.getInstance().getGlobalDetails();
    //------Scene Switchers------
    public void switchToSignIn(ActionEvent event) throws IOException{
        scene_switcher.switch_scene(event, "sign_in.fxml");
    }
    //------Controller Functions------
    public void sign_up(ActionEvent event) throws IOException {
        String new_username = new_username_input.getText();
        String new_password = new_password_input.getText();
        String re_entered_password = new_re_password_input.getText();
        String[] password_validated_array = validatePassword(new_password);
        boolean password_validated = !Objects.equals(password_validated_array[0], "false");

        if (Objects.equals(new_username, "") || Objects.equals(new_password, "")) {
            showAlert("Missing Information", "Make sure you fill in the username and password field", Alert.AlertType.INFORMATION);
        } else if (Objects.equals(re_entered_password, "")) {
            showAlert("Missing Information", "Ensure that you re-enter your password", Alert.AlertType.INFORMATION);
        } else if (!Objects.equals(new_password, re_entered_password)) {
            showAlert("Incorrect Password!", "Ensure that the password you re-entered matches the other", Alert.AlertType.ERROR);
        } else if (!password_validated) {
            showAlert("Password complexity", password_validated_array[1], Alert.AlertType.ERROR);
        } else {
            user new_user = new user(new_username,new_password,true);
            if (new_user.add_user()) {
                showAlert("Success", "User registered successfully.", Alert.AlertType.INFORMATION);
                switchToSignIn(event);
            } else {
                showAlert("Error", "Failed to register user. Username might already exist.", Alert.AlertType.ERROR);
            }
        }
    }

    //------Exit Program------
    public void exit_program() {
        remove_from_logged_in_users(global_username,SQL_obj);
        clearSessionCredentials(global_username);
        System.exit(1);
    }
}
