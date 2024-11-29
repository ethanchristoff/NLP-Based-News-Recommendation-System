package com.example.ethan_perera_2331419;

import com.example.ethan_perera_2331419.services.fundamental_tools;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class scene_switcher_service extends fundamental_tools {
    public void switch_scene(ActionEvent event, String page_name) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(page_name)));
        Stage stage;
        Scene scene;

        if (root == null) {
            showAlert("Error", page_name+" not found!", Alert.AlertType.ERROR);
            return;
        }

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
