package com.teachingaid.algorithms;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import com.teachingaid.ui.ArrayVisualizationPane;

import java.util.function.Consumer;

/**
 * Exponential Search Algorithm with step-by-step visualization
 * Also known as Doubling Search or Galloping Search
 */
public class ExponentialSearch {
    
    private final Color EXPONENTIAL_COLOR = Color.web("#E74C3C");
    private final Color RANGE_COLOR = Color.web("#F39C12");
    private final Color BINARY_LEFT_COLOR = Color.web("#9C27B0");
    private final Color BINARY_RIGHT_COLOR = Color.web("#FF5722");
    private final Color BINARY_MID_COLOR = Color.web("#2196F3");
    private final Color FOUND_COLOR = Color.web("#4CAF50");
    
    private boolean isPaused = false;
    private boolean isStopped = false;
    private int currentStep = 0;
    private int[] searchArray;
    private int targetValue;
    private ArrayVisualizationPane visualizationPane;
    private Consumer<Integer> onComplete;
    
    // Animation timing control
    private int animationDelay = 1000; // base delay in ms
    
    // Exponential search state variables
    private int bound = 1;
    private boolean foundRange = false;
    private int left, right, mid;
    
    public void visualizeSearch(int[] array, int target, ArrayVisualizationPane visualPane, 
                               Consumer<Integer> completionCallback) {
        this.searchArray = array.clone();
        this.targetValue = target;
        this.visualizationPane = visualPane;
        this.onComplete = completionCallback;
        this.currentStep = 0;
        this.isStopped = false;
        this.isPaused = false;
        this.bound = 1;
        this.foundRange = false;
        
        // Reset visualization
        visualizationPane.resetHighlights();
        visualizationPane.setInstructionText("Starting Exponential Search for " + target);
        
        // Check if first element is the target
        if (searchArray[0] == target) {
            Platform.runLater(() -> {
                visualizationPane.markAsFound(0);
                onComplete.accept(0);
            });
            return;
        }
        
        // Start the exponential phase
        performExponentialStep();
    }
    
    private void performExponentialStep() {
        if (isStopped || isPaused) {
            return;
        }
        
        currentStep++;
        
        if (!foundRange) {
            // Phase 1: Find the range exponentially
            int index = Math.min(bound, searchArray.length - 1);
            
            Platform.runLater(() -> {
                String stepDescription = String.format(
                    "Step %d: Exponential phase - checking index %d (bound: %d, value: %d)",
                    currentStep, index, bound, searchArray[index]
                );
                
                visualizationPane.highlightElement(index, EXPONENTIAL_COLOR, stepDescription);
            });
            
            PauseTransition expTransition = new PauseTransition(Duration.millis(animationDelay));
            expTransition.setOnFinished(e -> {
                if (isStopped || isPaused) return;
                
                if (bound < searchArray.length && searchArray[bound] < targetValue) {
                    // Double the bound
                    Platform.runLater(() -> {
                        String nextStepDesc = String.format(
                            "Value %d < target %d, doubling bound from %d to %d",
                            searchArray[Math.min(bound, searchArray.length - 1)], targetValue, bound, bound * 2
                        );
                        visualizationPane.setInstructionText(nextStepDesc);
                    });
                    
                    bound *= 2;
                    
                    PauseTransition nextExpTransition = new PauseTransition(Duration.millis(Math.max(200, animationDelay / 2)));
                    nextExpTransition.setOnFinished(e2 -> performExponentialStep());
                    nextExpTransition.play();
                    
                } else {
                    // Found the range
                    foundRange = true;
                    left = bound / 2;
                    right = Math.min(bound, searchArray.length - 1);
                    
                    Platform.runLater(() -> {
                        String rangeFoundDesc = String.format(
                            "Range found! Binary search from index %d to %d",
                            left, right
                        );
                        visualizationPane.setInstructionText(rangeFoundDesc);
                        
                        // Highlight the range
                        for (int i = left; i <= right; i++) {
                            visualizationPane.highlightElement(i, RANGE_COLOR, "Search range");
                        }
                    });
                    
                    PauseTransition rangeTransition = new PauseTransition(Duration.millis(animationDelay));
                    rangeTransition.setOnFinished(e2 -> performBinarySearchStep());
                    rangeTransition.play();
                }
            });
            expTransition.play();
            
        } else {
            // This shouldn't be reached as we switch to binary search
            performBinarySearchStep();
        }
    }
    
    private void performBinarySearchStep() {
        if (isStopped || isPaused) {
            return;
        }
        
        if (left > right) {
            // Not found
            Platform.runLater(() -> {
                visualizationPane.markAsNotFound();
                onComplete.accept(-1);
            });
            return;
        }
        
        mid = left + (right - left) / 2;
        currentStep++;
        
        Platform.runLater(() -> {
            String stepDescription = String.format(
                "Binary search step %d: Range [%d, %d], middle at index %d (value: %d)",
                currentStep, left, right, mid, searchArray[mid]
            );
            
            // Show the current range with different colors
            for (int i = left; i <= right; i++) {
                if (i == left && i != mid) {
                    visualizationPane.highlightElement(i, BINARY_LEFT_COLOR, "Left boundary");
                } else if (i == right && i != mid) {
                    visualizationPane.highlightElement(i, BINARY_RIGHT_COLOR, "Right boundary");
                } else if (i == mid) {
                    visualizationPane.highlightElement(i, BINARY_MID_COLOR, stepDescription);
                } else {
                    visualizationPane.highlightElement(i, Color.web("#E8F5E8"), "In range");
                }
            }
        });
        
        PauseTransition binaryTransition = new PauseTransition(Duration.millis(animationDelay));
        binaryTransition.setOnFinished(e -> {
            if (isStopped || isPaused) return;
            
            if (searchArray[mid] == targetValue) {
                // Found!
                Platform.runLater(() -> {
                    visualizationPane.markAsFound(mid);
                    onComplete.accept(mid);
                });
                
            } else if (searchArray[mid] > targetValue) {
                // Search left half
                right = mid - 1;
                
                Platform.runLater(() -> {
                    String nextStepDesc = String.format(
                        "Target %d < %d, searching left half. New range: [%d, %d]",
                        targetValue, searchArray[mid], left, right
                    );
                    visualizationPane.setInstructionText(nextStepDesc);
                });
                
                PauseTransition nextBinaryTransition = new PauseTransition(Duration.millis(Math.max(200, animationDelay / 2)));
                nextBinaryTransition.setOnFinished(e2 -> performBinarySearchStep());
                nextBinaryTransition.play();
                
            } else {
                // Search right half
                left = mid + 1;
                
                Platform.runLater(() -> {
                    String nextStepDesc = String.format(
                        "Target %d > %d, searching right half. New range: [%d, %d]",
                        targetValue, searchArray[mid], left, right
                    );
                    visualizationPane.setInstructionText(nextStepDesc);
                });
                
                PauseTransition nextBinaryTransition = new PauseTransition(Duration.millis(Math.max(200, animationDelay / 2)));
                nextBinaryTransition.setOnFinished(e2 -> performBinarySearchStep());
                nextBinaryTransition.play();
            }
        });
        binaryTransition.play();
    }
    
    public static int exponentialSearch(int[] array, int target) {
        if (array[0] == target) {
            return 0;
        }
        
        // Find range for binary search
        int bound = 1;
        while (bound < array.length && array[bound] < target) {
            bound *= 2;
        }
        
        // Binary search in the found range
        int left = bound / 2;
        int right = Math.min(bound, array.length - 1);
        
        return binarySearch(array, target, left, right);
    }
    
    private static int binarySearch(int[] array, int target, int left, int right) {
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
    
    public static String getAlgorithmInfo() {
        return "Exponential Search Algorithm:\n\n" +
               "Description:\n" +
               "Exponential search finds a range containing the target by exponentially " +
               "increasing the bound, then performs binary search in that range.\n\n" +
               "Time Complexity:\n" +
               "• Best Case: O(1) - target is at first position\n" +
               "• Average Case: O(log n)\n" +
               "• Worst Case: O(log n)\n\n" +
               "Space Complexity: O(1)\n\n" +
               "Algorithm Steps:\n" +
               "1. Check if first element is target\n" +
               "2. Find range by doubling bound (1, 2, 4, 8, ...)\n" +
               "3. Perform binary search in found range\n\n" +
               "Advantages:\n" +
               "• Works well for unbounded/infinite arrays\n" +
               "• Efficient when target is close to beginning\n" +
               "• Better than binary search for unknown array size\n\n" +
               "Use Cases:\n" +
               "• Searching in unbounded or infinite lists\n" +
               "• When array size is unknown\n" +
               "• Target likely to be near the beginning";
    }
    
    public static String[] getSearchTrace(int[] array, int target) {
        java.util.List<String> steps = new java.util.ArrayList<>();
        
        steps.add("Starting Exponential Search for value " + target);
        
        if (array[0] == target) {
            steps.add("Found! Target found at index 0");
            return steps.toArray(new String[0]);
        }
        
        // Find range
        int bound = 1;
        int stepNum = 1;
        
        while (bound < array.length && array[bound] < target) {
            steps.add(String.format("Step %d: Check bound %d, value %d < %d, double bound", 
                     stepNum++, bound, array[bound], target));
            bound *= 2;
        }
        
        int left = bound / 2;
        int right = Math.min(bound, array.length - 1);
        steps.add(String.format("Range found: [%d, %d], starting binary search", left, right));
        
        // Binary search trace
        while (left <= right) {
            int mid = left + (right - left) / 2;
            steps.add(String.format("Binary search: Range [%d, %d], middle %d = %d", 
                     left, right, mid, array[mid]));
            
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
        }
        
        if (left > right) {
            steps.add("Target not found in array");
        }
        
        return steps.toArray(new String[0]);
    }
    
    public void pause() {
        isPaused = true;
    }
    
    public void resume() {
        if (isPaused) {
            isPaused = false;
            if (!foundRange) {
                performExponentialStep();
            } else {
                performBinarySearchStep();
            }
        }
    }
    
    public void stop() {
        isStopped = true;
        isPaused = false;
    }
    
    public void reset() {
        currentStep = 0;
        isStopped = false;
        isPaused = false;
        bound = 1;
        foundRange = false;
        if (visualizationPane != null) {
            visualizationPane.resetHighlights();
        }
    }
    
    public boolean isPaused() {
        return isPaused;
    }
    
    public boolean isStopped() {
        return isStopped;
    }
    
    /**
     * Sets the animation delay for visualization speed control
     */
    public void setAnimationDelay(int delay) {
        this.animationDelay = Math.max(100, Math.min(2000, delay));
    }
    
    public int getAnimationDelay() {
        return animationDelay;
    }
}
