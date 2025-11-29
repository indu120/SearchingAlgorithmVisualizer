package com.teachingaid;

import com.teachingaid.ui.MainViewController;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.scene.text.*;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {

        // Custom Window Buttons
        Button minimizeBtn = new Button("-");
        Button restoreBtn = new Button("□");
        Button closeBtn = new Button("×");

        minimizeBtn.setOnAction(e -> primaryStage.setIconified(true));
        restoreBtn.setOnAction(e -> primaryStage.setMaximized(!primaryStage.isMaximized()));
        closeBtn.setOnAction(e -> primaryStage.close());

        // Style top bar
        HBox windowBar = new HBox(10, minimizeBtn, restoreBtn, closeBtn);
        windowBar.setAlignment(Pos.CENTER_LEFT);
        windowBar.setStyle("-fx-padding: 10; -fx-background-color: linear-gradient(to right, #1B2631, #2E4053);");

        // Page Title
        Label title = new Label("🔹 Teaching Aid for Searching Algorithm Visualizer");
        title.setFont(Font.font("Poppins", FontWeight.EXTRA_BOLD, 36));
        title.setTextFill(Color.web("#1F618D"));
        title.setEffect(new DropShadow(10, Color.LIGHTGRAY));

        // Launch Button with animations
        Button searchBtn = new Button("🚀 Launch Visualizer");
        searchBtn.setStyle(buttonStyleMain());

        // Hover animation (zoom in/out)
        ScaleTransition zoomIn = new ScaleTransition(Duration.millis(150), searchBtn);
        zoomIn.setToX(1.1);
        zoomIn.setToY(1.1);
        ScaleTransition zoomOut = new ScaleTransition(Duration.millis(150), searchBtn);
        zoomOut.setToX(1);
        zoomOut.setToY(1);

        searchBtn.setOnMouseEntered(e -> zoomIn.playFromStart());
        searchBtn.setOnMouseExited(e -> zoomOut.playFromStart());

        searchBtn.setOnAction(e -> {
            // Load the visualizer into the SAME primary stage instead of opening a new window
            MainViewController controller = new MainViewController();
            Scene vizScene = new Scene(controller.getView(), 1400, 900);
            try {
                vizScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            } catch (Exception ex) {
                System.out.println("Note: styles.css not found, using default styling");
            }
            primaryStage.setScene(vizScene);
            primaryStage.setMaximized(true);
            primaryStage.setMinWidth(1200);
            primaryStage.setMinHeight(800);
            controller.setPrimaryStage(primaryStage);
        });

        // Description Section (Formatted)
        TextFlow desc = new TextFlow();
        desc.setTextAlignment(TextAlignment.LEFT);

        addDescLine(desc, "🔹 Developed using JavaFX and Maven for an interactive and modular design.", "#2C3E50", false);
        addDescLine(desc, "🔹 Visually demonstrates the working of various searching algorithms such as Linear Search, Binary Search, Jump Search, Exponential Search, KMP, and Boyer–Moore.", "#2C3E50", false);
        addDescLine(desc, "🔹 Provides an interactive interface to observe step-by-step execution of algorithms.", "#2C3E50", false);
        addDescLine(desc, "🔹 Includes an integrated MCQ Test Module to evaluate users’ understanding.", "#2C3E50", false);
        addDescLine(desc, "🔹 Supports PDF export for saving results and reports.", "#2C3E50", false);
        addDescLine(desc, "🔹 Designed as an educational tool to simplify learning of algorithmic concepts.", "#2C3E50", false);

        desc.setMaxWidth(1200);
        desc.setStyle("-fx-padding: 20; -fx-background-color: rgba(255,255,255,0.7); -fx-background-radius: 15;");

        ScrollPane scroll = new ScrollPane(desc);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-border-color: transparent;");
        scroll.setPrefHeight(400);

        // Layout arrangement
        VBox content = new VBox(30, title, searchBtn, scroll);
        content.setAlignment(Pos.TOP_CENTER);
        content.setStyle("-fx-padding: 50;");

        BorderPane root = new BorderPane();
        root.setTop(windowBar);
        root.setCenter(content);

        // Elegant gradient background
        root.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #EBF5FB, #D6EAF8, #AED6F1);"
        );

        Scene scene = new Scene(root, 1200, 800);

        // Button styles for top bar
        minimizeBtn.setStyle(buttonStyleNeutral());
        restoreBtn.setStyle(buttonStyleNeutral());
        closeBtn.setStyle(buttonStyleRed());

        minimizeBtn.setOnMouseEntered(e -> minimizeBtn.setStyle(buttonStyleNeutralHover()));
        minimizeBtn.setOnMouseExited(e -> minimizeBtn.setStyle(buttonStyleNeutral()));

        restoreBtn.setOnMouseEntered(e -> restoreBtn.setStyle(buttonStyleNeutralHover()));
        restoreBtn.setOnMouseExited(e -> restoreBtn.setStyle(buttonStyleNeutral()));

        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle(buttonStyleRedHover()));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle(buttonStyleRed()));

        primaryStage.setScene(scene);
        primaryStage.setTitle("Teaching Aid for Searching Algorithms");
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    private void addDescLine(TextFlow flow, String text, String color, boolean bold) {
        Text t = new Text(text + "\n\n");
        t.setFont(Font.font("Poppins", bold ? FontWeight.BOLD : FontWeight.NORMAL, 18));
        t.setFill(Color.web(color));
        flow.getChildren().add(t);
    }

    private String buttonStyleMain() {
        return "-fx-background-color: linear-gradient(to right, #2980B9, #2471A3);" +
               "-fx-text-fill: white; -fx-font-size: 20px; -fx-font-family: 'Poppins';" +
               "-fx-font-weight: bold; -fx-padding: 18 40; -fx-background-radius: 30;" +
               "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 8,0,0,2);";
    }

    private String buttonStyleNeutral() {
        return "-fx-background-color: #BDC3C7; -fx-text-fill: #2C3E50; -fx-font-size: 14px;" +
               "-fx-font-weight: bold; -fx-background-radius: 50; -fx-padding: 5 10;";
    }

    private String buttonStyleNeutralHover() {
        return "-fx-background-color: #95A5A6; -fx-text-fill: #2C3E50; -fx-font-size: 14px;" +
               "-fx-font-weight: bold; -fx-background-radius: 50; -fx-padding: 5 10;";
    }

    private String buttonStyleRed() {
        return "-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-size: 14px;" +
               "-fx-font-weight: bold; -fx-background-radius: 50; -fx-padding: 5 10;";
    }

    private String buttonStyleRedHover() {
        return "-fx-background-color: #C0392B; -fx-text-fill: white; -fx-font-size: 14px;" +
               "-fx-font-weight: bold; -fx-background-radius: 50; -fx-padding: 5 10;";
    }

    public static void main(String[] args) {
        launch(args);
    }
}
