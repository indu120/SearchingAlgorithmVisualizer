package com.teachingaid.algorithms;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import com.teachingaid.ui.ArrayVisualizationPane;

import java.util.function.Consumer;

/**
 * Binary Search Algorithm with step-by-step visualization
 */
public class BinarySearch {
    
    private final Color BINARY_LEFT_COLOR = Color.web("#9C27B0");
    private final Color BINARY_RIGHT_COLOR = Color.web("#FF5722");
    private final Color BINARY_MID_COLOR = Color.web("#2196F3");
    private final Color FOUND_COLOR = Color.web("#4CAF50");
    private final Color COMPARING_COLOR = Color.web("#FFC107");
    
    private boolean isPaused = false;
    private boolean isStopped = false;
    private int currentStep = 0;
    private int[] searchArray;
    private int targetValue;
    private ArrayVisualizationPane visualizationPane;
    private Consumer<Integer> onComplete;
    
    // Animation timing control
    private int animationDelay = 1000; // base delay in milliseconds
    
    // Binary search state variables
    private int left, right, mid;
    
    /**
     * Visualizes the binary search algorithm
     * @param array The sorted array to search in
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
        
        // Initialize binary search variables
        this.left = 0;
        this.right = array.length - 1;
        
        // Reset visualization
        visualizationPane.resetHighlights();
        visualizationPane.setInstructionText("Starting Binary Search for " + target + " in sorted array");
        
        // Start the search animation
        performBinarySearchStep();
    }
    
    private void performBinarySearchStep() {
        if (isStopped || isPaused) {
            return;
        }
        
        if (left > right) {
            // Search completed - not found
            Platform.runLater(() -> {
                visualizationPane.markAsNotFound();
                onComplete.accept(-1);
            });
            return;
        }
        
        // Calculate middle index
        mid = left + (right - left) / 2;
        currentStep++;
        
        // Show the current search range
        Platform.runLater(() -> {
            String stepDescription = String.format(
                "Step %d: Searching in range [%d, %d], middle at index %d (value: %d)",
                currentStep, left, right, mid, searchArray[mid]
            );
            
            visualizationPane.highlightRange(left, right, mid, stepDescription);
        });
        
        // Pause for visualization
        PauseTransition rangeTransition = new PauseTransition(Duration.millis(animationDelay));
        rangeTransition.setOnFinished(e -> {
            if (isStopped || isPaused) return;
            
            // Compare middle element with target
            Platform.runLater(() -> {
                String compareDescription = String.format(
                    "Comparing middle element %d with target %d: %s",
                    searchArray[mid], targetValue,
                    getComparisonResult(searchArray[mid], targetValue)
                );
                
                Color compareColor = COMPARING_COLOR;
                if (searchArray[mid] == targetValue) {
                    compareColor = FOUND_COLOR;
                }
                
                visualizationPane.highlightElement(mid, compareColor, compareDescription);
            });
            
            // Pause for comparison visualization
            PauseTransition compareTransition = new PauseTransition(Duration.millis(animationDelay));
            compareTransition.setOnFinished(e2 -> {
                if (isStopped || isPaused) return;
                
                if (searchArray[mid] == targetValue) {
                    // Found the element!
                    Platform.runLater(() -> {
                        visualizationPane.markAsFound(mid);
                        onComplete.accept(mid);
                    });
                } else if (searchArray[mid] > targetValue) {
                    // Target is in left half
                    right = mid - 1;
                    
                    Platform.runLater(() -> {
                        String nextStepDesc = String.format(
                            "Target %d < %d, searching left half. New range: [%d, %d]",
                            targetValue, searchArray[mid], left, right
                        );
                        visualizationPane.setInstructionText(nextStepDesc);
                    });
                    
                    // Continue search in left half
                    PauseTransition nextStepTransition = new PauseTransition(Duration.millis(Math.max(200, animationDelay / 2)));
                    nextStepTransition.setOnFinished(e3 -> performBinarySearchStep());
                    nextStepTransition.play();
                    
                } else {
                    // Target is in right half
                    left = mid + 1;
                    
                    Platform.runLater(() -> {
                        String nextStepDesc = String.format(
                            "Target %d > %d, searching right half. New range: [%d, %d]",
                            targetValue, searchArray[mid], left, right
                        );
                        visualizationPane.setInstructionText(nextStepDesc);
                    });
                    
                    // Continue search in right half
                    PauseTransition nextStepTransition = new PauseTransition(Duration.millis(Math.max(200, animationDelay / 2)));
                    nextStepTransition.setOnFinished(e3 -> performBinarySearchStep());
                    nextStepTransition.play();
                }
            });
            compareTransition.play();
        });
        rangeTransition.play();
    }
    
    private String getComparisonResult(int midValue, int target) {
        if (midValue == target) {
            return "MATCH FOUND!";
        } else if (midValue > target) {
            return String.format("%d > %d, search LEFT half", midValue, target);
        } else {
            return String.format("%d < %d, search RIGHT half", midValue, target);
        }
    }
    
    /**
     * Performs binary search without visualization (for comparison/testing)
     * @param array The sorted array to search in
     * @param target The value to search for
     * @return The index of the target element, or -1 if not found
     */
    public static int binarySearch(int[] array, int target) {
        int left = 0;
        int right = array.length - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            if (array[mid] == target) {
                return mid;
            } else if (array[mid] > target) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        
        return -1;
    }
    
    /**
     * Checks if an array is sorted in ascending order
     * @param array The array to check
     * @return true if sorted, false otherwise
     */
    public static boolean isSorted(int[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i] < array[i-1]) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Gets detailed information about binary search algorithm
     * @return Algorithm information string
     */
    public static String getAlgorithmInfo() {
        return "Binary Search Algorithm:\n\n" +
               "Description:\n" +
               "Binary search works on sorted arrays by repeatedly dividing the search space in half. " +
               "It compares the target with the middle element and eliminates half of the remaining elements.\n\n" +
               "Prerequisites:\n" +
               "• Array must be sorted in ascending order\n\n" +
               "Time Complexity:\n" +
               "• Best Case: O(1) - target is the middle element\n" +
               "• Average Case: O(log n) - typical case\n" +
               "• Worst Case: O(log n) - target is not present or at the ends\n\n" +
               "Space Complexity: O(1) for iterative implementation\n\n" +
               "Advantages:\n" +
               "• Very efficient for large sorted datasets\n" +
               "• Logarithmic time complexity\n" +
               "• Simple logic and implementation\n" +
               "• Predictable performance\n\n" +
               "Disadvantages:\n" +
               "• Requires sorted data\n" +
               "• Not suitable for linked lists (no random access)\n" +
               "• Overhead of sorting if data is not already sorted\n\n" +
               "Use Cases:\n" +
               "• Large sorted datasets\n" +
               "• Databases and search systems\n" +
               "• When frequent searches are required\n" +
               "• Dictionary/phone book lookups";
    }
    
    /**
     * Gets the step-by-step trace of binary search
     * @param array The sorted array to search in
     * @param target The value to search for
     * @return Array of step descriptions
     */
    public static String[] getSearchTrace(int[] array, int target) {
        java.util.List<String> steps = new java.util.ArrayList<>();
        
        steps.add("Starting Binary Search for value " + target);
        steps.add("Array must be sorted: " + java.util.Arrays.toString(array));
        
        int left = 0;
        int right = array.length - 1;
        int stepNum = 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            steps.add(String.format("Step %d: Range [%d, %d], Middle index %d = %d", 
                     stepNum, left, right, mid, array[mid]));
            
            if (array[mid] == target) {
                steps.add(String.format("Found! Target %d found at index %d", target, mid));
                break;
            } else if (array[mid] > target) {
                steps.add(String.format("%d > %d, search left half", array[mid], target));
                right = mid - 1;
            } else {
                steps.add(String.format("%d < %d, search right half", array[mid], target));
                left = mid + 1;
            }
            
            stepNum++;
        }
        
        if (binarySearch(array, target) == -1) {
            steps.add("Search completed. Target not found in array.");
        }
        
        return steps.toArray(new String[0]);
    }
    
    /**
     * Calculates the maximum number of comparisons needed for binary search
     * @param arraySize The size of the array
     * @return Maximum comparisons needed
     */
    public static int getMaxComparisons(int arraySize) {
        return (int) Math.ceil(Math.log(arraySize) / Math.log(2));
    }
    
    public void pause() {
        isPaused = true;
    }
    
    public void resume() {
        if (isPaused) {
            isPaused = false;
            performBinarySearchStep();
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
        left = 0;
        right = searchArray != null ? searchArray.length - 1 : 0;
        if (visualizationPane != null) {
            visualizationPane.resetHighlights();
        }
    }
    
    /**
     * Sets the animation delay for visualization speed control
     * @param delay delay in milliseconds (100-2000ms recommended)
     */
    public void setAnimationDelay(int delay) {
        this.animationDelay = Math.max(100, Math.min(2000, delay));
    }
    
    public int getAnimationDelay() {
        return animationDelay;
    }
}
