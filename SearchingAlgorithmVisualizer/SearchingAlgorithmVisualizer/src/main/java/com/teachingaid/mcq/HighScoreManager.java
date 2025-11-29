package com.teachingaid.mcq;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages high scores with persistence to file system
 */
public class HighScoreManager {
    private static final String HIGH_SCORES_FILE = "mcq_high_scores.dat";
    private static final int MAX_HIGH_SCORES = 10;
    private List<HighScoreEntry> highScores;
    private Path highScoresPath;

    public HighScoreManager() {
        // Create high scores file in user's home directory
        String userHome = System.getProperty("user.home");
        highScoresPath = Paths.get(userHome, ".search_visualizer", HIGH_SCORES_FILE);
        
        // Ensure directory exists
        try {
            Files.createDirectories(highScoresPath.getParent());
        } catch (IOException e) {
            System.err.println("Failed to create high scores directory: " + e.getMessage());
        }
        
        highScores = new ArrayList<>();
        loadHighScores();
    }

    public boolean isNewHighScore(int score) {
        if (highScores.size() < MAX_HIGH_SCORES) {
            return true;
        }
        return score > highScores.get(highScores.size() - 1).getScore();
    }

    public void addHighScore(String playerName, int score, double accuracy, 
                           int questionsAnswered, String grade) {
        HighScoreEntry newEntry = new HighScoreEntry(
            playerName, 
            score, 
            accuracy, 
            questionsAnswered, 
            grade, 
            LocalDateTime.now()
        );
        
        highScores.add(newEntry);
        highScores.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        
        // Keep only top scores
        if (highScores.size() > MAX_HIGH_SCORES) {
            highScores = highScores.subList(0, MAX_HIGH_SCORES);
        }
        
        saveHighScores();
    }

    public List<HighScoreEntry> getHighScores() {
        return new ArrayList<>(highScores);
    }

    public HighScoreEntry getBestScore() {
        return highScores.isEmpty() ? null : highScores.get(0);
    }

    public int getRank(int score) {
        int rank = 1;
        for (HighScoreEntry entry : highScores) {
            if (score > entry.getScore()) {
                return rank;
            }
            rank++;
        }
        return rank;
    }

    private void loadHighScores() {
        try {
            if (Files.exists(highScoresPath)) {
                List<String> lines = Files.readAllLines(highScoresPath);
                for (String line : lines) {
                    HighScoreEntry entry = HighScoreEntry.fromString(line);
                    if (entry != null) {
                        highScores.add(entry);
                    }
                }
                
                // Sort by score descending
                highScores.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
            }
        } catch (IOException e) {
            System.err.println("Failed to load high scores: " + e.getMessage());
        }
    }

    private void saveHighScores() {
        try {
            List<String> lines = highScores.stream()
                .map(HighScoreEntry::toString)
                .collect(Collectors.toList());
            
            Files.write(highScoresPath, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Failed to save high scores: " + e.getMessage());
        }
    }

    public void clearHighScores() {
        highScores.clear();
        try {
            Files.deleteIfExists(highScoresPath);
        } catch (IOException e) {
            System.err.println("Failed to clear high scores: " + e.getMessage());
        }
    }

    // High Score Entry class
    public static class HighScoreEntry {
        private String playerName;
        private int score;
        private double accuracy;
        private int questionsAnswered;
        private String grade;
        private LocalDateTime dateTime;
        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        public HighScoreEntry(String playerName, int score, double accuracy, 
                            int questionsAnswered, String grade, LocalDateTime dateTime) {
            this.playerName = playerName;
            this.score = score;
            this.accuracy = accuracy;
            this.questionsAnswered = questionsAnswered;
            this.grade = grade;
            this.dateTime = dateTime;
        }

        // Getters
        public String getPlayerName() { return playerName; }
        public int getScore() { return score; }
        public double getAccuracy() { return accuracy; }
        public int getQuestionsAnswered() { return questionsAnswered; }
        public String getGrade() { return grade; }
        public LocalDateTime getDateTime() { return dateTime; }

        public String getFormattedDateTime() {
            return dateTime.format(formatter);
        }

        public String getAccuracyPercentage() {
            return String.format("%.1f%%", accuracy * 100);
        }

        @Override
        public String toString() {
            return String.join("|", 
                playerName,
                String.valueOf(score),
                String.valueOf(accuracy),
                String.valueOf(questionsAnswered),
                grade,
                dateTime.format(formatter)
            );
        }

        public static HighScoreEntry fromString(String line) {
            try {
                String[] parts = line.split("\\|");
                if (parts.length >= 6) {
                    return new HighScoreEntry(
                        parts[0],
                        Integer.parseInt(parts[1]),
                        Double.parseDouble(parts[2]),
                        Integer.parseInt(parts[3]),
                        parts[4],
                        LocalDateTime.parse(parts[5], formatter)
                    );
                }
            } catch (Exception e) {
                System.err.println("Failed to parse high score entry: " + line);
            }
            return null;
        }
    }
}
