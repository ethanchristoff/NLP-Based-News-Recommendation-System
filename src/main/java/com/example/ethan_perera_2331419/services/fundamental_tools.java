package com.example.ethan_perera_2331419.services;

import com.example.ethan_perera_2331419.db.SQL_Driver;
import javafx.scene.control.Alert;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class fundamental_tools {
    //------Object Initializers------
    private static ExecutorService executor;
    //------Fundamental_tools functions------
    public void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void saveSessionCredentials(String username, String password) {
        String filename = "user_cache/"+username+"_session_logs.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("username=" + username + "\n");
            writer.write("password=" + password + "\n");
            System.out.println("Session saved to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save session.", Alert.AlertType.ERROR);
        }
    }

    public void clearSessionCredentials(String username) {
        String filename = "user_cache/"+username+"_session_logs.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("");
            System.out.println("Session log cleared.");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to clear session.", Alert.AlertType.ERROR);
        }
    }

    public String[] readSessionCredentials(String username) {
        String filename = "user_cache/"+username+"_session_logs.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            String[] sessionData = new String[2];  // [username, password]
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("username=")) {
                    sessionData[0] = line.split("=")[1];
                } else if (line.startsWith("password=")) {
                    sessionData[1] = line.split("=")[1];
                }
            }
            return sessionData;
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to read session.", Alert.AlertType.ERROR);
            return null;
        }
    }

    public String[] validatePassword(String password) {
        String[] result = new String[2];
        int minLength = 3;
        int maxLength = 12;
        int minNumericValues = 3;

        if (password.length() < minLength || password.length() > maxLength) {
            result[0] = "false";
            result[1] = "The password length must be greater than " + minLength +
                    " and less than or equal to " + maxLength;
            return result;
        }

        int numericValuesCount = 0;
        boolean hasSpecialChar = false;

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isDigit(c)) {
                numericValuesCount++;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecialChar = true;
            }
        }

        if (numericValuesCount < minNumericValues) {
            result[0] = "false";
            result[1] = "The password must contain at least " + minNumericValues + " numbers.";
        } else if (!hasSpecialChar) {
            result[0] = "false";
            result[1] = "The password must contain at least one special character.";
        } else {
            result[0] = "true";
            result[1] = "Password is valid.";
        }

        return result;
    }

    public boolean file_exists(String filePath){
        File file = new File(filePath);
        return file.exists() && file.isFile() && file.length() > 0;
    }

    public void write_to_text_file(String fileName, String articleTitle) {
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.append(articleTitle).append("\n-------------------\n");
        } catch (IOException e) {
            showAlert("Error", "Failed to save the article description.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    public String[] readHeadersFromTextFile(String fileName) {
        List<String> headers = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            StringBuilder header = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.equals("-------------------")) {
                    headers.add(header.toString().trim());
                    header.setLength(0);
                } else {
                    header.append(line).append("\n");
                }
            }

            if (header.length() > 0) {
                headers.add(header.toString().trim());
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to read article headers from file.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }

        return headers.toArray(new String[0]);
    }

    public void add_to_logged_in_users(String username, SQL_Driver SQL_obj){
        SQL_obj.open_connection();
        String sql = "INSERT INTO logged_in_users (username) VALUES (?)";
        SQL_obj.set_query(sql);
        PreparedStatement pstmt = SQL_obj.getPreparedStatement();

        try {
            pstmt.setString(1, username);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database connection error.", Alert.AlertType.ERROR);
        } finally {
            SQL_obj.closeResources();
            SQL_obj.close_connection();
        }
    }

    public boolean check_logged_in_users(String username, SQL_Driver SQL_obj) {
        SQL_obj.open_connection();
        String sql = "SELECT COUNT(*) FROM logged_in_users WHERE username = ?";

        SQL_obj.set_query(sql);
        PreparedStatement pstmt = SQL_obj.getPreparedStatement();

        try {
            pstmt.setString(1, username);
            ResultSet rs = SQL_obj.get_ResultSet(pstmt);

            if (rs.next()) {
                int userCount = rs.getInt(1);
                return userCount > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database connection error.", Alert.AlertType.ERROR);
        } finally {
            SQL_obj.closeResources();
            SQL_obj.close_connection();
        }

        return false;
    }

    public void remove_from_logged_in_users(String username, SQL_Driver SQL_obj){
        SQL_obj.open_connection();
        String sql = "DELETE FROM logged_in_users WHERE username = ?";
        SQL_obj.set_query(sql);
        PreparedStatement pstmt = SQL_obj.getPreparedStatement();

        try {
            pstmt.setString(1, username);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database connection error.", Alert.AlertType.ERROR);
        } finally {
            SQL_obj.closeResources();
            SQL_obj.close_connection();
        }
    }

    public static void run_simultaneously(List<Runnable> tasks) {
        executor = Executors.newFixedThreadPool(tasks.size());

        for (Runnable task : tasks) {
            executor.submit(task);
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                System.out.println("Executor shutdown");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}