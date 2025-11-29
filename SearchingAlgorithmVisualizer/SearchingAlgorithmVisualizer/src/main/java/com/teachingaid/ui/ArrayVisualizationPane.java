package com.teachingaid.ui;

import javafx.animation.FillTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
* Visual representation of the array with animated search visualization
*/
public class ArrayVisualizationPane extends VBox {

    private int[] array;
    private List<ArrayElement> arrayElements;
    private HBox arrayContainer;
    private Label instructionLabel;

    // Modern Color Scheme
    private final Color DEFAULT_COLOR = Color.web("#f8fafc");
    private final Color CURRENT_COLOR = Color.web("#fbbf24");
    private final Color FOUND_COLOR = Color.web("#10b981");
    private final Color NOT_FOUND_COLOR = Color.web("#ef4444");
    private final Color BINARY_LEFT_COLOR = Color.web("#8b5cf6");
    private final Color BINARY_RIGHT_COLOR = Color.web("#f97316");
    private final Color BINARY_MID_COLOR = Color.web("#3b82f6");

    public ArrayVisualizationPane() {
        setSpacing(20);
        setAlignment(Pos.CENTER);
        setMinHeight(300);
        setPrefHeight(300);
        VBox.setVgrow(this, Priority.ALWAYS);

        instructionLabel = new Label("Enter array values and click Start to begin visualization");
        instructionLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 16));
        instructionLabel.setTextFill(Color.web("#555"));
        instructionLabel.setWrapText(true);
        instructionLabel.setMaxWidth(Double.MAX_VALUE);
        instructionLabel.setAlignment(Pos.CENTER);

        arrayContainer = new HBox(10);
        arrayContainer.setAlignment(Pos.CENTER);
        HBox.setHgrow(arrayContainer, Priority.ALWAYS);
        arrayContainer.setMinHeight(200);

        getChildren().addAll(instructionLabel, arrayContainer);
        VBox.setVgrow(arrayContainer, Priority.ALWAYS);

        arrayElements = new ArrayList<>();
    }

    public void setArray(int[] newArray) {
        this.array = newArray.clone();
        updateVisualization();
    }

    private void updateVisualization() {
        arrayElements.clear();
        arrayContainer.getChildren().clear();

        if (array == null || array.length == 0) {
            instructionLabel.setText("Enter array values and click Start to begin visualization");
            return;
        }

        instructionLabel.setText("Array loaded with " + array.length + " elements");

        // Calculate element size based on array length
        double maxWidth = Math.min(80, 600.0 / array.length);
        double elementHeight = 60;

        for (int i = 0; i < array.length; i++) {
            ArrayElement element = new ArrayElement(array[i], i, maxWidth, elementHeight);
            arrayElements.add(element);
            arrayContainer.getChildren().add(element.getContainer());
        }
    }

    public void highlightElement(int index, Color color, String description) {
        if (index >= 0 && index < arrayElements.size()) {
            arrayElements.get(index).highlight(color, description);
            instructionLabel.setText(description);
        }
    }

    public void highlightRange(int left, int right, int mid, String description) {
        // Reset all elements first
        resetHighlights();

        // Highlight range for binary search
        for (int i = 0; i < arrayElements.size(); i++) {
            if (i == mid) {
                arrayElements.get(i).highlight(BINARY_MID_COLOR, "Middle element");
            } else if (i >= left && i <= right) {
                if (i == left) {
                    arrayElements.get(i).highlight(BINARY_LEFT_COLOR, "Left boundary");
                } else if (i == right) {
                    arrayElements.get(i).highlight(BINARY_RIGHT_COLOR, "Right boundary");
                } else {
                    arrayElements.get(i).highlight(Color.web("#E8F5E8"), "In range");
                }
            } else {
                arrayElements.get(i).setInactive();
            }
        }

        instructionLabel.setText(description);
    }

    public void resetHighlights() {
        for (ArrayElement element : arrayElements) {
            element.reset();
        }
        instructionLabel.setText("Array ready for search");
    }

    public void markAsFound(int index) {
        if (index >= 0 && index < arrayElements.size()) {
            arrayElements.get(index).markAsFound();
            instructionLabel.setText("✓ Element found at index " + index + "!");
        }
    }

    public void markAsNotFound() {
        instructionLabel.setText("✗ Element not found in the array");
    }

    /**
    * Inner class representing a single array element
    */
    private class ArrayElement {
        private VBox container;
        private Rectangle rectangle;
        private Label valueLabel;
        private Label indexLabel;
        private int value;
        private int index;

        public ArrayElement(int value, int index, double width, double height) {
            this.value = value;
            this.index = index;

            container = new VBox(8);
            container.setAlignment(Pos.CENTER);

            // Create rectangle with modern gradient and shadow
            rectangle = new Rectangle(width, height);
            LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, null,
                new Stop(0, DEFAULT_COLOR),
                new Stop(0.5, DEFAULT_COLOR.brighter()),
                new Stop(1, DEFAULT_COLOR.darker())
            );
            rectangle.setFill(gradient);
            rectangle.setStroke(Color.web("#e2e8f0"));
            rectangle.setStrokeWidth(2);
            rectangle.setArcWidth(15);
            rectangle.setArcHeight(15);
            
            // Add modern drop shadow effect
            rectangle.setEffect(new javafx.scene.effect.DropShadow(
                javafx.scene.effect.BlurType.THREE_PASS_BOX,
                Color.color(0, 0, 0, 0.1),
                8, 0.3, 0, 3
            ));

            // Value label with modern typography
            valueLabel = new Label(String.valueOf(value));
            valueLabel.setFont(Font.font("SF Pro Display", FontWeight.BOLD,
                Math.min(18, Math.max(12, width / 2.5))));
            valueLabel.setTextFill(Color.web("#1f2937"));
            valueLabel.setStyle("-fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.1), 1, 0, 0, 1);");

            // Index label with modern styling
            indexLabel = new Label("[" + index + "]");
            indexLabel.setFont(Font.font("SF Pro Display", FontWeight.MEDIUM,
                Math.min(14, Math.max(10, width / 4))));
            indexLabel.setTextFill(Color.web("#6b7280"));
            indexLabel.setStyle("-fx-background-color: rgba(107,114,128,0.1); -fx-background-radius: 8px; -fx-padding: 2 6 2 6;");

            container.getChildren().addAll(indexLabel, rectangle, valueLabel);

            // Add modern hover effect with smooth transitions
            container.setOnMouseEntered(e -> {
                ScaleTransition scaleIn = new ScaleTransition(Duration.millis(150), rectangle);
                scaleIn.setToX(1.05);
                scaleIn.setToY(1.05);
                scaleIn.play();
                
                // Enhanced shadow on hover
                rectangle.setEffect(new javafx.scene.effect.DropShadow(
                    javafx.scene.effect.BlurType.THREE_PASS_BOX,
                    Color.color(102.0/255, 126.0/255, 234.0/255, 0.3),
                    12, 0.5, 0, 4
                ));
            });

            container.setOnMouseExited(e -> {
                ScaleTransition scaleOut = new ScaleTransition(Duration.millis(150), rectangle);
                scaleOut.setToX(1.0);
                scaleOut.setToY(1.0);
                scaleOut.play();
                
                // Reset shadow
                rectangle.setEffect(new javafx.scene.effect.DropShadow(
                    javafx.scene.effect.BlurType.THREE_PASS_BOX,
                    Color.color(0, 0, 0, 0.1),
                    8, 0.3, 0, 3
                ));
            });
        }

        public void highlight(Color color, String tooltip) {
            // Smooth color transition with modern gradient
            LinearGradient highlightGradient = new LinearGradient(0, 0, 0, 1, true, null,
                new Stop(0, color),
                new Stop(0.5, color.brighter()),
                new Stop(1, color.darker())
            );
            rectangle.setFill(highlightGradient);

            // Enhanced scale animation with better timing
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), rectangle);
            scaleTransition.setToX(1.08);
            scaleTransition.setToY(1.08);
            scaleTransition.setAutoReverse(true);
            scaleTransition.setCycleCount(2);
            scaleTransition.play();
            
            // Enhanced shadow effect during highlighting
            rectangle.setEffect(new javafx.scene.effect.DropShadow(
                javafx.scene.effect.BlurType.THREE_PASS_BOX,
                color.darker(),
                15, 0.6, 0, 5
            ));

            // Smart text color adjustment
            if (color.getBrightness() > 0.6) {
                valueLabel.setTextFill(Color.web("#1f2937"));
            } else {
                valueLabel.setTextFill(Color.WHITE);
            }
            
            // Animate the value label for extra emphasis
            ScaleTransition labelScale = new ScaleTransition(Duration.millis(200), valueLabel);
            labelScale.setToX(1.1);
            labelScale.setToY(1.1);
            labelScale.setAutoReverse(true);
            labelScale.setCycleCount(2);
            labelScale.play();
        }

        public void reset() {
            // Restore default gradient
            LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, null,
                new Stop(0, DEFAULT_COLOR),
                new Stop(0.5, DEFAULT_COLOR.brighter()),
                new Stop(1, DEFAULT_COLOR.darker())
            );
            rectangle.setFill(gradient);
            rectangle.setOpacity(1.0);
            valueLabel.setTextFill(Color.web("#1f2937"));
            
            // Reset scales
            rectangle.setScaleX(1.0);
            rectangle.setScaleY(1.0);
            valueLabel.setScaleX(1.0);
            valueLabel.setScaleY(1.0);
            
            // Reset shadow effect
            rectangle.setEffect(new javafx.scene.effect.DropShadow(
                javafx.scene.effect.BlurType.THREE_PASS_BOX,
                Color.color(0, 0, 0, 0.1),
                8, 0.3, 0, 3
            ));
        }

        public void setInactive() {
            rectangle.setOpacity(0.3);
            valueLabel.setTextFill(Color.web("#999"));
        }

        public void markAsFound() {
            // Create stunning success gradient
            LinearGradient successGradient = new LinearGradient(0, 0, 0, 1, true, null,
                new Stop(0, FOUND_COLOR.brighter()),
                new Stop(0.5, FOUND_COLOR),
                new Stop(1, FOUND_COLOR.darker())
            );
            rectangle.setFill(successGradient);
            
            // Epic celebration animation sequence
            ScaleTransition celebration1 = new ScaleTransition(Duration.millis(300), container);
            celebration1.setToX(1.3);
            celebration1.setToY(1.3);
            celebration1.setAutoReverse(true);
            celebration1.setCycleCount(2);
            
            // Add glowing effect
            rectangle.setEffect(new javafx.scene.effect.DropShadow(
                javafx.scene.effect.BlurType.GAUSSIAN,
                FOUND_COLOR,
                20, 0.8, 0, 0
            ));
            
            // Pulse the value label
            ScaleTransition labelPulse = new ScaleTransition(Duration.millis(200), valueLabel);
            labelPulse.setToX(1.4);
            labelPulse.setToY(1.4);
            labelPulse.setAutoReverse(true);
            labelPulse.setCycleCount(6);
            
            // Change text color to white for better contrast
            valueLabel.setTextFill(Color.WHITE);
            
            // Play animations
            celebration1.play();
            labelPulse.play();
        }

        public VBox getContainer() {
            return container;
        }

        public int getValue() {
            return value;
        }

        public int getIndex() {
            return index;
        }
    }

    public int[] getArray() {
        return array != null ? array.clone() : null;
    }

    public List<ArrayElement> getArrayElements() {
        return new ArrayList<>(arrayElements);
    }

    public void setInstructionText(String text) {
        instructionLabel.setText(text);
    }

    public String getInstructionText() {
        return instructionLabel.getText();
    }
}