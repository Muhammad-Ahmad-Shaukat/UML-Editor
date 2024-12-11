package com.boota.javaproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
/**
 * Main class for the JavaFX application. This class extends the `Application`
 * class and serves as the entry point for the JavaFX application lifecycle.
 * It sets up the primary stage and loads the main FXML file to display the
 * UI. The application launches by invoking the `main` method.
 *
 * The `start` method initializes the main scene, sets the title of the stage,
 * and displays the stage. It also assigns the `primaryStage` to a static
 * reference, allowing other parts of the application to access it globally.
 */
public class Main extends Application {
    public static Stage primaryStage;
    /**
     * Initializes and starts the primary stage of the JavaFX application by loading
     * the main FXML file and setting up the scene. This method is called automatically
     * during the application life cycle after the `launch` method is invoked.
     *
     * @param stage the primary stage for the JavaFX application. It is provided by
     *              the JavaFX framework and is used to display the application's UI.
     * @throws IOException if an error occurs during loading the FXML resource file.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("MainCanvas.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 500);
        stage.setTitle("xyz!");
        stage.setScene(scene);
        stage.show();
        primaryStage = stage;
    }
    /**
     * The main entry point for the JavaFX application. This method is responsible
     * for launching the JavaFX application by calling the `launch` method provided
     * by the `Application` class. The `launch` method initializes the JavaFX
     * framework and invokes the `start` method when ready.
     *
     * @param args command-line arguments passed to the application. These can be
     *             used to configure application behavior or provide additional
     *             runtime parameters. Typically, these are not used in most JavaFX
     *             applications.
     */
    public static void main(String[] args) {
        launch();
    }
}