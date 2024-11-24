package com.example.ethan_perera_2331419;

import com.example.ethan_perera_2331419.db.SQL_Driver;
import com.example.ethan_perera_2331419.services.fundamental_tools;
import com.example.ethan_perera_2331419.services.store_details;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class sign_in_controller extends fundamental_tools {
    //------FXML Loaders------
    @FXML
    private TextField username_input;
    @FXML
    private TextField password_input;
    //------Variable Loaders------
    private final store_details current_user = new store_details();
    private final store_details temp_creds = new store_details();
    //------SQL Based Variables------
    private PreparedStatement pstmt;
    private ResultSet rs;
    private String sql;
    //------Object Initializers------
    private final SQL_Driver SQL_obj = new SQL_Driver();
    //------Scene Switchers------
    public void switchToSignUp(ActionEvent event) throws IOException {
        scene_switcher(event, "sign_up.fxml");
    }

    public void switchToResetPassword(ActionEvent event) throws IOException {
        scene_switcher(event, "reset_password.fxml");
    }

    public void switchToHome_Temp_Creds(ActionEvent event) throws IOException{
        temp_creds.getInstance().setGlobalDetails("temp");
        scene_switcher(event, "home_page.fxml");
    }

    public void switchToHome_Validated(ActionEvent event) throws IOException {
        String inputUsername = username_input.getText();
        String inputPassword = password_input.getText();

        current_user.getInstance().setGlobalDetails(inputUsername);

        if (Objects.equals(inputUsername, "") || Objects.equals(inputPassword, "")) {
            showAlert("Error", "Input Fields Empty!", Alert.AlertType.ERROR);
        }else if(check_logged_in_users(inputUsername,SQL_obj)){
            showAlert("Session Error","That user is already logged in!", Alert.AlertType.ERROR);
        }else {
            if (authenticateUser(inputUsername, inputPassword)) {
                add_to_logged_in_users(inputUsername,SQL_obj);
                saveSessionCredentials(inputUsername, inputPassword);
                scene_switcher(event, "home_page.fxml");
            }
        }
    }
    //------Controller Functions------
    private boolean authenticateUser(String inputUsername, String inputPassword) {
        SQL_obj.open_connection();

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
    //------Exit Program------
    public void exit_program() {
        System.exit(1);
    }
}
