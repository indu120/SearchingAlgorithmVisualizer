package com.teachingaid.algorithms;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import com.teachingaid.ui.StringVisualizationPane;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Boyer-Moore String Search Algorithm with step-by-step visualization
 * Uses Bad Character Rule for efficient pattern matching
 */
public class BoyerMooreStringSearch {
    
    private final Color PATTERN_COLOR = Color.web("#3498DB");
    private final Color TEXT_COLOR = Color.web("#2ECC71");
    private final Color MATCH_COLOR = Color.web("#E74C3C");
    private final Color MISMATCH_COLOR = Color.web("#F39C12");
    private final Color FOUND_COLOR = Color.web("#27AE60");
    private final Color SKIP_COLOR = Color.web("#9B59B6");
    
    private boolean isPaused = false;
    private boolean isStopped = false;
    private int currentStep = 0;
    private String text;
    private String pattern;
    private StringVisualizationPane visualizationPane;
    private Consumer<Integer> onComplete;
    
    // Animation timing control
    private int animationDelay = 1000; // base delay in ms
    
    // Boyer-Moore state variables
    private Map<Character, Integer> badCharTable;
    private int textIndex = 0;
    private boolean tableBuilt = false;
    
    public void visualizeSearch(String text, String pattern, StringVisualizationPane visualPane, 
                               Consumer<Integer> completionCallback) {
        this.text = text;
        this.pattern = pattern;
        this.visualizationPane = visualPane;
        this.onComplete = completionCallback;
        this.currentStep = 0;
        this.isStopped = false;
        this.isPaused = false;
        this.textIndex = pattern.length() - 1; // Start from the end of pattern
        this.tableBuilt = false;
        
        // Reset visualization
        visualPane.resetHighlights();
        visualPane.setInstructionText("Starting Boyer-Moore String Search for pattern \"" + pattern + "\" in text");
        
        // Build bad character table first
        buildBadCharacterTable();
    }
    
    private void buildBadCharacterTable() {
        badCharTable = new HashMap<>();
        
        Platform.runLater(() -> {
            visualizationPane.setInstructionText("Building Bad Character Table for pattern: " + pattern);
        });
        
        PauseTransition startTransition = new PauseTransition(Duration.millis(animationDelay));
        startTransition.setOnFinished(e -> {
            // Build the table
            for (int i = 0; i < pattern.length() - 1; i++) {
                badCharTable.put(pattern.charAt(i), pattern.length() - 1 - i);
            }
            
            Platform.runLater(() -> {
                StringBuilder tableInfo = new StringBuilder("Bad Character Table built:\n");
                for (Map.Entry<Character, Integer> entry : badCharTable.entrySet()) {
                    tableInfo.append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n");
                }
                visualizationPane.setInstructionText(tableInfo.toString() + "Now starting pattern matching...");
            });
            
            tableBuilt = true;
            
            PauseTransition matchingTransition = new PauseTransition(Duration.millis(Math.max(400, animationDelay * 2L)));
            matchingTransition.setOnFinished(e2 -> performBoyerMooreSearchStep());
            matchingTransition.play();
        });
        startTransition.play();
    }
    
    private void performBoyerMooreSearchStep() {
        if (isStopped || isPaused) {
            return;
        }
        
        // Check if we've gone beyond the text
        if (textIndex >= text.length()) {
            // Pattern not found
            Platform.runLater(() -> {
                visualizationPane.markAsNotFound();
                onComplete.accept(-1);
            });
            return;
        }
        
        currentStep++;
        int startPos = textIndex - pattern.length() + 1;
        
        Platform.runLater(() -> {
            String stepDesc = String.format("Step %d: Comparing pattern from right to left at position %d", 
                                          currentStep, startPos);
            visualizationPane.setInstructionText(stepDesc);
        });
        
        // Compare pattern from right to left
        compareFromRight(startPos);
    }
    
    private void compareFromRight(int startPos) {
        int patternIndex = pattern.length() - 1;
        int currentTextPos = startPos + patternIndex;
        
        Platform.runLater(() -> {
            String compareDesc = String.format("Comparing text[%d]='%c' with pattern[%d]='%c'", 
                                             currentTextPos, text.charAt(currentTextPos),
                                             patternIndex, pattern.charAt(patternIndex));
            visualizationPane.highlightComparison(currentTextPos, patternIndex, compareDesc);
        });
        
        PauseTransition comparisonTransition = new PauseTransition(Duration.millis(animationDelay));
        comparisonTransition.setOnFinished(e -> {
            if (isStopped || isPaused) return;
            
            if (text.charAt(currentTextPos) == pattern.charAt(patternIndex)) {
                // Character matches, check if we've matched the entire pattern
                if (patternIndex == 0) {
                    // Complete match found!
                    Platform.runLater(() -> {
                        visualizationPane.markAsFound(startPos);
                        onComplete.accept(startPos);
                    });
                } else {
                    // Continue matching from right to left
                    continueMatching(startPos, patternIndex - 1);
                }
            } else {
                // Mismatch - use bad character rule
                char badChar = text.charAt(currentTextPos);
                int skip = badCharTable.getOrDefault(badChar, pattern.length());
                
                Platform.runLater(() -> {
                    String mismatchDesc = String.format("Mismatch! Bad character '%c' - skipping %d positions", 
                                                      badChar, skip);
                    visualizationPane.setInstructionText(mismatchDesc);
                });
                
                textIndex += skip;
                
                PauseTransition skipTransition = new PauseTransition(Duration.millis(Math.max(300, animationDelay * 6L / 5L)));
                skipTransition.setOnFinished(e2 -> performBoyerMooreSearchStep());
                skipTransition.play();
            }
        });
        comparisonTransition.play();
    }
    
    private void continueMatching(int startPos, int patternIndex) {
        if (patternIndex < 0) {
            // Complete match found!
            Platform.runLater(() -> {
                visualizationPane.markAsFound(startPos);
                onComplete.accept(startPos);
            });
            return;
        }
        
        int currentTextPos = startPos + patternIndex;
        
        Platform.runLater(() -> {
            String compareDesc = String.format("Continue matching: text[%d]='%c' with pattern[%d]='%c'", 
                                             currentTextPos, text.charAt(currentTextPos),
                                             patternIndex, pattern.charAt(patternIndex));
            visualizationPane.highlightComparison(currentTextPos, patternIndex, compareDesc);
        });
        
        PauseTransition comparisonTransition = new PauseTransition(Duration.millis(Math.max(200, animationDelay / 2)));
        comparisonTransition.setOnFinished(e -> {
            if (isStopped || isPaused) return;
            
            if (text.charAt(currentTextPos) == pattern.charAt(patternIndex)) {
                // Match continues
                continueMatching(startPos, patternIndex - 1);
            } else {
                // Mismatch during continued matching
                char badChar = text.charAt(startPos + pattern.length() - 1); // Use rightmost character
                int skip = badCharTable.getOrDefault(badChar, pattern.length());
                
                Platform.runLater(() -> {
                    String mismatchDesc = String.format("Mismatch during matching! Skipping %d positions", skip);
                    visualizationPane.setInstructionText(mismatchDesc);
                });
                
                textIndex += skip;
                
                PauseTransition skipTransition = new PauseTransition(Duration.millis(animationDelay));
                skipTransition.setOnFinished(e2 -> performBoyerMooreSearchStep());
                skipTransition.play();
            }
        });
        comparisonTransition.play();
    }
    
    public static int boyerMooreSearch(String text, String pattern) {
        if (pattern.isEmpty()) return 0;
        
        // Build bad character table
        Map<Character, Integer> badCharTable = new HashMap<>();
        for (int i = 0; i < pattern.length() - 1; i++) {
            badCharTable.put(pattern.charAt(i), pattern.length() - 1 - i);
        }
        
        int textIndex = pattern.length() - 1;
        
        while (textIndex < text.length()) {
            int startPos = textIndex - pattern.length() + 1;
            int patternIndex = pattern.length() - 1;
            
            // Compare from right to left
            while (patternIndex >= 0 && text.charAt(startPos + patternIndex) == pattern.charAt(patternIndex)) {
                patternIndex--;
            }
            
            if (patternIndex < 0) {
                return startPos; // Pattern found
            }
            
            // Bad character rule
            char badChar = text.charAt(textIndex);
            int skip = badCharTable.getOrDefault(badChar, pattern.length());
            textIndex += skip;
        }
        
        return -1;
    }
    
    public static String getAlgorithmInfo() {
        return "Boyer-Moore String Search Algorithm:\n\n" +
               "Description:\n" +
               "Boyer-Moore searches by comparing the pattern from right to left and uses " +
               "preprocessing to skip characters efficiently when mismatches occur.\n\n" +
               "Time Complexity:\n" +
               "• Best case: O(n/m) - sublinear when pattern doesn't occur\n" +
               "• Average case: O(n)\n" +
               "• Worst case: O(n × m) - rare, when pattern and text have many repetitions\n\n" +
               "Space Complexity: O(σ) where σ is alphabet size\n\n" +
               "Key Features:\n" +
               "• Compares pattern from right to left\n" +
               "• Uses Bad Character Rule for skipping\n" +
               "• Can be sublinear in best case\n" +
               "• Preprocessing creates lookup table\n\n" +
               "Algorithm Steps:\n" +
               "1. Preprocess pattern to create bad character table\n" +
               "2. Align pattern with text from left\n" +
               "3. Compare from rightmost character of pattern\n" +
               "4. On mismatch, use bad character rule to skip\n\n" +
               "Use Cases:\n" +
               "• Text editors (find/replace)\n" +
               "• Large document searching\n" +
               "• DNA sequence analysis\n" +
               "• Network packet inspection";
    }
    
    public static String[] getSearchTrace(String text, String pattern) {
        java.util.List<String> steps = new java.util.ArrayList<>();
        
        steps.add("Starting Boyer-Moore Search for pattern \"" + pattern + "\" in text \"" + text + "\"");
        
        // Build bad character table trace
        Map<Character, Integer> badCharTable = new HashMap<>();
        for (int i = 0; i < pattern.length() - 1; i++) {
            badCharTable.put(pattern.charAt(i), pattern.length() - 1 - i);
        }
        steps.add("Bad Character Table: " + badCharTable.toString());
        
        int textIndex = pattern.length() - 1;
        int stepNum = 1;
        
        while (textIndex < text.length()) {
            int startPos = textIndex - pattern.length() + 1;
            steps.add(String.format("Try position %d:", startPos));
            
            int patternIndex = pattern.length() - 1;
            boolean foundMismatch = false;
            
            // Compare from right to left
            while (patternIndex >= 0) {
                steps.add(String.format("Step %d: Compare text[%d]='%c' with pattern[%d]='%c'", 
                         stepNum++, startPos + patternIndex, text.charAt(startPos + patternIndex), 
                         patternIndex, pattern.charAt(patternIndex)));
                
                if (text.charAt(startPos + patternIndex) == pattern.charAt(patternIndex)) {
                    steps.add("Match! Continue...");
                    patternIndex--;
                } else {
                    foundMismatch = true;
                    char badChar = text.charAt(textIndex);
                    int skip = badCharTable.getOrDefault(badChar, pattern.length());
                    steps.add(String.format("Mismatch! Bad character '%c' - skip %d positions", badChar, skip));
                    textIndex += skip;
                    break;
                }
            }
            
            if (!foundMismatch) {
                steps.add(String.format("Pattern found at index %d!", startPos));
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
            if (!tableBuilt) {
                buildBadCharacterTable();
            } else {
                performBoyerMooreSearchStep();
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
        textIndex = 0;
        tableBuilt = false;
        badCharTable = null;
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
        this.animationDelay = Math.max(200, Math.min(3000, delay));
    }
    
    public int getAnimationDelay() {
        return animationDelay;
    }
}
