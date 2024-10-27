package com.example.ethan_perera_2331419;

import javafx.scene.control.Alert;

import java.io.*;

public class fundamental_tools {
    public void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void saveSessionCredentials(String username, String password) {
        String filename = "session_logs.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("username=" + username + "\n");
            writer.write("password=" + password + "\n");
            System.out.println("Session saved to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save session.", Alert.AlertType.ERROR);
        }
    }

    public void clearSessionCredentials() {
        String filename = "session_logs.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("");
            System.out.println("Session log cleared.");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to clear session.", Alert.AlertType.ERROR);
        }
    }

    public String[] readSessionCredentials() {
        String filename = "session_logs.txt";
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

}
