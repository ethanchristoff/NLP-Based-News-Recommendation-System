package com.example.ethan_perera_2331419.models;

import com.example.ethan_perera_2331419.db.SQL_Driver;
import com.example.ethan_perera_2331419.services.fundamental_tools;
import com.example.ethan_perera_2331419.services.store_user_details;
import com.google.gson.JsonObject;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class user extends fundamental_tools {
    //------Variable Loaders------
    private String inputUsername;
    private String inputPassword;
    private boolean GUIalert;
    //------SQL Based Variables------
    private PreparedStatement pstmt;
    private ResultSet rs;
    private String sql;
    //------Object Initializers------
    private final SQL_Driver SQL_obj = new SQL_Driver();
    private final rate rate_service = new rate();
    //------Constructor Initialization------
    public user(String inputUsername, String inputPassword, boolean GUIalert){
        this.inputUsername = inputUsername;
        this.inputPassword = inputPassword;
        this.GUIalert = GUIalert;
    }

    public user(String inputUsername, boolean GUIalert){
        this.inputUsername = inputUsername;
        this.inputPassword = "";
        this.GUIalert = GUIalert;
    }
    //------User Functions------
    public boolean authenticateUser() {
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
                if (GUIalert){
                    showAlert("Success", "User authenticated: " + authenticatedUsername, Alert.AlertType.INFORMATION);
                }else{
                    System.out.println("Success! The user has been authenticated: " + authenticatedUsername);
                }
                return true;
            } else {
                if (GUIalert){
                    showAlert("Error", "Invalid username or password.", Alert.AlertType.ERROR);
                }else {
                    System.out.println("Error the username or password is invalid");
                }
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (GUIalert){
                showAlert("Error", "Invalid username or password.", Alert.AlertType.ERROR);
            }else{
                System.out.println("Error the username or password is invalid");
            }
            return false;
        } finally {
            SQL_obj.closeResources();
            SQL_obj.close_connection();
        }
    }

    public boolean add_user() {
        boolean userAdded = false;
        SQL_obj.open_connection();

        try {
            String sql = "SELECT COUNT(*) FROM users WHERE username = ?;";
            SQL_obj.set_query(sql);
            PreparedStatement selectPstmt = SQL_obj.getPreparedStatement();
            ResultSet rs = null;

            try {
                selectPstmt.setString(1, inputUsername);
                rs = selectPstmt.executeQuery();

                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Error: Username already exists. Please choose another.");
                } else {
                    // Insert the new user into the users table
                    sql = "INSERT INTO users (username, password) VALUES (?, ?);";
                    SQL_obj.set_query(sql);
                    PreparedStatement insertPstmt = SQL_obj.getPreparedStatement();

                    try {
                        insertPstmt.setString(1, inputUsername);
                        insertPstmt.setString(2, inputPassword);
                        insertPstmt.executeUpdate();

                        System.out.println("Success: User registered successfully.");
                        userAdded = true;
                    } finally {
                        insertPstmt.close();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error: Database connection error.");
            } finally {
                if (rs != null) rs.close();
                if (selectPstmt != null) selectPstmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQL_obj.closeResources();
            SQL_obj.close_connection();
        }

        return userAdded;
    }

    public boolean reset_user_password(String resetRePassword){
        boolean reset_password = false;
        String checkSql = "SELECT username FROM users WHERE username = ? AND password = ?";
        SQL_obj.set_query(checkSql);
        pstmt = SQL_obj.getPreparedStatement();

        try {
            pstmt.setString(1, inputUsername);
            pstmt.setString(2, inputPassword);
            rs = SQL_obj.get_ResultSet(pstmt);

            if (rs.next()) {
                String updateSql = "UPDATE users SET password = ? WHERE username = ?";
                SQL_obj.set_query(updateSql);
                pstmt = SQL_obj.getPreparedStatement();

                pstmt.setString(1, resetRePassword);
                pstmt.setString(2, inputUsername);
                pstmt.executeUpdate();

                reset_password = true;
                if (GUIalert){
                    showAlert("Success", "Password has been reset successfully.", Alert.AlertType.INFORMATION);
                }else{
                    System.out.println("Success, the password has been reset successfully,");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (GUIalert){
                showAlert("Error", "Database connection error.", Alert.AlertType.ERROR);
            }else {
                System.out.println("Error, there was a database connection error");
            }
        } finally {
            SQL_obj.closeResources();
            SQL_obj.close_connection();
        }

        return reset_password;
    }

    public String show_personal_details(){
        sql = "SELECT Read_Articles, Preferred_Genres  FROM users WHERE username = ? AND password = ?";
        SQL_obj.set_query(sql);
        pstmt = SQL_obj.getPreparedStatement();
        String return_string = "";
        try {

            pstmt.setString(1, inputUsername);
            pstmt.setString(2, inputPassword);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                String read_articles = rs.getString("Read_Articles");
                String preferred_genres = rs.getString("Preferred_Genres");
                return_string = "Most Recently Read Article: "+read_articles+"\nPreferred Genres: "+preferred_genres+"\n\nTo view your liked articles view your personal details page!";
            } else {
                if (GUIalert){
                    showAlert("Error", "Invalid username or password.", Alert.AlertType.ERROR);
                }else{
                    System.out.println("Error invalid username or password");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (GUIalert){
                showAlert("Error", "Database connection error.", Alert.AlertType.ERROR);
            }else{
                System.out.println("Error there was a database connection error");
            }
        }

        return return_string;
    }

    public boolean clear_user_details(String filePath){
        boolean user_details_cleared = false;
        try (FileWriter writer = new FileWriter(filePath, false)) {
            writer.write("");
            user_details_cleared = true;
        } catch (IOException e) {
            if (GUIalert){
                showAlert("File Error","An error occurred while clearing the file: " + e.getMessage(), Alert.AlertType.INFORMATION);
            }else{
                System.out.println("File error, an error occurred while clearing the file");
            }
        }
        return user_details_cleared;
    }

    public String readLikedArticlesFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        } catch (IOException e) {
            return null;
        }
    }

    public void likesArticle(String description, String userName, Button likeButton) {
        rate_service.likeArticle(description,userName,likeButton);
    }

    public void skipsArticle(String userName, JsonObject article, String globalUsername) {
        rate_service.skipArticle(userName,article,globalUsername);
    }
}