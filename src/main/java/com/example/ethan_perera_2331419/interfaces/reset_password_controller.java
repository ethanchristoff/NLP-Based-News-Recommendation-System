package com.example.ethan_perera_2331419.interfaces;

import com.example.ethan_perera_2331419.db.SQL_Driver;
import com.example.ethan_perera_2331419.models.user;
import com.example.ethan_perera_2331419.scene_switcher_service;
import com.example.ethan_perera_2331419.services.fundamental_tools;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class reset_password_controller extends fundamental_tools {
    //------FXML Loaders------
    @FXML
    private TextField reset_pword_username_input;
    @FXML
    private TextField reset_password_input;
    @FXML
    private TextField reset_re_password_input;
    //------Object Initializers------
    private final SQL_Driver SQL_obj = new SQL_Driver();
    private final scene_switcher_service scene_switcher = new scene_switcher_service();
    //------Scene Switchers------
    public void switchToSignIn(ActionEvent event) throws IOException {
        scene_switcher.switch_scene(event, "sign_in.fxml");
    }
    //------Controller Functions------
    public void resetPassword(ActionEvent event) throws IOException {
        SQL_obj.open_connection();
        String resetUsername = reset_pword_username_input.getText();
        String resetPassword = reset_password_input.getText();
        String resetRePassword = reset_re_password_input.getText();
        String[] password_validated_array = validatePassword(resetPassword);
        boolean password_valiadated = !Objects.equals(password_validated_array[0], "false");

        if (Objects.equals(resetUsername, "") || Objects.equals(resetPassword, "") || Objects.equals(resetRePassword, "")) {
            showAlert("Missing Information", "Ensure that you fill all the fields!", Alert.AlertType.INFORMATION);
        }else if (!password_valiadated) {
            showAlert("Password complexity",password_validated_array[1], Alert.AlertType.ERROR);
        }else {
            user User = new user(resetUsername,resetPassword,true);
            boolean reset_password = User.reset_user_password(resetRePassword);
            if (reset_password){
                remove_from_logged_in_users(resetUsername,SQL_obj);
                clearSessionCredentials(resetUsername);
                switchToSignIn(event);
            }else {
                showAlert("Error", "Invalid username or current password.", Alert.AlertType.ERROR);
            }
        }
    }
    //------Exit Program------
    public void exit_program() {
        System.exit(1);
    }
}
