package com.boota.javaproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
/**
 * The MainCanvasController class is responsible for managing user interactions
 * on the main canvas of the application. It handles navigation between different
 * scenes (e.g., Class Diagram and Use Case Diagram) and provides functionality
 * for application control, such as closing the application.
 *
 * The class uses the JavaFX framework to handle events and load new scenes from
 * FXML files. The internal helper methods manage scene transitions and handle
 * scenarios such as error occurrences during file loading.
 */
public class MainCanvasController {

    /**
     * Closes the current application window or stage. This method retrieves the
     * stage associated with the event source (e.g., a button) and shuts it down.
     *
     * @param event the ActionEvent triggered by the user interaction, such as clicking
     *              a button. This event is used to fetch the current stage and close it.
     */
    // Function to close the application
    @FXML
    private void close(ActionEvent event) {
        // Get the current stage from the event's source
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();  // Close the stage
    }

    /**
     * Opens the Class Diagram scene by transitioning to the "ClassDiagramCanvas.fxml" layout.
     * This method is triggered when the associated UI element (e.g., a button) is activated.
     * The helper method `openNewScene` is used to handle the scene loading and transition.
     *
     * @param event the ActionEvent triggered by the UI element invoking this method.
     *              It provides context about the source of the action.
     */
    // Function to open the Class Diagram scene
    @FXML
    private void openClassDiagramCanvas(ActionEvent event) {
        openNewScene("ClassDiagramCanvas.fxml", event);
    }

    /**
     * Opens the Use Case Diagram scene. This method switches the current
     * application's view to the Use Case Diagram by loading the corresponding
     * FXML file.
     *
     * @param event the ActionEvent triggered by the user interaction, such as clicking
     *              a button. This event is used to get the current stage and load
     *              the new scene.
     */
    // Function to open the Use Case Diagram scene
    @FXML
    private void openUseCaseDiagramCanvas(ActionEvent event) {
        openNewScene("UseCaseDiagramCanvas.fxml", event);
    }

    /**
     * Opens a new scene in the current stage using the specified FXML file.
     * This method loads the FXML layout, sets up the scene, and transitions
     * the application to the new scene.
     *
     * @param fxmlFile the path to the FXML file that defines the layout for the new scene.
     *                 This should be a valid FXML resource within the application's resources.
     * @param event    the ActionEvent triggered by a user interaction, such as a button click.
     *                 It is used to retrieve the current stage for scene transitioning.
     */
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

    /**
     * Displays an error message to the user when an operation fails or an exception is caught.
     * The specific implementation of how the message is displayed (e.g., as a dialog or console log)
     * can vary depending on the application's requirements.
     *
     * @param message the error message to be displayed to the user. This message provides details about
     *                the issue encountered.
     */
    // Show an error message in case something goes wrong
    private void showError(String message) {
        // You can implement an error dialog or log as needed
    }
}
