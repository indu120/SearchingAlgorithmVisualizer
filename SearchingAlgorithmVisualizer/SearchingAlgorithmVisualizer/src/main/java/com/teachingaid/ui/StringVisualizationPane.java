package com.teachingaid.ui;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
* Visualization pane for string search algorithms
* Displays text, pattern, and failure function with highlighting
*/
public class StringVisualizationPane extends VBox {

    private static final Color TEXT_COLOR = Color.web("#2ECC71");
    private static final Color PATTERN_COLOR = Color.web("#3498DB");
    private static final Color MATCH_COLOR = Color.web("#E74C3C");
    private static final Color MISMATCH_COLOR = Color.web("#F39C12");
    private static final Color FAILURE_COLOR = Color.web("#9B59B6");
    private static final Color FOUND_COLOR = Color.web("#27AE60");
    private static final Color CURRENT_COLOR = Color.web("#E67E22");

    private String text;
    private String pattern;
    private HBox textContainer;
    private HBox patternContainer;
    private HBox failureFunctionContainer;
    private Label[] textLabels;
    private Label[] patternLabels;
    private Label[] failureLabels;
    private Label instructionLabel;
    private VBox failureFunctionBox;
    private ScrollPane textScrollPane;
    private ScrollPane patternScrollPane;
    private ScrollPane failureScrollPane;

    public StringVisualizationPane() {
        super();
        setSpacing(20);
        setPadding(new Insets(15));
        setAlignment(Pos.CENTER);
        setMinHeight(300);
        setPrefHeight(400); // Increased height to accommodate scroll panes

        // Initialize instruction label
        instructionLabel = new Label("Enter text and pattern to start visualization");
        instructionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        instructionLabel.setTextFill(Color.web("#34495E"));
        instructionLabel.setWrapText(true);
        instructionLabel.setMaxWidth(Double.MAX_VALUE);
        instructionLabel.setAlignment(Pos.CENTER);

        // Create containers
        textContainer = new HBox(5);
        textContainer.setAlignment(Pos.CENTER_LEFT);
        textContainer.setPadding(new Insets(5));
        
        patternContainer = new HBox(5);
        patternContainer.setAlignment(Pos.CENTER_LEFT);
        patternContainer.setPadding(new Insets(5));
        
        failureFunctionContainer = new HBox(5);
        failureFunctionContainer.setAlignment(Pos.CENTER_LEFT);
        failureFunctionContainer.setPadding(new Insets(5));

        // Create scroll panes
        textScrollPane = createScrollPane(textContainer, "Text");
        patternScrollPane = createScrollPane(patternContainer, "Pattern");
        
        // Create failure function display box
        failureFunctionBox = new VBox(10);
        failureFunctionBox.setAlignment(Pos.CENTER);
        failureFunctionBox.setVisible(false);

        Label textTitle = new Label("Text:");
        textTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        textTitle.setTextFill(Color.web("#2C3E50"));

        Label patternTitle = new Label("Pattern:");
        patternTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        patternTitle.setTextFill(Color.web("#2C3E50"));

        VBox textSection = new VBox(5);
        textSection.setAlignment(Pos.CENTER);
        textSection.getChildren().addAll(textTitle, textScrollPane);

        VBox patternSection = new VBox(5);
        patternSection.setAlignment(Pos.CENTER);
        patternSection.getChildren().addAll(patternTitle, patternScrollPane);

        getChildren().addAll(
            instructionLabel,
            textSection,
            patternSection,
            failureFunctionBox
        );
    }

    private ScrollPane createScrollPane(HBox content, String type) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(content);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefViewportWidth(400); // Set initial width
        scrollPane.setMaxHeight(100); // Limit height to prevent taking too much space
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: #F8F9FA; -fx-border-color: #DEE2E6; -fx-border-width: 1;");
        
        // Adjust scroll pane width based on content
        content.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            if (newWidth.doubleValue() > scrollPane.getPrefViewportWidth()) {
                scrollPane.setPrefViewportWidth(Math.min(newWidth.doubleValue(), 600));
            }
        });
        
        return scrollPane;
    }

    public void setTextAndPattern(String text, String pattern) {
        this.text = text;
        this.pattern = pattern;

        createTextVisualization();
        createPatternVisualization();
        resetHighlights();
        
        // Adjust scroll pane sizes based on content length
        adjustScrollPaneSizes();
    }

    private void adjustScrollPaneSizes() {
        // Adjust text scroll pane width based on text length
        if (text != null) {
            double textWidth = Math.min(text.length() * 45, 600); // 45px per character box
            textScrollPane.setPrefViewportWidth(textWidth);
        }
        
        // Adjust pattern scroll pane width based on pattern length
        if (pattern != null) {
            double patternWidth = Math.min(pattern.length() * 45, 400); // 45px per character box
            patternScrollPane.setPrefViewportWidth(patternWidth);
        }
    }

    private void createTextVisualization() {
        textContainer.getChildren().clear();
        if (text == null || text.isEmpty()) return;
        
        textLabels = new Label[text.length()];

        for (int i = 0; i < text.length(); i++) {
            Label charLabel = new Label(String.valueOf(text.charAt(i)));
            charLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 18));
            charLabel.setTextFill(TEXT_COLOR);
            charLabel.setPrefSize(40, 40);
            charLabel.setAlignment(Pos.CENTER);

            // Background rectangle
            Rectangle background = new Rectangle(40, 40);
            background.setFill(Color.web("#ECF0F1"));
            background.setStroke(Color.web("#BDC3C7"));
            background.setStrokeWidth(1);

            StackPane charPane = new StackPane();
            charPane.getChildren().addAll(background, charLabel);

            // Add index label below
            Label indexLabel = new Label(String.valueOf(i));
            indexLabel.setFont(Font.font("Arial", 10));
            indexLabel.setTextFill(Color.web("#7F8C8D"));

            VBox charBox = new VBox(2);
            charBox.setAlignment(Pos.CENTER);
            charBox.getChildren().addAll(charPane, indexLabel);

            textLabels[i] = charLabel;
            textContainer.getChildren().add(charBox);
        }
    }

    private void createPatternVisualization() {
        patternContainer.getChildren().clear();
        if (pattern == null || pattern.isEmpty()) return;
        
        patternLabels = new Label[pattern.length()];

        for (int i = 0; i < pattern.length(); i++) {
            Label charLabel = new Label(String.valueOf(pattern.charAt(i)));
            charLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 18));
            charLabel.setTextFill(PATTERN_COLOR);
            charLabel.setPrefSize(40, 40);
            charLabel.setAlignment(Pos.CENTER);

            // Background rectangle
            Rectangle background = new Rectangle(40, 40);
            background.setFill(Color.web("#EBF3FD"));
            background.setStroke(Color.web("#85C1E9"));
            background.setStrokeWidth(1);

            StackPane charPane = new StackPane();
            charPane.getChildren().addAll(background, charLabel);

            // Add index label below
            Label indexLabel = new Label(String.valueOf(i));
            indexLabel.setFont(Font.font("Arial", 10));
            indexLabel.setTextFill(Color.web("#7F8C8D"));

            VBox charBox = new VBox(2);
            charBox.setAlignment(Pos.CENTER);
            charBox.getChildren().addAll(charPane, indexLabel);

            patternLabels[i] = charLabel;
            patternContainer.getChildren().add(charBox);
        }
    }

    public void showFailureFunction(int[] failure, String pattern) {
        failureFunctionBox.getChildren().clear();
        failureFunctionBox.setVisible(true);

        Label failureTitle = new Label("Failure Function (Partial Match Table):");
        failureTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        failureTitle.setTextFill(Color.web("#2C3E50"));

        // Pattern display for failure function
        HBox patternRow = new HBox(5);
        patternRow.setAlignment(Pos.CENTER);

        // Failure function values
        HBox failureRow = new HBox(5);
        failureRow.setAlignment(Pos.CENTER);

        failureLabels = new Label[failure.length];

        for (int i = 0; i < pattern.length(); i++) {
            // Pattern character
            Label patternChar = new Label(String.valueOf(pattern.charAt(i)));
            patternChar.setFont(Font.font("Courier New", FontWeight.BOLD, 16));
            patternChar.setTextFill(PATTERN_COLOR);
            patternChar.setPrefSize(35, 35);
            patternChar.setAlignment(Pos.CENTER);

            Rectangle patternBg = new Rectangle(35, 35);
            patternBg.setFill(Color.web("#EBF3FD"));
            patternBg.setStroke(Color.web("#85C1E9"));
            patternBg.setStrokeWidth(1);

            StackPane patternPane = new StackPane();
            patternPane.getChildren().addAll(patternBg, patternChar);
            patternRow.getChildren().add(patternPane);

            // Failure function value
            Label failureValue = new Label(String.valueOf(failure[i]));
            failureValue.setFont(Font.font("Courier New", FontWeight.BOLD, 16));
            failureValue.setTextFill(FAILURE_COLOR);
            failureValue.setPrefSize(35, 35);
            failureValue.setAlignment(Pos.CENTER);

            Rectangle failureBg = new Rectangle(35, 35);
            failureBg.setFill(Color.web("#F4F1F8"));
            failureBg.setStroke(Color.web("#D2B4DE"));
            failureBg.setStrokeWidth(1);

            StackPane failurePane = new StackPane();
            failurePane.getChildren().addAll(failureBg, failureValue);
            failureRow.getChildren().add(failurePane);

            failureLabels[i] = failureValue;
        }

        // Create scroll pane for failure function if pattern is long
        if (pattern.length() > 15) {
            HBox failureContent = new HBox(10);
            failureContent.setAlignment(Pos.CENTER);
            failureContent.getChildren().addAll(failureRow);
            
            failureScrollPane = new ScrollPane();
            failureScrollPane.setContent(failureContent);
            failureScrollPane.setFitToHeight(true);
            failureScrollPane.setPrefViewportWidth(300);
            failureScrollPane.setMaxHeight(60);
            failureScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            failureScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            failureScrollPane.setStyle("-fx-background: #F8F9FA; -fx-border-color: #DEE2E6; -fx-border-width: 1;");
            
            failureFunctionBox.getChildren().addAll(
                failureTitle,
                patternRow,
                failureScrollPane
            );
        } else {
            // Labels for rows
            Label patternLabel = new Label("Pattern:");
            patternLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
            patternLabel.setTextFill(Color.web("#2C3E50"));

            Label failureRowLabel = new Label("Failure:");
            failureRowLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
            failureRowLabel.setTextFill(Color.web("#2C3E50"));

            HBox patternRowWithLabel = new HBox(10);
            patternRowWithLabel.setAlignment(Pos.CENTER);
            patternRowWithLabel.getChildren().addAll(patternLabel, patternRow);

            HBox failureRowWithLabel = new HBox(10);
            failureRowWithLabel.setAlignment(Pos.CENTER);
            failureRowWithLabel.getChildren().addAll(failureRowLabel, failureRow);

            failureFunctionBox.getChildren().addAll(
                failureTitle,
                patternRowWithLabel,
                failureRowWithLabel
            );
        }

        // Animate appearance
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), failureFunctionBox);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    public void highlightFailureConstruction(int index, int value, String description) {
        setInstructionText(description);

        // Reset previous highlights
        if (failureLabels != null) {
            for (Label label : failureLabels) {
                label.getParent().setStyle("-fx-background-color: #F4F1F8;");
            }
        }

        // Highlight current failure value being constructed
        if (index < failureLabels.length) {
            failureLabels[index].getParent().setStyle("-fx-background-color: #E8DAEF;");

            // Animate the highlight
            ScaleTransition scale = new ScaleTransition(Duration.millis(300), failureLabels[index].getParent());
            scale.setFromX(1.0);
            scale.setFromY(1.0);
            scale.setToX(1.2);
            scale.setToY(1.2);
            scale.setAutoReverse(true);
            scale.setCycleCount(2);
            scale.play();
            
            // Ensure the highlighted element is visible in scroll pane
            if (failureScrollPane != null) {
                double viewportWidth = failureScrollPane.getViewportBounds().getWidth();
                double contentWidth = failureScrollPane.getContent().getBoundsInLocal().getWidth();
                double scrollPosition = (index * 45) / contentWidth; // 45px per failure box
                failureScrollPane.setHvalue(scrollPosition);
            }
        }
    }

    public void highlightComparison(int textIndex, int patternIndex, String description) {
        setInstructionText(description);

        // Reset all highlights
        resetCharacterColors();

        // Highlight current comparison
        if (textIndex < textLabels.length) {
            ((StackPane)textLabels[textIndex].getParent()).getChildren().get(0).setStyle("-fx-fill: #F7DC6F;");
            textLabels[textIndex].setTextFill(CURRENT_COLOR);
            
            // Ensure the highlighted text character is visible in scroll pane
            double viewportWidth = textScrollPane.getViewportBounds().getWidth();
            double contentWidth = textScrollPane.getContent().getBoundsInLocal().getWidth();
            double scrollPosition = (textIndex * 45) / contentWidth; // 45px per character box
            textScrollPane.setHvalue(scrollPosition);
        }

        if (patternIndex < patternLabels.length) {
            ((StackPane)patternLabels[patternIndex].getParent()).getChildren().get(0).setStyle("-fx-fill: #F7DC6F;");
            patternLabels[patternIndex].setTextFill(CURRENT_COLOR);
            
            // Ensure the highlighted pattern character is visible in scroll pane
            double viewportWidth = patternScrollPane.getViewportBounds().getWidth();
            double contentWidth = patternScrollPane.getContent().getBoundsInLocal().getWidth();
            double scrollPosition = (patternIndex * 45) / contentWidth; // 45px per character box
            patternScrollPane.setHvalue(scrollPosition);
        }

        // Animate comparison
        if (textIndex < textLabels.length) {
            ScaleTransition textScale = new ScaleTransition(Duration.millis(400), textLabels[textIndex].getParent());
            textScale.setFromX(1.0);
            textScale.setFromY(1.0);
            textScale.setToX(1.1);
            textScale.setToY(1.1);
            textScale.setAutoReverse(true);
            textScale.setCycleCount(2);
            textScale.play();
        }

        if (patternIndex < patternLabels.length) {
            ScaleTransition patternScale = new ScaleTransition(Duration.millis(400), patternLabels[patternIndex].getParent());
            patternScale.setFromX(1.0);
            patternScale.setFromY(1.0);
            patternScale.setToX(1.1);
            patternScale.setToY(1.1);
            patternScale.setAutoReverse(true);
            patternScale.setCycleCount(2);
            patternScale.play();
        }
    }

    public void highlightFailureUse(int failureIndex, int failureValue) {
        if (failureIndex < failureLabels.length) {
            failureLabels[failureIndex].getParent().setStyle("-fx-background-color: #FAD7A0;");

            // Animate the failure value being used
            ScaleTransition scale = new ScaleTransition(Duration.millis(500), failureLabels[failureIndex].getParent());
            scale.setFromX(1.0);
            scale.setFromY(1.0);
            scale.setToX(1.3);
            scale.setToY(1.3);
            scale.setAutoReverse(true);
            scale.setCycleCount(2);
            scale.play();
        }
    }

    public void markAsFound(int foundIndex) {
        setInstructionText("Pattern found at index " + foundIndex + "!");

        // Highlight the found pattern in the text
        for (int i = 0; i < pattern.length(); i++) {
            int textPos = foundIndex + i;
            if (textPos < textLabels.length) {
                ((StackPane)textLabels[textPos].getParent()).getChildren().get(0).setStyle("-fx-fill: #ABEBC6;");
                textLabels[textPos].setTextFill(FOUND_COLOR);

                // Animate success
                ScaleTransition successScale = new ScaleTransition(Duration.millis(600), textLabels[textPos].getParent());
                successScale.setFromX(1.0);
                successScale.setFromY(1.0);
                successScale.setToX(1.2);
                successScale.setToY(1.2);
                successScale.setAutoReverse(true);
                successScale.setCycleCount(3);
                successScale.play();
            }
        }

        // Highlight the pattern
        for (int i = 0; i < pattern.length(); i++) {
            ((StackPane)patternLabels[i].getParent()).getChildren().get(0).setStyle("-fx-fill: #ABEBC6;");
            patternLabels[i].setTextFill(FOUND_COLOR);
        }
    }

    public void markAsNotFound() {
        setInstructionText("Pattern not found in text!");

        // Fade out pattern to indicate not found
        FadeTransition patternFade = new FadeTransition(Duration.millis(800), patternContainer);
        patternFade.setFromValue(1.0);
        patternFade.setToValue(0.5);
        patternFade.setAutoReverse(true);
        patternFade.setCycleCount(2);
        patternFade.play();
    }

    public void resetHighlights() {
        resetCharacterColors();

        if (failureFunctionBox != null) {
            failureFunctionBox.setVisible(false);
        }

        setInstructionText("Ready to start string search visualization");
        
        // Reset scroll positions
        textScrollPane.setHvalue(0);
        patternScrollPane.setHvalue(0);
    }

    private void resetCharacterColors() {
        // Reset text colors
        if (textLabels != null) {
            for (int i = 0; i < textLabels.length; i++) {
                textLabels[i].setTextFill(TEXT_COLOR);
                ((StackPane)textLabels[i].getParent()).getChildren().get(0).setStyle("-fx-fill: #ECF0F1;");
            }
        }

        // Reset pattern colors
        if (patternLabels != null) {
            for (int i = 0; i < patternLabels.length; i++) {
                patternLabels[i].setTextFill(PATTERN_COLOR);
                ((StackPane)patternLabels[i].getParent()).getChildren().get(0).setStyle("-fx-fill: #EBF3FD;");
            }
        }

        // Reset failure function colors
        if (failureLabels != null) {
            for (Label label : failureLabels) {
                label.getParent().setStyle("-fx-background-color: #F4F1F8;");
            }
        }
    }

    public void setInstructionText(String text) {
        instructionLabel.setText(text);

        // Animate instruction text change
        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), instructionLabel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.7);
        fadeOut.setOnFinished(e -> {
            FadeTransition fadeIn = new FadeTransition(Duration.millis(150), instructionLabel);
            fadeIn.setFromValue(0.7);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }

    public void showStep(int step) {
        // Optional: Show current step number
        // This could be implemented to show step counter
    }

    // Getter methods for text and pattern
    public String getText() {
        return text;
    }

    public String getPattern() {
        return pattern;
    }
}