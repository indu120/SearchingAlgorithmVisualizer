package com.teachingaid.algorithms;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import com.teachingaid.ui.StringVisualizationPane;

import java.util.function.Consumer;

/**
 * Naive String Search Algorithm with step-by-step visualization
 * Also known as Brute Force String Search - compares pattern at every position
 */
public class NaiveStringSearch {
    
    private final Color PATTERN_COLOR = Color.web("#3498DB");
    private final Color TEXT_COLOR = Color.web("#2ECC71");
    private final Color MATCH_COLOR = Color.web("#E74C3C");
    private final Color MISMATCH_COLOR = Color.web("#F39C12");
    private final Color FOUND_COLOR = Color.web("#27AE60");
    
    private boolean isPaused = false;
    private boolean isStopped = false;
    private int currentStep = 0;
    private String text;
    private String pattern;
    private StringVisualizationPane visualizationPane;
    private Consumer<Integer> onComplete;
    
    // Animation timing control
    private int animationDelay = 1000; // base delay in ms
    
    // Search state variables
    private int textIndex = 0;
    private int patternIndex = 0;
    
    public void visualizeSearch(String text, String pattern, StringVisualizationPane visualPane, 
                               Consumer<Integer> completionCallback) {
        this.text = text;
        this.pattern = pattern;
        this.visualizationPane = visualPane;
        this.onComplete = completionCallback;
        this.currentStep = 0;
        this.isStopped = false;
        this.isPaused = false;
        this.textIndex = 0;
        this.patternIndex = 0;
        
        // Reset visualization
        visualPane.resetHighlights();
        visualPane.setInstructionText("Starting Naive String Search for pattern \"" + pattern + "\" in text");
        
        // Start the search
        PauseTransition startTransition = new PauseTransition(Duration.millis(animationDelay));
        startTransition.setOnFinished(e -> performNaiveSearchStep());
        startTransition.play();
    }
    
    private void performNaiveSearchStep() {
        if (isStopped || isPaused) {
            return;
        }
        
        // Check if we've reached the end of possible starting positions
        if (textIndex > text.length() - pattern.length()) {
            // Pattern not found
            Platform.runLater(() -> {
                visualizationPane.markAsNotFound();
                onComplete.accept(-1);
            });
            return;
        }
        
        currentStep++;
        
        Platform.runLater(() -> {
            String stepDesc = String.format("Step %d: Comparing pattern at position %d - text[%d]='%c' with pattern[%d]='%c'", 
                                          currentStep, textIndex, textIndex + patternIndex, 
                                          text.charAt(textIndex + patternIndex), 
                                          patternIndex, pattern.charAt(patternIndex));
            visualizationPane.highlightComparison(textIndex + patternIndex, patternIndex, stepDesc);
        });
        
        PauseTransition comparisonTransition = new PauseTransition(Duration.millis(animationDelay));
        comparisonTransition.setOnFinished(e -> {
            if (isStopped || isPaused) return;
            
            if (text.charAt(textIndex + patternIndex) == pattern.charAt(patternIndex)) {
                // Match found for current character
                patternIndex++;
                
                if (patternIndex == pattern.length()) {
                    // Complete pattern found!
                    Platform.runLater(() -> {
                        visualizationPane.markAsFound(textIndex);
                        onComplete.accept(textIndex);
                    });
                } else {
                    Platform.runLater(() -> {
                        String matchDesc = String.format("Character match! Continue comparing... (%d/%d)", 
                                                        patternIndex, pattern.length());
                        visualizationPane.setInstructionText(matchDesc);
                    });
                    
                    PauseTransition matchTransition = new PauseTransition(Duration.millis(Math.max(200, animationDelay / 2)));
                    matchTransition.setOnFinished(e2 -> performNaiveSearchStep());
                    matchTransition.play();
                }
                
            } else {
                // Mismatch - move to next position in text
                Platform.runLater(() -> {
                    String mismatchDesc = String.format("Mismatch! Moving to next starting position (%d -> %d)", 
                                                      textIndex, textIndex + 1);
                    visualizationPane.setInstructionText(mismatchDesc);
                });
                
                textIndex++;
                patternIndex = 0;
                
                PauseTransition mismatchTransition = new PauseTransition(Duration.millis(Math.max(200, animationDelay / 2)));
                mismatchTransition.setOnFinished(e2 -> performNaiveSearchStep());
                mismatchTransition.play();
            }
        });
        comparisonTransition.play();
    }
    
    public static int naiveSearch(String text, String pattern) {
        if (pattern.isEmpty()) return 0;
        
        for (int i = 0; i <= text.length() - pattern.length(); i++) {
            int j = 0;
            while (j < pattern.length() && text.charAt(i + j) == pattern.charAt(j)) {
                j++;
            }
            if (j == pattern.length()) {
                return i;
            }
        }
        return -1;
    }
    
    public static String getAlgorithmInfo() {
        return "Naive (Brute Force) String Search Algorithm:\n\n" +
               "Description:\n" +
               "The naive approach tries to match the pattern at every possible position " +
               "in the text by comparing characters one by one.\n\n" +
               "Time Complexity:\n" +
               "• Best case: O(n) when pattern found at beginning\n" +
               "• Average case: O(n × m)\n" +
               "• Worst case: O(n × m) where n is text length, m is pattern length\n\n" +
               "Space Complexity: O(1) - no additional space required\n\n" +
               "Key Features:\n" +
               "• Simple and easy to understand\n" +
               "• Works on any text and pattern\n" +
               "• No preprocessing required\n" +
               "• Can be inefficient for large texts\n\n" +
               "Algorithm Steps:\n" +
               "1. Start at first position in text\n" +
               "2. Compare pattern character by character\n" +
               "3. If mismatch occurs, move to next position\n" +
               "4. Repeat until pattern found or text exhausted\n\n" +
               "Use Cases:\n" +
               "• Small texts or patterns\n" +
               "• One-time searches\n" +
               "• Educational purposes\n" +
               "• When simplicity is preferred over efficiency";
    }
    
    public static String[] getSearchTrace(String text, String pattern) {
        java.util.List<String> steps = new java.util.ArrayList<>();
        
        steps.add("Starting Naive Search for pattern \"" + pattern + "\" in text \"" + text + "\"");
        
        int stepNum = 1;
        for (int i = 0; i <= text.length() - pattern.length(); i++) {
            steps.add(String.format("Try position %d:", i));
            
            int j = 0;
            while (j < pattern.length()) {
                steps.add(String.format("Step %d: Compare text[%d]='%c' with pattern[%d]='%c'", 
                         stepNum++, i + j, text.charAt(i + j), j, pattern.charAt(j)));
                
                if (text.charAt(i + j) == pattern.charAt(j)) {
                    steps.add("Match! Continue...");
                    j++;
                } else {
                    steps.add("Mismatch! Try next position");
                    break;
                }
            }
            
            if (j == pattern.length()) {
                steps.add(String.format("Pattern found at index %d!", i));
                return steps.toArray(new String[0]);
            }
        }
        
        steps.add("Pattern not found in text");
        return steps.toArray(new String[0]);
    }
    
    public void pause() {
        isPaused = true;
    }
    
    public void resume() {
        if (isPaused) {
            isPaused = false;
            performNaiveSearchStep();
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
        textIndex = 0;
        patternIndex = 0;
        if (visualizationPane != null) {
            visualizationPane.resetHighlights();
        }
    }
    
    /**
     * Sets the animation delay for visualization speed control
     */
    public void setAnimationDelay(int delay) {
        this.animationDelay = Math.max(200, Math.min(3000, delay));
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
