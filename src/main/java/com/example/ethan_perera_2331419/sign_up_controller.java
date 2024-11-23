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

public class sign_up_controller extends fundamental_tools {
    //------FXML Loaders------
    @FXML
    private TextField new_username_input;
    @FXML
    private TextField new_password_input;
    @FXML
    private TextField new_re_password_input;
    //------SQL Based Variables------
    private PreparedStatement selectPstmt;
    private ResultSet rs;
    private String sql;
    //------Object Initializers------
    private final SQL_Driver SQL_obj = new SQL_Driver();
    //------Scene Switchers------
    public void switchToSignIn(ActionEvent event) throws IOException{
        scene_switcher(event, "sign_in.fxml");
    }
    //------Controller Functions------
    public void sign_up(ActionEvent event) throws IOException {
        SQL_obj.open_connection();
        String new_username = new_username_input.getText();
        String new_password = new_password_input.getText();
        String re_entered_password = new_re_password_input.getText();
        String[] password_validated_array = validatePassword(new_password);
        boolean password_valiadated = !Objects.equals(password_validated_array[0], "false");

        if (Objects.equals(new_username, "") || Objects.equals(new_password, "")) {
            showAlert("Missing Information", "Make sure you fill in the username and password field", Alert.AlertType.INFORMATION);
        } else if (Objects.equals(re_entered_password, "")) {
            showAlert("Missing Information", "Ensure that you re-enter your password", Alert.AlertType.INFORMATION);
        } else if (!Objects.equals(new_password, re_entered_password)) {
            showAlert("Incorrect Password!", "Ensure that the password you re-entered matches the other", Alert.AlertType.ERROR);
        } else if (!password_valiadated) {
            showAlert("Password complexity",password_validated_array[1], Alert.AlertType.ERROR);
        }else {
            sql = "SELECT COUNT(*) FROM users WHERE username = ?;";
            SQL_obj.set_query(sql);
            selectPstmt = SQL_obj.getPreparedStatement();

            try {
                selectPstmt.setString(1, new_username);
                rs = selectPstmt.executeQuery();

                if (rs.next() && rs.getInt(1) > 0) {
                    showAlert("Error", "Username already exists. Please choose another.", Alert.AlertType.ERROR);
                } else {
                    sql = "INSERT INTO users (username, password) VALUES (?, ?);";
                    SQL_obj.set_query(sql);
                    PreparedStatement insertPstmt = SQL_obj.getPreparedStatement();

                    try {
                        insertPstmt.setString(1, new_username);
                        insertPstmt.setString(2, new_password);
                        insertPstmt.executeUpdate();

                        showAlert("Success", "User registered successfully.", Alert.AlertType.INFORMATION);
                        switchToSignIn(event);
                    } finally {
                        insertPstmt.close();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Database connection error.", Alert.AlertType.ERROR);
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (selectPstmt != null) selectPstmt.close();
                    SQL_obj.closeResources();
                    SQL_obj.close_connection();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //------Exit Program------
    public void exit_program() {
        System.exit(1);
    }
}
