package com.teachingaid;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.teachingaid.ui.MainViewController;

/**
 * Main Application class for the Searching Algorithm Visualizer
 * A teaching aid to demonstrate linear search and binary search algorithms
 */
public class SearchingVisualizerApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Searching Algorithm Visualizer - Teaching Aid");
        
        // Create the main view controller
        MainViewController mainController = new MainViewController();
        Scene scene = new Scene(mainController.getView(), 1400, 900);
        
        // Apply CSS styling
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        
        // Set window properties for better display
        primaryStage.setMaximized(true);
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(800);
        primaryStage.setResizable(true);
        
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Set the stage reference in the controller for modal dialogs
        mainController.setPrimaryStage(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
