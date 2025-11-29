package com.teachingaid.mcq;

import java.util.*;
import java.util.stream.Collectors;

/**
 * MCQ Test Engine with adaptive difficulty and intelligent question selection
 */
public class MCQTestEngine {
    private List<MCQQuestion> questionPool;
    private List<MCQQuestion> currentQuestions;
    private int currentQuestionIndex;
    private int score;
    private int correctAnswers;
    private int wrongAnswers;
    private int currentDifficulty;
    private List<String> userAnswers;
    private List<Boolean> answerCorrectness;
    private List<Integer> questionDifficulties;
    private Random random;
    
    // Adaptive difficulty parameters
    private static final int MIN_DIFFICULTY = 1;
    private static final int MAX_DIFFICULTY = 5;
    private static final int QUESTIONS_PER_LEVEL = 3; // Questions before considering difficulty change
    private int questionsAtCurrentLevel;
    private double accuracyThreshold = 0.7; // 70% accuracy to increase difficulty
    
    public MCQTestEngine() {
        this.questionPool = MCQQuestionBank.getAllQuestions();
        this.currentQuestions = new ArrayList<>();
        this.currentQuestionIndex = 0;
        this.score = 0;
        this.correctAnswers = 0;
        this.wrongAnswers = 0;
        this.currentDifficulty = 1; // Start with easy questions
        this.userAnswers = new ArrayList<>();
        this.answerCorrectness = new ArrayList<>();
        this.questionDifficulties = new ArrayList<>();
        this.random = new Random();
        this.questionsAtCurrentLevel = 0;
        
        initializeTest();
    }
    
    private void initializeTest() {
        // Start with easy questions
        generateNextQuestions(10); // Generate initial set of questions
    }
    
    private void generateNextQuestions(int count) {
        List<MCQQuestion> availableQuestions = questionPool.stream()
            .filter(q -> q.getDifficulty() == currentDifficulty)
            .filter(q -> !currentQuestions.contains(q)) // Avoid repeats
            .collect(Collectors.toList());
        
        // If not enough questions at current difficulty, include adjacent difficulties
        if (availableQuestions.size() < count) {
            int lowerDiff = Math.max(MIN_DIFFICULTY, currentDifficulty - 1);
            int higherDiff = Math.min(MAX_DIFFICULTY, currentDifficulty + 1);
            
            List<MCQQuestion> additionalQuestions = questionPool.stream()
                .filter(q -> (q.getDifficulty() == lowerDiff || q.getDifficulty() == higherDiff))
                .filter(q -> !currentQuestions.contains(q))
                .collect(Collectors.toList());
            
            availableQuestions.addAll(additionalQuestions);
        }
        
        // Shuffle and select questions
        Collections.shuffle(availableQuestions, random);
        int questionsToAdd = Math.min(count, availableQuestions.size());
        
        for (int i = 0; i < questionsToAdd; i++) {
            currentQuestions.add(availableQuestions.get(i));
        }
    }
    
    public MCQQuestion getCurrentQuestion() {
        if (currentQuestionIndex < currentQuestions.size()) {
            return currentQuestions.get(currentQuestionIndex);
        }
        return null;
    }
    
    public boolean submitAnswer(int selectedOption) {
        MCQQuestion currentQuestion = getCurrentQuestion();
        if (currentQuestion == null) return false;
        
        boolean isCorrect = currentQuestion.isCorrect(selectedOption);
        String selectedAnswer = currentQuestion.getOptions()[selectedOption];
        
        // Record answer
        userAnswers.add(selectedAnswer);
        answerCorrectness.add(isCorrect);
        questionDifficulties.add(currentQuestion.getDifficulty());
        
        // Update scores
        if (isCorrect) {
            correctAnswers++;
            score += currentQuestion.getPoints();
        } else {
            wrongAnswers++;
        }
        
        questionsAtCurrentLevel++;
        
        // Move to next question
        currentQuestionIndex++;
        
        // Check if we need more questions
        if (currentQuestionIndex >= currentQuestions.size()) {
            generateNextQuestions(5); // Generate more questions as needed
        }
        
        // Adaptive difficulty adjustment
        adjustDifficulty();
        
        return isCorrect;
    }
    
    private void adjustDifficulty() {
        // Only adjust after a few questions at current level
        if (questionsAtCurrentLevel >= QUESTIONS_PER_LEVEL) {
            double recentAccuracy = calculateRecentAccuracy();
            
            if (recentAccuracy >= accuracyThreshold && currentDifficulty < MAX_DIFFICULTY) {
                // Increase difficulty
                currentDifficulty++;
                questionsAtCurrentLevel = 0;
            } else if (recentAccuracy < 0.4 && currentDifficulty > MIN_DIFFICULTY) {
                // Decrease difficulty if performance is poor
                currentDifficulty--;
                questionsAtCurrentLevel = 0;
            }
        }
    }
    
    private double calculateRecentAccuracy() {
        int recentQuestions = Math.min(QUESTIONS_PER_LEVEL, answerCorrectness.size());
        if (recentQuestions == 0) return 0.0;
        
        int recentCorrect = 0;
        for (int i = answerCorrectness.size() - recentQuestions; i < answerCorrectness.size(); i++) {
            if (answerCorrectness.get(i)) {
                recentCorrect++;
            }
        }
        
        return (double) recentCorrect / recentQuestions;
    }
    
    public boolean hasNextQuestion() {
        // Allow questions up to the size of the question pool, picked randomly by difficulty/topic
        return currentQuestionIndex < questionPool.size() && !currentQuestions.isEmpty();
    }
    
    public TestResult getTestResult() {
        double accuracy = getTotalQuestions() > 0 ? (double) correctAnswers / getTotalQuestions() : 0.0;
        int maxPossibleScore = calculateMaxPossibleScore();
        
        return new TestResult(
            score,
            correctAnswers,
            wrongAnswers,
            getTotalQuestions(),
            accuracy,
            getDifficultyDistribution(),
            getCategoryDistribution(),
            maxPossibleScore
        );
    }
    
    private int calculateMaxPossibleScore() {
        return currentQuestions.stream()
            .limit(getTotalQuestions())
            .mapToInt(MCQQuestion::getPoints)
            .sum();
    }
    
    private Map<Integer, Integer> getDifficultyDistribution() {
        Map<Integer, Integer> distribution = new HashMap<>();
        for (int i = 0; i < Math.min(getTotalQuestions(), questionDifficulties.size()); i++) {
            int difficulty = questionDifficulties.get(i);
            distribution.put(difficulty, distribution.getOrDefault(difficulty, 0) + 1);
        }
        return distribution;
    }
    
    private Map<String, Integer> getCategoryDistribution() {
        Map<String, Integer> distribution = new HashMap<>();
        for (int i = 0; i < Math.min(getTotalQuestions(), currentQuestions.size()); i++) {
            String category = currentQuestions.get(i).getCategory();
            distribution.put(category, distribution.getOrDefault(category, 0) + 1);
        }
        return distribution;
    }
    
    // Getters
    public int getCurrentQuestionNumber() {
        return currentQuestionIndex + 1;
    }
    
    public int getTotalQuestions() {
        return Math.min(currentQuestionIndex, answerCorrectness.size());
    }
    
    public int getScore() {
        return score;
    }
    
    public int getCorrectAnswers() {
        return correctAnswers;
    }
    
    public int getWrongAnswers() {
        return wrongAnswers;
    }
    
    public int getCurrentDifficulty() {
        return currentDifficulty;
    }
    
    public double getCurrentAccuracy() {
        return getTotalQuestions() > 0 ? (double) correctAnswers / getTotalQuestions() : 0.0;
    }
    
    public String getDifficultyName(int difficulty) {
        switch (difficulty) {
            case 1: return "Easy";
            case 2: return "Medium";
            case 3: return "Hard";
            case 4: return "Expert";
            case 5: return "Master";
            default: return "Unknown";
        }
    }
    
    // Inner class for test results
    public static class TestResult {
        private final int finalScore;
        private final int correctAnswers;
        private final int wrongAnswers;
        private final int totalQuestions;
        private final double accuracy;
        private final Map<Integer, Integer> difficultyDistribution;
        private final Map<String, Integer> categoryDistribution;
        private final int maxPossibleScore;
        
        public TestResult(int finalScore, int correctAnswers, int wrongAnswers, int totalQuestions,
                         double accuracy, Map<Integer, Integer> difficultyDistribution,
                         Map<String, Integer> categoryDistribution, int maxPossibleScore) {
            this.finalScore = finalScore;
            this.correctAnswers = correctAnswers;
            this.wrongAnswers = wrongAnswers;
            this.totalQuestions = totalQuestions;
            this.accuracy = accuracy;
            this.difficultyDistribution = difficultyDistribution;
            this.categoryDistribution = categoryDistribution;
            this.maxPossibleScore = maxPossibleScore;
        }
        
        // Getters
        public int getFinalScore() { return finalScore; }
        public int getCorrectAnswers() { return correctAnswers; }
        public int getWrongAnswers() { return wrongAnswers; }
        public int getTotalQuestions() { return totalQuestions; }
        public double getAccuracy() { return accuracy; }
        public Map<Integer, Integer> getDifficultyDistribution() { return difficultyDistribution; }
        public Map<String, Integer> getCategoryDistribution() { return categoryDistribution; }
        public int getMaxPossibleScore() { return maxPossibleScore; }
        
        public String getGrade() {
            double percentage = (double) finalScore / maxPossibleScore * 100;
            if (percentage >= 90) return "A+";
            else if (percentage >= 80) return "A";
            else if (percentage >= 70) return "B";
            else if (percentage >= 60) return "C";
            else if (percentage >= 50) return "D";
            else return "F";
        }
    }
}
