package com.teachingaid.algorithms;

import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import com.teachingaid.ui.ArrayVisualizationPane;

import java.util.function.Consumer;

/**
 * Linear Search Algorithm with step-by-step visualization
 */
public class LinearSearch {
    
    private final Color CURRENT_COLOR = Color.web("#FFC107");
    private final Color COMPARING_COLOR = Color.web("#FF5722");
    private final Color FOUND_COLOR = Color.web("#4CAF50");
    private final Color NOT_FOUND_COLOR = Color.web("#F44336");
    
    private boolean isPaused = false;
    private boolean isStopped = false;
    private int currentStep = 0;
    private int[] searchArray;
    private int targetValue;
    private ArrayVisualizationPane visualizationPane;
    private Consumer<Integer> onComplete;
    
    // Animation timing control
    private int animationDelay = 800; // Default delay in milliseconds
    
    /**
     * Visualizes the linear search algorithm
     * @param array The array to search in
     * @param target The value to search for
     * @param visualPane The visualization pane
     * @param completionCallback Callback when search is complete
     */
    public void visualizeSearch(int[] array, int target, ArrayVisualizationPane visualPane, 
                               Consumer<Integer> completionCallback) {
        this.searchArray = array.clone();
        this.targetValue = target;
        this.visualizationPane = visualPane;
        this.onComplete = completionCallback;
        this.currentStep = 0;
        this.isStopped = false;
        this.isPaused = false;
        
        // Reset visualization
        visualizationPane.resetHighlights();
        visualizationPane.setInstructionText("Starting Linear Search for " + target);
        
        // Start the search animation
        performLinearSearchStep();
    }
    
    private void performLinearSearchStep() {
        if (isStopped || isPaused) {
            return;
        }
        
        if (currentStep >= searchArray.length) {
            // Search completed - not found
            Platform.runLater(() -> {
                visualizationPane.markAsNotFound();
                onComplete.accept(-1);
            });
            return;
        }
        
        // Highlight current element
        Platform.runLater(() -> {
            String stepDescription = String.format(
                "Step %d: Examining element at index %d (value: %d)",
                currentStep + 1, currentStep, searchArray[currentStep]
            );
            
            visualizationPane.highlightElement(currentStep, CURRENT_COLOR, stepDescription);
        });
        
        // Pause for visualization
        PauseTransition examineTransition = new PauseTransition(Duration.millis(animationDelay));
        examineTransition.setOnFinished(e -> {
            if (isStopped || isPaused) return;
            
            // Compare with target
            Platform.runLater(() -> {
                String compareDescription = String.format(
                    "Comparing %d with target %d: %s",
                    searchArray[currentStep], targetValue,
                    searchArray[currentStep] == targetValue ? "MATCH!" : "No match"
                );
                
                Color compareColor = searchArray[currentStep] == targetValue ? FOUND_COLOR : COMPARING_COLOR;
                visualizationPane.highlightElement(currentStep, compareColor, compareDescription);
            });
            
            // Pause for comparison visualization
            PauseTransition compareTransition = new PauseTransition(Duration.millis(animationDelay + 200));
            compareTransition.setOnFinished(e2 -> {
                if (isStopped || isPaused) return;
                
                if (searchArray[currentStep] == targetValue) {
                    // Found the element!
                    Platform.runLater(() -> {
                        visualizationPane.markAsFound(currentStep);
                        onComplete.accept(currentStep);
                    });
                } else {
                    // Continue to next element
                    currentStep++;
                    
                    // Add a small delay before next step
                    PauseTransition nextStepTransition = new PauseTransition(Duration.millis(animationDelay / 2));
                    nextStepTransition.setOnFinished(e3 -> performLinearSearchStep());
                    nextStepTransition.play();
                }
            });
            compareTransition.play();
        });
        examineTransition.play();
    }
    
    /**
     * Performs linear search without visualization (for comparison/testing)
     * @param array The array to search in
     * @param target The value to search for
     * @return The index of the target element, or -1 if not found
     */
    public static int linearSearch(int[] array, int target) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == target) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Gets detailed information about linear search algorithm
     * @return Algorithm information string
     */
    public static String getAlgorithmInfo() {
        return "Linear Search Algorithm:\n\n" +
               "Description:\n" +
               "Linear search sequentially checks each element in the array until the target is found or all elements have been checked.\n\n" +
               "Time Complexity:\n" +
               "• Best Case: O(1) - target is the first element\n" +
               "• Average Case: O(n) - target is in the middle\n" +
               "• Worst Case: O(n) - target is the last element or not present\n\n" +
               "Space Complexity: O(1)\n\n" +
               "Advantages:\n" +
               "• Simple to implement and understand\n" +
               "• Works on unsorted arrays\n" +
               "• No extra space required\n" +
               "• Stable algorithm\n\n" +
               "Disadvantages:\n" +
               "• Inefficient for large datasets\n" +
               "• Time complexity is linear\n\n" +
               "Use Cases:\n" +
               "• Small datasets\n" +
               "• Unsorted data\n" +
               "• When simplicity is preferred over efficiency";
    }
    
    /**
     * Gets the step-by-step trace of linear search
     * @param array The array to search in
     * @param target The value to search for
     * @return Array of step descriptions
     */
    public static String[] getSearchTrace(int[] array, int target) {
        java.util.List<String> steps = new java.util.ArrayList<>();
        
        steps.add("Starting Linear Search for value " + target);
        
        for (int i = 0; i < array.length; i++) {
            steps.add(String.format("Step %d: Check array[%d] = %d", i + 1, i, array[i]));
            
            if (array[i] == target) {
                steps.add(String.format("Found! Target %d found at index %d", target, i));
                break;
            } else {
                steps.add(String.format("%d ≠ %d, continue searching", array[i], target));
            }
        }
        
        if (linearSearch(array, target) == -1) {
            steps.add("Search completed. Target not found in array.");
        }
        
        return steps.toArray(new String[0]);
    }
    
    public void pause() {
        isPaused = true;
    }
    
    public void resume() {
        if (isPaused) {
            isPaused = false;
            performLinearSearchStep();
        }
    }
    
    public void stop() {
        isStopped = true;
        isPaused = false;
    }
    
    public boolean isPaused() {
        return isPaused;
    }
    
    public boolean isStopped() {
        return isStopped;
    }
    
    public int getCurrentStep() {
        return currentStep;
    }
    
    public void reset() {
        currentStep = 0;
        isStopped = false;
        isPaused = false;
        if (visualizationPane != null) {
            visualizationPane.resetHighlights();
        }
    }
    
    /**
     * Sets the animation delay for visualization speed control
     * @param delay The delay in milliseconds (100-2000ms recommended)
     */
    public void setAnimationDelay(int delay) {
        this.animationDelay = Math.max(100, Math.min(2000, delay)); // Clamp between 100ms and 2000ms
    }
    
    /**
     * Gets the current animation delay
     * @return The current delay in milliseconds
     */
    public int getAnimationDelay() {
        return animationDelay;
    }
}
