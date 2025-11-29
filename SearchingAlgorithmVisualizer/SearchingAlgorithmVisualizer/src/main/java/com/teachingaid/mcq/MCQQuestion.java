package com.teachingaid.mcq;

/**
 * Represents a single MCQ question with options and metadata
 */
public class MCQQuestion {
    private String question;
    private String[] options;
    private int correctAnswerIndex;
    private int difficulty; // 1=Easy, 2=Medium, 3=Hard, 4=Expert, 5=Master
    private String category; // e.g., "Linear Search", "Binary Search", etc.
    private String explanation;
    private int points;

    public MCQQuestion(String question, String[] options, int correctAnswerIndex, 
                      int difficulty, String category, String explanation, int points) {
        this.question = question;
        this.options = options.clone();
        this.correctAnswerIndex = correctAnswerIndex;
        this.difficulty = difficulty;
        this.category = category;
        this.explanation = explanation;
        this.points = points;
    }

    // Getters
    public String getQuestion() { return question; }
    public String[] getOptions() { return options.clone(); }
    public int getCorrectAnswerIndex() { return correctAnswerIndex; }
    public int getDifficulty() { return difficulty; }
    public String getCategory() { return category; }
    public String getExplanation() { return explanation; }
    public int getPoints() { return points; }

    // Helper method to get correct answer text
    public String getCorrectAnswer() {
        return options[correctAnswerIndex];
    }

    public boolean isCorrect(int selectedIndex) {
        return selectedIndex == correctAnswerIndex;
    }
}
