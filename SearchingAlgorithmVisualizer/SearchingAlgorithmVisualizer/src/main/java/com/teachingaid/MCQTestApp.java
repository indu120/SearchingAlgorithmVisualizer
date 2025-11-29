package com.teachingaid;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.teachingaid.ui.MCQTestView;

/**
 * Standalone MCQ Test Application
 * Run this separately while we fix the integration issues
 */
public class MCQTestApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("MCQ Challenge: Searching Algorithms");
        
        // Create the MCQ test view
        MCQTestView mcqTestView = new MCQTestView();
        mcqTestView.setPrimaryStage(primaryStage);
        
        Scene scene = new Scene(mcqTestView.getView(), 1400, 800);
        
        // Apply CSS styling (if exists)
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception e) {
            // Ignore if CSS file not found
            System.out.println("Note: styles.css not found, using default styling");
        }
        
        // Set window properties
        primaryStage.setMaximized(true);
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(700);
        primaryStage.setResizable(true);
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
