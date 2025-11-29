package com.teachingaid.algorithms;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import com.teachingaid.ui.ArrayVisualizationPane;

import java.util.function.Consumer;

/**
 * Jump Search Algorithm with step-by-step visualization
 * Works on sorted arrays by jumping ahead by fixed steps, then linear search within block
 */
public class JumpSearch {
    
    private final Color JUMP_COLOR = Color.web("#FF6B35");
    private final Color BLOCK_COLOR = Color.web("#F7931E");
    private final Color LINEAR_COLOR = Color.web("#FFD23F");
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
    
    // Jump search state variables
    private int jumpSize;
    private int prev = 0;
    private int jumpIndex = 0;
    
    public void visualizeSearch(int[] array, int target, ArrayVisualizationPane visualPane, 
                               Consumer<Integer> completionCallback) {
        this.searchArray = array.clone();
        this.targetValue = target;
        this.visualizationPane = visualPane;
        this.onComplete = completionCallback;
        this.currentStep = 0;
        this.isStopped = false;
        this.isPaused = false;
        
        // Calculate optimal jump size
        this.jumpSize = (int) Math.sqrt(array.length);
        this.prev = 0;
        this.jumpIndex = 0;
        
        // Reset visualization
        visualizationPane.resetHighlights();
        visualizationPane.setInstructionText("Starting Jump Search for " + target + " with jump size " + jumpSize);
        
        // Start the search animation
        performJumpSearchStep();
    }
    
    private void performJumpSearchStep() {
        if (isStopped || isPaused) {
            return;
        }
        
        currentStep++;
        
        // Phase 1: Jump through blocks
        if (jumpIndex < searchArray.length) {
            // Highlight current jump position
            Platform.runLater(() -> {
                String stepDescription = String.format(
                    "Step %d: Jumping to index %d (value: %d), comparing with target %d",
                    currentStep, jumpIndex, searchArray[jumpIndex], targetValue
                );
                
                visualizationPane.highlightElement(jumpIndex, JUMP_COLOR, stepDescription);
            });
            
            PauseTransition jumpTransition = new PauseTransition(Duration.millis(animationDelay));
            jumpTransition.setOnFinished(e -> {
                if (isStopped || isPaused) return;
                
                if (searchArray[jumpIndex] < targetValue) {
                    // Continue jumping
                    prev = jumpIndex;
                    jumpIndex = Math.min(jumpIndex + jumpSize, searchArray.length - 1);
                    
                    Platform.runLater(() -> {
                        String nextStepDesc = String.format(
                            "Value %d < target %d, continue jumping. Previous: %d, Next jump: %d",
                            searchArray[prev], targetValue, prev, jumpIndex
                        );
                        visualizationPane.setInstructionText(nextStepDesc);
                    });
                    
                    PauseTransition nextJumpTransition = new PauseTransition(Duration.millis(Math.max(200, animationDelay / 2)));
                    nextJumpTransition.setOnFinished(e2 -> performJumpSearchStep());
                    nextJumpTransition.play();
                    
                } else {
                    // Found the block, start linear search
                    Platform.runLater(() -> {
                        String blockFoundDesc = String.format(
                            "Found block! Value %d >= target %d. Linear search from %d to %d",
                            searchArray[jumpIndex], targetValue, prev, jumpIndex
                        );
                        visualizationPane.setInstructionText(blockFoundDesc);
                    });
                    
                    PauseTransition blockTransition = new PauseTransition(Duration.millis(animationDelay));
                    blockTransition.setOnFinished(e2 -> performLinearSearchInBlock());
                    blockTransition.play();
                }
            });
            jumpTransition.play();
            
        } else {
            // Reached end, search in last block
            Platform.runLater(() -> {
                String endBlockDesc = String.format(
                    "Reached end of array. Linear search from %d to %d",
                    prev, searchArray.length - 1
                );
                visualizationPane.setInstructionText(endBlockDesc);
            });
            
            PauseTransition endTransition = new PauseTransition(Duration.millis(Math.max(200, animationDelay / 2)));
            endTransition.setOnFinished(e -> performLinearSearchInBlock());
            endTransition.play();
        }
    }
    
    private void performLinearSearchInBlock() {
        // Highlight the block we're searching in
        Platform.runLater(() -> {
            for (int i = prev; i <= Math.min(jumpIndex, searchArray.length - 1); i++) {
                visualizationPane.highlightElement(i, BLOCK_COLOR, "Searching in this block");
            }
        });
        
        PauseTransition blockHighlightTransition = new PauseTransition(Duration.millis(animationDelay));
        blockHighlightTransition.setOnFinished(e -> linearSearchStep(prev));
        blockHighlightTransition.play();
    }
    
    private void linearSearchStep(int currentIndex) {
        if (isStopped || isPaused) {
            return;
        }
        
        if (currentIndex > Math.min(jumpIndex, searchArray.length - 1)) {
            // Not found in this block
            Platform.runLater(() -> {
                visualizationPane.markAsNotFound();
                onComplete.accept(-1);
            });
            return;
        }
        
        Platform.runLater(() -> {
            String linearStepDesc = String.format(
                "Linear search: Checking index %d (value: %d) vs target %d",
                currentIndex, searchArray[currentIndex], targetValue
            );
            visualizationPane.highlightElement(currentIndex, LINEAR_COLOR, linearStepDesc);
        });
        
        PauseTransition linearTransition = new PauseTransition(Duration.millis(Math.max(200, animationDelay / 2)));
        linearTransition.setOnFinished(e -> {
            if (isStopped || isPaused) return;
            
            if (searchArray[currentIndex] == targetValue) {
                // Found!
                Platform.runLater(() -> {
                    visualizationPane.markAsFound(currentIndex);
                    onComplete.accept(currentIndex);
                });
                } else {
                    // Continue linear search
                    PauseTransition nextLinearTransition = new PauseTransition(Duration.millis(Math.max(150, animationDelay / 3)));
                nextLinearTransition.setOnFinished(e2 -> linearSearchStep(currentIndex + 1));
                nextLinearTransition.play();
            }
        });
        linearTransition.play();
    }
    
    /**
     * Performs jump search without visualization
     */
    public static int jumpSearch(int[] array, int target) {
        int n = array.length;
        int jump = (int) Math.sqrt(n);
        int prev = 0;
        
        // Jump through blocks
        while (array[Math.min(jump, n) - 1] < target) {
            prev = jump;
            jump += (int) Math.sqrt(n);
            if (prev >= n) {
                return -1;
            }
        }
        
        // Linear search in the identified block
        while (array[prev] < target) {
            prev++;
            if (prev == Math.min(jump, n)) {
                return -1;
            }
        }
        
        if (array[prev] == target) {
            return prev;
        }
        
        return -1;
    }
    
    public static String getAlgorithmInfo() {
        return "Jump Search Algorithm:\n\n" +
               "Description:\n" +
               "Jump search works on sorted arrays by jumping ahead by √n steps, " +
               "then performing linear search in the identified block.\n\n" +
               "Time Complexity:\n" +
               "• Best Case: O(1) - target is at first jump\n" +
               "• Average Case: O(√n)\n" +
               "• Worst Case: O(√n)\n\n" +
               "Space Complexity: O(1)\n\n" +
               "Key Features:\n" +
               "• Optimal jump size is √n\n" +
               "• Better than linear search for large sorted arrays\n" +
               "• Worse than binary search but simpler to implement\n" +
               "• Good balance between jumps and linear search\n\n" +
               "Use Cases:\n" +
               "• When binary search is not suitable\n" +
               "• Systems where backward jumping is costly\n" +
               "• Large sorted datasets with uniform distribution";
    }
    
    public static String[] getSearchTrace(int[] array, int target) {
        java.util.List<String> steps = new java.util.ArrayList<>();
        int n = array.length;
        int jump = (int) Math.sqrt(n);
        int prev = 0;
        
        steps.add("Starting Jump Search for value " + target);
        steps.add("Array size: " + n + ", Optimal jump size: " + jump);
        
        int stepNum = 1;
        
        // Jump phase
        while (array[Math.min(jump, n) - 1] < target) {
            steps.add(String.format("Step %d: Jump to index %d, value %d < %d, continue jumping", 
                     stepNum++, Math.min(jump, n) - 1, array[Math.min(jump, n) - 1], target));
            prev = jump;
            jump += (int) Math.sqrt(n);
            if (prev >= n) {
                steps.add("Reached end of array, target not found");
                return steps.toArray(new String[0]);
            }
        }
        
        steps.add(String.format("Found block: Linear search from index %d to %d", prev, Math.min(jump, n) - 1));
        
        // Linear search phase
        while (array[prev] < target) {
            steps.add(String.format("Linear search: index %d, value %d < %d", prev, array[prev], target));
            prev++;
            if (prev == Math.min(jump, n)) {
                steps.add("Block exhausted, target not found");
                return steps.toArray(new String[0]);
            }
        }
        
        if (array[prev] == target) {
            steps.add(String.format("Found! Target %d found at index %d", target, prev));
        } else {
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
            performJumpSearchStep();
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
        prev = 0;
        jumpIndex = 0;
        if (visualizationPane != null) {
            visualizationPane.resetHighlights();
        }
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
    
    public boolean isPaused() {
        return isPaused;
    }
    
    public boolean isStopped() {
        return isStopped;
    }
}
