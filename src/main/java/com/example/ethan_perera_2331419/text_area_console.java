package com.example.ethan_perera_2331419;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.Objects;
public class text_area_console extends Application {
    private final PipedInputStream pipeIn = new PipedInputStream();
    private final PipedInputStream pipeIn2 = new PipedInputStream();
    Thread errorThrower;
    private Thread reader;
    private Thread reader2;
    boolean quit;
    private TextArea txtArea;
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("home_page.fxml")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        txtArea = ConsoleRedirect.staticTxtArea;
        executeReaderThreads();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                closeThread();
                Platform.exit();
                System.exit(0);
            }
        });
    }
    //method to handle thread closing on stage closing
    synchronized void closeThread()
    {
        System.out.println("Message: Stage is closed.");
        this.quit = true;
        notifyAll();
        try { this.reader.join(1000L); this.pipeIn.close(); } catch (Exception e) {
        }try { this.reader2.join(1000L); this.pipeIn2.close(); } catch (Exception e) {
    }System.exit(0);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    public void executeReaderThreads()
    {
        try
        {
            PipedOutputStream pout = new PipedOutputStream(this.pipeIn);
            System.setOut(new PrintStream(pout, true));
        }
        catch (IOException | SecurityException ignored)
        { }
        try
        {
            PipedOutputStream pout2 = new PipedOutputStream(this.pipeIn2);
            System.setErr(new PrintStream(pout2, true));
        }
        catch (IOException | SecurityException ignored)
        {
        }
        ReaderThread obj = new ReaderThread(pipeIn, pipeIn2, errorThrower, reader, reader2, quit, txtArea);
    }
}