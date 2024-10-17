module com.example.ethan_perera_2331419 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.ethan_perera_2331419 to javafx.fxml;
    exports com.example.ethan_perera_2331419;
}