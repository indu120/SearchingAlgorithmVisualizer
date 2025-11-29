package com.teachingaid.algorithms;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import com.teachingaid.ui.StringVisualizationPane;

import java.util.function.Consumer;

/**
 * Knuth-Morris-Pratt String Search Algorithm with step-by-step visualization
 * Shows the failure function (partial match table) construction and pattern matching
 */
public class KMPStringSearch {
    
    private final Color PATTERN_COLOR = Color.web("#3498DB");
    private final Color TEXT_COLOR = Color.web("#2ECC71");
    private final Color MATCH_COLOR = Color.web("#E74C3C");
    private final Color MISMATCH_COLOR = Color.web("#F39C12");
    private final Color FAILURE_COLOR = Color.web("#9B59B6");
    private final Color FOUND_COLOR = Color.web("#27AE60");
    
    private boolean isPaused = false;
    private boolean isStopped = false;
    private int currentStep = 0;
    private String text;
    private String pattern;
    private StringVisualizationPane visualizationPane;
    private Consumer<Integer> onComplete;
    
    // Animation timing control
    private int animationDelay = 1000; // Default delay in milliseconds
    
    // KMP state variables
    private int[] failure;
    private boolean failureFunctionBuilt = false;
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
        this.failureFunctionBuilt = false;
        this.textIndex = 0;
        this.patternIndex = 0;
        
        // Reset visualization
        visualPane.resetHighlights();
        visualPane.setInstructionText("Starting KMP String Search for pattern \"" + pattern + "\" in text");
        
        // Start by building the failure function
        buildFailureFunction();
    }
    
    private void buildFailureFunction() {
        failure = new int[pattern.length()];
        Platform.runLater(() -> {
            visualizationPane.setInstructionText("Building failure function (partial match table) for pattern: " + pattern);
            visualizationPane.showFailureFunction(failure, pattern);
        });
        
        PauseTransition startTransition = new PauseTransition(Duration.millis(animationDelay + 500));
        startTransition.setOnFinished(e -> buildFailureFunctionStep(0, 0));
        startTransition.play();
    }
    
    private void buildFailureFunctionStep(int i, int j) {
        if (isStopped || isPaused) {
            return;
        }
        
        currentStep++;
        
        if (i >= pattern.length()) {
            // Failure function construction complete
            failureFunctionBuilt = true;
            Platform.runLater(() -> {
                visualizationPane.setInstructionText("Failure function complete! Now starting pattern matching...");
                visualizationPane.showFailureFunction(failure, pattern);
            });
            
            PauseTransition matchingTransition = new PauseTransition(Duration.millis(1500));
            matchingTransition.setOnFinished(e -> performKMPSearchStep());
            matchingTransition.play();
            return;
        }
        
        if (i == 0) {
            failure[0] = 0;
            Platform.runLater(() -> {
                visualizationPane.highlightFailureConstruction(0, failure[0], "failure[0] = 0 (base case)");
            });
            
            PauseTransition baseTransition = new PauseTransition(Duration.millis(animationDelay));
            baseTransition.setOnFinished(e -> buildFailureFunctionStep(1, 0));
            baseTransition.play();
            return;
        }
        
        final int currentI = i;
        final int currentJ = j;
        
        Platform.runLater(() -> {
            String stepDesc = String.format("Building failure[%d]: comparing pattern[%d]='%c' with pattern[%d]='%c'", 
                                          currentI, currentI, pattern.charAt(currentI), currentJ, pattern.charAt(currentJ));
            visualizationPane.highlightFailureConstruction(currentI, currentJ, stepDesc);
        });
        
        PauseTransition comparisonTransition = new PauseTransition(Duration.millis(animationDelay));
        comparisonTransition.setOnFinished(e -> {
            if (isStopped || isPaused) return;
            
            if (pattern.charAt(currentI) == pattern.charAt(currentJ)) {
                // Match found
                int newJ = currentJ + 1;
                failure[currentI] = newJ;
                
                Platform.runLater(() -> {
                    String matchDesc = String.format("Match! failure[%d] = %d", currentI, newJ);
                    visualizationPane.highlightFailureConstruction(currentI, failure[currentI], matchDesc);
                });
                
                PauseTransition matchTransition = new PauseTransition(Duration.millis(800));
                matchTransition.setOnFinished(e2 -> buildFailureFunctionStep(currentI + 1, newJ));
                matchTransition.play();
                
            } else {
                // Mismatch
                if (currentJ != 0) {
                    int newJ = failure[currentJ - 1];
                    
                    Platform.runLater(() -> {
                        String mismatchDesc = String.format("Mismatch! Using failure[%d] = %d", currentJ - 1, failure[currentJ - 1]);
                        visualizationPane.highlightFailureConstruction(currentI, currentJ, mismatchDesc);
                    });
                    
                    PauseTransition mismatchTransition = new PauseTransition(Duration.millis(800));
                    mismatchTransition.setOnFinished(e2 -> buildFailureFunctionStep(currentI, newJ));
                    mismatchTransition.play();
                    
                } else {
                    failure[currentI] = 0;
                    Platform.runLater(() -> {
                        String noMatchDesc = String.format("No match, failure[%d] = 0", currentI);
                        visualizationPane.highlightFailureConstruction(currentI, failure[currentI], noMatchDesc);
                    });
                    
                    PauseTransition noMatchTransition = new PauseTransition(Duration.millis(800));
                    noMatchTransition.setOnFinished(e2 -> buildFailureFunctionStep(currentI + 1, 0));
                    noMatchTransition.play();
                }
            }
        });
        comparisonTransition.play();
    }
    
    private void performKMPSearchStep() {
        if (isStopped || isPaused) {
            return;
        }
        
        if (textIndex >= text.length()) {
            // Pattern not found
            Platform.runLater(() -> {
                visualizationPane.markAsNotFound();
                onComplete.accept(-1);
            });
            return;
        }
        
        currentStep++;
        
        Platform.runLater(() -> {
            String stepDesc = String.format("Step %d: Comparing text[%d]='%c' with pattern[%d]='%c'", 
                                          currentStep, textIndex, text.charAt(textIndex), 
                                          patternIndex, pattern.charAt(patternIndex));
            visualizationPane.highlightComparison(textIndex, patternIndex, stepDesc);
        });
        
        PauseTransition comparisonTransition = new PauseTransition(Duration.millis(animationDelay));
        comparisonTransition.setOnFinished(e -> {
            if (isStopped || isPaused) return;
            
            if (text.charAt(textIndex) == pattern.charAt(patternIndex)) {
                // Match
                textIndex++;
                patternIndex++;
                
                if (patternIndex == pattern.length()) {
                    // Pattern found!
                    int foundIndex = textIndex - pattern.length();
                    Platform.runLater(() -> {
                        visualizationPane.markAsFound(foundIndex);
                        onComplete.accept(foundIndex);
                    });
                } else {
                    Platform.runLater(() -> {
                        String matchDesc = String.format("Match! Continue comparing... (%d/%d)", 
                                                        patternIndex, pattern.length());
                        visualizationPane.setInstructionText(matchDesc);
                    });
                    
                    PauseTransition matchTransition = new PauseTransition(Duration.millis(600));
                    matchTransition.setOnFinished(e2 -> performKMPSearchStep());
                    matchTransition.play();
                }
                
            } else {
                // Mismatch
                if (patternIndex != 0) {
                    Platform.runLater(() -> {
                        String mismatchDesc = String.format("Mismatch! Using failure[%d] = %d to skip", 
                                                          patternIndex - 1, failure[patternIndex - 1]);
                        visualizationPane.setInstructionText(mismatchDesc);
                        visualizationPane.highlightFailureUse(patternIndex - 1, failure[patternIndex - 1]);
                    });
                    
                    patternIndex = failure[patternIndex - 1];
                    
                    PauseTransition mismatchTransition = new PauseTransition(Duration.millis(1000));
                    mismatchTransition.setOnFinished(e2 -> performKMPSearchStep());
                    mismatchTransition.play();
                    
                } else {
                    textIndex++;
                    Platform.runLater(() -> {
                        visualizationPane.setInstructionText("No partial match, move to next character");
                    });
                    
                    PauseTransition nextTransition = new PauseTransition(Duration.millis(600));
                    nextTransition.setOnFinished(e2 -> performKMPSearchStep());
                    nextTransition.play();
                }
            }
        });
        comparisonTransition.play();
    }
    
    public static int kmpSearch(String text, String pattern) {
        if (pattern.isEmpty()) return 0;
        
        // Build failure function
        int[] failure = buildFailureFunction(pattern);
        
        int textIndex = 0;
        int patternIndex = 0;
        
        while (textIndex < text.length()) {
            if (text.charAt(textIndex) == pattern.charAt(patternIndex)) {
                textIndex++;
                patternIndex++;
                
                if (patternIndex == pattern.length()) {
                    return textIndex - pattern.length();
                }
            } else {
                if (patternIndex != 0) {
                    patternIndex = failure[patternIndex - 1];
                } else {
                    textIndex++;
                }
            }
        }
        
        return -1;
    }
    
    private static int[] buildFailureFunction(String pattern) {
        int[] failure = new int[pattern.length()];
        if (pattern.length() == 0) return failure;
        
        failure[0] = 0;
        int i = 1;
        int j = 0;
        
        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(j)) {
                failure[i] = j + 1;
                i++;
                j++;
            } else {
                if (j != 0) {
                    j = failure[j - 1];
                } else {
                    failure[i] = 0;
                    i++;
                }
            }
        }
        
        return failure;
    }
    
    public static String getAlgorithmInfo() {
        return "Knuth-Morris-Pratt (KMP) String Search Algorithm:\n\n" +
               "Description:\n" +
               "KMP algorithm searches for a pattern in text by preprocessing the pattern " +
               "to create a failure function that allows skipping characters during mismatch.\n\n" +
               "Time Complexity:\n" +
               "• Preprocessing: O(m) where m is pattern length\n" +
               "• Searching: O(n) where n is text length\n" +
               "• Overall: O(n + m)\n\n" +
               "Space Complexity: O(m) for failure function\n\n" +
               "Key Features:\n" +
               "• Never backtracks in the text\n" +
               "• Uses failure function for efficient skipping\n" +
               "• Optimal for single pattern matching\n" +
               "• Guarantees linear time complexity\n\n" +
               "Algorithm Steps:\n" +
               "1. Build failure function (partial match table)\n" +
               "2. Search using the failure function for mismatches\n" +
               "3. Never move backwards in the text\n\n" +
               "Use Cases:\n" +
               "• Text editors and word processors\n" +
               "• DNA sequence analysis\n" +
               "• Network intrusion detection\n" +
               "• Compiler design (lexical analysis)";
    }
    
    public static String[] getSearchTrace(String text, String pattern) {
        java.util.List<String> steps = new java.util.ArrayList<>();
        
        steps.add("Starting KMP Search for pattern \"" + pattern + "\" in text \"" + text + "\"");
        
        // Build failure function trace
        int[] failure = buildFailureFunction(pattern);
        steps.add("Failure function: " + java.util.Arrays.toString(failure));
        
        int textIndex = 0;
        int patternIndex = 0;
        int stepNum = 1;
        
        while (textIndex < text.length()) {
            steps.add(String.format("Step %d: Compare text[%d]='%c' with pattern[%d]='%c'", 
                     stepNum++, textIndex, text.charAt(textIndex), patternIndex, pattern.charAt(patternIndex)));
            
            if (text.charAt(textIndex) == pattern.charAt(patternIndex)) {
                steps.add("Match! Advance both pointers");
                textIndex++;
                patternIndex++;
                
                if (patternIndex == pattern.length()) {
                    int foundIndex = textIndex - pattern.length();
                    steps.add(String.format("Pattern found at index %d!", foundIndex));
                    break;
                }
            } else {
                if (patternIndex != 0) {
                    steps.add(String.format("Mismatch! Use failure[%d] = %d", patternIndex - 1, failure[patternIndex - 1]));
                    patternIndex = failure[patternIndex - 1];
                } else {
                    steps.add("No partial match, advance text pointer");
                    textIndex++;
                }
            }
        }
        
        if (textIndex >= text.length() && patternIndex != pattern.length()) {
            steps.add("Pattern not found in text");
        }
        
        return steps.toArray(new String[0]);
    }
    
    public void pause() {
        isPaused = true;
    }
    
    public void resume() {
        if (isPaused) {
            isPaused = false;
            if (!failureFunctionBuilt) {
                // Continue building failure function
                buildFailureFunctionStep(1, 0); // This needs proper state restoration
            } else {
                performKMPSearchStep();
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
        failureFunctionBuilt = false;
        textIndex = 0;
        patternIndex = 0;
        failure = null;
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
     * @param delay The delay in milliseconds (200-3000ms recommended)
     */
    public void setAnimationDelay(int delay) {
        this.animationDelay = Math.max(200, Math.min(3000, delay)); // Clamp between 200ms and 3000ms
    }
    
    /**
     * Gets the current animation delay
     * @return The current delay in milliseconds
     */
    public int getAnimationDelay() {
        return animationDelay;
    }
}
