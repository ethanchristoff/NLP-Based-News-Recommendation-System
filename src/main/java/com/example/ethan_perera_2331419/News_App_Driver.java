package com.example.ethan_perera_2331419;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class News_App_Driver extends Application{

    private static int window_count;

    @Override
    public void start(Stage primaryStage) {
        for (int i = 0; i < window_count; i++) {
            openNewWindow(i);
        }
    }

    private void openNewWindow(int no_of_windows) {
        int window_id = no_of_windows+1;
        Stage stage = new Stage();

        File directory = new File("user_cache");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(News_App_Driver.class.getResource("sign_in.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);

            stage.setTitle("News App - Window: " + window_id);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load the application.");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the number of application windows you would like to run:");
        News_App_Driver.window_count = sc.nextInt();
        sc.close();
        launch(args);
    }
}