module com.example.ethan_perera_2331419 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.gson;
    requires org.apache.opennlp.tools;
    requires java.net.http;
    requires javafx.web;
    requires org.json;

    exports com.example.ethan_perera_2331419.services;
    opens com.example.ethan_perera_2331419.services to javafx.fxml;
    exports com.example.ethan_perera_2331419.db;
    opens com.example.ethan_perera_2331419 to javafx.fxml;
    exports com.example.ethan_perera_2331419;
}