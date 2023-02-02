module com.example.crc_javafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.crc_javafx to javafx.fxml;
    exports com.example.crc_javafx;
}