package com.boota.demo;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/boota/demo/line.fxml")); // Check the path
            Parent root = loader.load();
            primaryStage.setTitle("Line Drawing Example");
            primaryStage.setScene(new Scene(root, 600, 600));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Print the stack trace for debugging
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}