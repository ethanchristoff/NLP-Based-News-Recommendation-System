package com.example.ethan_perera_2331419.models;

import com.example.ethan_perera_2331419.db.SQL_Driver;
import com.example.ethan_perera_2331419.services.fundamental_tools;
import javafx.scene.control.Alert;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class recommend extends fundamental_tools {
    //------Variable Loaders------

    //------SQL Based Variables------

    //------Object Initializers------
    private final SQL_Driver SQL_obj = new SQL_Driver();
    //------View_Recommended_Articles Functions------
    public void set_preferred_genre(String preferred_genre, String username) {
        String sql = "UPDATE users SET Preferred_Genres = ? WHERE username = ?";
        SQL_obj.set_query(sql);
        PreparedStatement pstmt = SQL_obj.getPreparedStatement();

        try {

            pstmt.setString(1, preferred_genre);
            pstmt.setString(2, username);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database connection error.", Alert.AlertType.ERROR);
        } finally {
            SQL_obj.closeResources();
            SQL_obj.close_connection();
        }
    }
}
