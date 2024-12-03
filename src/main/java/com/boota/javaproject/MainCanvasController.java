package com.boota.javaproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainCanvasController {

    // Function to close the application
    @FXML
    private void close(ActionEvent event) {
        // Get the current stage from the event's source
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();  // Close the stage
    }

    // Function to open the Class Diagram scene
    @FXML
    private void openClassDiagramCanvas(ActionEvent event) {
        openNewScene("ClassDiagramCanvas.fxml", event);
    }

    // Function to open the Use Case Diagram scene
    @FXML
    private void openUseCaseDiagramCanvas(ActionEvent event) {
        openNewScene("UseCaseDiagramCanvas.fxml", event);
    }

    // Helper method to open a new scene
    private void openNewScene(String fxmlFile, ActionEvent event) {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Pane root = loader.load();

            // Get the current stage and set the new scene
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();  // Get the current stage
            Scene newScene = new Scene(root);
            stage.setScene(newScene);  // Set the new scene
            stage.show();  // Show the new scene

        } catch (IOException e) {
            e.printStackTrace();
            showError("Error opening the scene.");
        }
    }

    // Show an error message in case something goes wrong
    private void showError(String message) {
        // You can implement an error dialog or log as needed
    }
}
