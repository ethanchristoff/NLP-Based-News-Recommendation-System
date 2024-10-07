package com.example.ethan_perera_2331419;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

class ConsoleRedirect {
    private final TextArea textArea;
    private final PrintStream consoleStream;

    public ConsoleRedirect(TextArea textArea) {
        this.textArea = textArea;
        consoleStream = System.out;
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) {
                appendText(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) {
                appendText(new String(b, off, len));
            }

            @Override
            public void write(byte[] b) {
                write(b, 0, b.length);
            }
        };
        PrintStream customStream = new PrintStream(out, true);
        System.setOut(customStream);
        System.setErr(customStream);
    }

    private void appendText(String text) {
        Platform.runLater(() -> textArea.appendText(text));
    }

    public void restoreConsole() {
        System.setOut(consoleStream);
        System.setErr(consoleStream);
    }
}

public class news_app_controller implements Initializable{

    public static TextArea staticTxtArea;
    private Stage stage;
    private Scene scene;
    private Parent root;

    public void exit_program(){
        System.exit(1);
    }

    public void swicthToMain(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("text_area_test.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void say_hello(){
        System.out.println("Hello!");
    }

    @FXML
    private TextArea text_output_area;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        staticTxtArea = text_output_area;
        new ConsoleRedirect(text_output_area);
        staticTxtArea = text_output_area;
    }
}