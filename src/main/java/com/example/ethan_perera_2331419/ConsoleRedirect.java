package com.example.ethan_perera_2331419;

import javafx.application.Platform;
import javafx.scene.control.*;
import java.io.*;

class ConsoleRedirect {
    private final TextArea textArea;
    public static TextArea staticTxtArea;

    public ConsoleRedirect(TextArea textArea) {
        this.textArea = textArea;
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
}