package com.example.ethan_perera_2331419;

import com.example.ethan_perera_2331419.services.fundamental_tools;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class News_App_Driver extends Application{

    private fundamental_tools services = new fundamental_tools();

    @Override
    public void start(Stage primaryStage) {
        Stage stage = new Stage();

        File directory = new File("user_cache");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(News_App_Driver.class.getResource("sign_in.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);

            stage.setTitle("News App");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setOnCloseRequest(e->{
                services.showAlert("Exit Cancelled","You can only use the exit button", Alert.AlertType.ERROR);
                e.consume();
            });
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load the application.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}