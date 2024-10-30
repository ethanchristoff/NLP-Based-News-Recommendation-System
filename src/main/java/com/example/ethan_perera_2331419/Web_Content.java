package com.example.ethan_perera_2331419;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class Web_Content {

    private WebEngine engine;

    public void initialize_engine(WebView webView) {
        if (webView != null) {
            engine = webView.getEngine();
            load_page("https://www.google.com"); // Pre-load the web page
            webView.setManaged(false);
        } else {
            System.err.println("WebView is not initialized!");
        }
    }

    public void reload_page() {
        if (isEngineInitialized()) {
            engine.reload();
        }
    }

    public void load_page(String url) {
        if (isEngineInitialized()) {
            engine.load(url);
        }
    }

    private boolean isEngineInitialized() {
        if (engine == null) {
            System.err.println("WebEngine is not initialized!");
            return false;
        }
        return true;
    }
}
