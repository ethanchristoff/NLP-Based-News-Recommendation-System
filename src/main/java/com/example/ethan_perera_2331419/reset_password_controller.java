package com.example.ethan_perera_2331419;

import com.example.ethan_perera_2331419.db.SQL_Driver;
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
    //------SQL Based Variables------
    private ResultSet rs;
    private PreparedStatement pstmt;
    //------Object Initializers------
    private final SQL_Driver SQL_obj = new SQL_Driver();
    //------Scene Switchers------
    public void switchToSignIn(ActionEvent event) throws IOException {
        scene_switcher(event, "sign_in.fxml");
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
            String checkSql = "SELECT username FROM users WHERE username = ? AND password = ?";
            SQL_obj.set_query(checkSql);
            pstmt = SQL_obj.getPreparedStatement();

            try {
                pstmt.setString(1, resetUsername);
                pstmt.setString(2, resetPassword);
                rs = SQL_obj.get_ResultSet(pstmt);

                if (rs.next()) {
                    String updateSql = "UPDATE users SET password = ? WHERE username = ?";
                    SQL_obj.set_query(updateSql);
                    pstmt = SQL_obj.getPreparedStatement();

                    pstmt.setString(1, resetRePassword);
                    pstmt.setString(2, resetUsername);
                    pstmt.executeUpdate();

                    showAlert("Success", "Password has been reset successfully.", Alert.AlertType.INFORMATION);
                    switchToSignIn(event);
                } else {
                    // Username or password did not match
                    showAlert("Error", "Invalid username or current password.", Alert.AlertType.ERROR);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Database connection error.", Alert.AlertType.ERROR);
            } finally {
                SQL_obj.closeResources();
                SQL_obj.close_connection();
            }
        }
    }
    //------Exit Program------
    public void exit_program() {
        System.exit(1);
    }
}
