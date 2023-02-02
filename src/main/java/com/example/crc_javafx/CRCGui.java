package com.example.crc_javafx;

import java.util.zip.CRC32;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class CRCGui extends Application {
    @Override
    public void start(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        Label sentPacketLabel = new Label("Sent Packet:");
        GridPane.setConstraints(sentPacketLabel, 0, 0);

        TextField sentPacketField = new TextField();
        GridPane.setConstraints(sentPacketField, 1, 0);

        Label receivedPacketLabel = new Label("Received Packet:");
        GridPane.setConstraints(receivedPacketLabel, 0, 1);

        TextField receivedPacketField = new TextField();
        GridPane.setConstraints(receivedPacketField, 1, 1);

        Label resultLabel = new Label("Result:");
        GridPane.setConstraints(resultLabel, 0, 2);

        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        GridPane.setConstraints(resultArea, 1, 2);

        Button checkButton = new Button("Check");
        GridPane.setConstraints(checkButton, 1, 3);
        checkButton.setOnAction(e -> {
            byte[] sentPacket = sentPacketField.getText().getBytes();
            byte[] receivedPacket = receivedPacketField.getText().getBytes();

            CRC32 sentCRC = new CRC32();
            sentCRC.update(sentPacket);
            long sentChecksum = sentCRC.getValue();

            CRC32 receivedCRC = new CRC32();
            receivedCRC.update(receivedPacket);
            long receivedChecksum = receivedCRC.getValue();

            if (sentChecksum != receivedChecksum) {
                resultArea.setText("Error detected in the received packet." + System.lineSeparator() +
                        "Correcting the error..." + System.lineSeparator());

                boolean corrected = false;

                for (int i = 0; i < receivedPacket.length; i++) {
                    byte[] correctedPacket = receivedPacket.clone();
                    correctedPacket[i] ^= 1;
                    receivedCRC.reset();
                    receivedCRC.update(correctedPacket);
                    long correctedChecksum = receivedCRC.getValue();

                    if (sentChecksum == correctedChecksum) {
                        resultArea.appendText("Single Bit Error corrected successfully!" + System.lineSeparator() +
                                "Corrected Packet: " + new String(correctedPacket) + System.lineSeparator());
                        corrected = true;
                        break;
                    }
                }
                if (!corrected) {
                    resultArea.appendText("Unable to correct the error, multiple-bit/burst error detected." + System.lineSeparator());
                }
            } else {
                resultArea.setText("No error detected in the received packet.");
            }
        });
        grid.getChildren().addAll(sentPacketLabel, sentPacketField,
                receivedPacketLabel, receivedPacketField, resultLabel, resultArea, checkButton);

        Scene scene = new Scene(grid, 700, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("CRC Checker");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

