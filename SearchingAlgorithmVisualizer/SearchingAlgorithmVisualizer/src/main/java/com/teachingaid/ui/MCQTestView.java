package com.teachingaid.ui;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.teachingaid.mcq.*;

/**
 * Modern, attractive and interactive MCQ Test UI
 */
public class MCQTestView {
    private BorderPane mainLayout;
    private MCQTestEngine testEngine;
    private HighScoreManager highScoreManager;
    private Stage primaryStage;
    
    // UI Components
    private Label questionCounterLabel;
    private Label scoreLabel;
    private Label difficultyLabel;
    private Label accuracyLabel;
    private ProgressBar progressBar;
    private Label questionLabel;
    private VBox optionsContainer;
    private Button[] optionButtons;
    private Button nextButton;
    private Button finishTestButton;
    private VBox explanationBox;
    private Label explanationLabel;
    private VBox feedbackBox;
    
    // Animation and effects
    private Timeline pulseAnimation;
    private Timeline scoreCountAnimation;
    private int animatedScore = 0;
    
    // Current question state
    private MCQQuestion currentQuestion;
    private int selectedOption = -1;
    private boolean questionAnswered = false;
    
    // Modern colors and styling
    private static final String PRIMARY_COLOR = "#74b9ff";
    private static final String SECONDARY_COLOR = "#6c5ce7";
    private static final String SUCCESS_COLOR = "#00b894";
    private static final String ERROR_COLOR = "#e84393";
    private static final String WARNING_COLOR = "#fdcb6e";
    private static final String INFO_COLOR = "#74b9ff";

    public MCQTestView() {
        this.testEngine = new MCQTestEngine();
        this.highScoreManager = new HighScoreManager();
        initializeComponents();
        setupLayout();
        setupStyling();
        setupResponsiveDesign();
        loadFirstQuestion();
    }

    private void initializeComponents() {
        mainLayout = new BorderPane();
        mainLayout.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%);"
        );

        // Header components with emojis
        questionCounterLabel = new Label("📝 Question 1 of 50");
        scoreLabel = new Label("🏆 Score: 0");
        difficultyLabel = new Label("⚙️ Difficulty: Easy");
        accuracyLabel = new Label("🎯 Accuracy: 0%");
        
        progressBar = new ProgressBar(0.0);
        progressBar.setPrefWidth(250);
        progressBar.setPrefHeight(8);
        progressBar.getStyleClass().add("mcq-progress");

        // Question components
        questionLabel = new Label();
        questionLabel.setWrapText(true);
        questionLabel.getStyleClass().add("question-text");

        // Options
        optionsContainer = new VBox(8);
        optionsContainer.setAlignment(Pos.CENTER);
        optionButtons = new Button[4];
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = createOptionButton(i);
            optionsContainer.getChildren().add(optionButtons[i]);
        }

        // Control buttons
        nextButton = createStyledButton("Next Question →", PRIMARY_COLOR);
        nextButton.setOnAction(e -> handleNextQuestion());
        nextButton.setDisable(true);

        finishTestButton = createStyledButton("Finish Test", ERROR_COLOR);
        finishTestButton.setOnAction(e -> finishTest());

        // Explanation box
        explanationBox = new VBox(10);
        explanationBox.setVisible(false);
        explanationBox.getStyleClass().add("explanation-box");
        
        explanationLabel = new Label();
        explanationLabel.setWrapText(true);
        explanationLabel.getStyleClass().add("explanation-text");
        
        explanationBox.getChildren().add(explanationLabel);

        // Feedback box for animations
        feedbackBox = new VBox();
        feedbackBox.setAlignment(Pos.CENTER);
        feedbackBox.setVisible(false);
    }

    private Button createOptionButton(int index) {
        Button button = new Button();
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMinWidth(600);
        button.setPrefWidth(800);
        button.setMinHeight(50);
        button.setPrefHeight(55);
        button.setMaxHeight(70);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setWrapText(true);
        button.setTextAlignment(TextAlignment.LEFT);
        button.getStyleClass().add("option-button");
        
        // Make button responsive to container width
        HBox.setHgrow(button, Priority.ALWAYS);
        
        // Add hover and click effects
        button.setOnMouseEntered(e -> addHoverEffect(button));
        button.setOnMouseExited(e -> removeHoverEffect(button));
        
        button.setOnAction(e -> selectOption(index));
        
        return button;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.getStyleClass().add("control-button");
        button.setStyle(String.format(
            "-fx-background-color: %s; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: 600; " +
            "-fx-background-radius: 8px; " +
            "-fx-padding: 8 16 8 16; " +
            "-fx-font-size: 12px; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 4, 0, 0, 2); " +
            "-fx-pref-height: 32px;",
            color
        ));
        
        // Add click animation
        button.setOnMousePressed(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
            st.setToX(0.95);
            st.setToY(0.95);
            st.play();
        });
        
        button.setOnMouseReleased(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
        
        return button;
    }

    private void setupLayout() {
        // Header
        VBox headerBox = createHeaderSection();
        
        // Main content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));
        contentBox.setAlignment(Pos.TOP_CENTER);
        // Make content responsive - will be adjusted by setupResponsiveDesign()
        contentBox.setMaxWidth(1000);
        contentBox.setMinWidth(400);
        contentBox.setPrefWidth(900);
        
        // Question section
        VBox questionSection = new VBox(12);
        questionSection.getStyleClass().add("question-section");
        questionSection.getChildren().addAll(questionLabel, optionsContainer);
        
        // Controls section
        HBox controlsBox = new HBox(20);
        controlsBox.setAlignment(Pos.CENTER);
        controlsBox.getChildren().addAll(nextButton, finishTestButton);
        
        contentBox.getChildren().addAll(questionSection, controlsBox, explanationBox, feedbackBox);
        
        // Center content in scroll pane
        StackPane centerStack = new StackPane();
        centerStack.getChildren().add(contentBox);
        centerStack.setAlignment(Pos.TOP_CENTER);
        
        scrollPane.setContent(centerStack);
        
        mainLayout.setTop(headerBox);
        mainLayout.setCenter(scrollPane);
    }

    private VBox createHeaderSection() {
        VBox headerBox = new VBox(10);
        headerBox.setPadding(new Insets(15));
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #4299e1 0%, #3182ce 100%); " +
            "-fx-background-radius: 0 0 15 15;"
        );

        // Title with more emojis and animation
        Label titleLabel = new Label("🧠 MCQ Challenge: Searching Algorithms 🎯");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.getStyleClass().add("title-label");
        
        // Add pulsing animation to title
        Timeline titlePulse = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(titleLabel.scaleXProperty(), 1.0)),
            new KeyFrame(Duration.seconds(1), new KeyValue(titleLabel.scaleXProperty(), 1.05)),
            new KeyFrame(Duration.seconds(2), new KeyValue(titleLabel.scaleXProperty(), 1.0))
        );
        titlePulse.setCycleCount(Timeline.INDEFINITE);
        titlePulse.play();

        // Stats bar
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);
        
        // Style stat labels
        for (Label label : new Label[]{questionCounterLabel, scoreLabel, difficultyLabel, accuracyLabel}) {
            label.setTextFill(Color.WHITE);
            label.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 12));
            label.getStyleClass().add("stat-label");
        }
        
        statsBox.getChildren().addAll(questionCounterLabel, scoreLabel, difficultyLabel, accuracyLabel);

        // Progress section
        VBox progressSection = new VBox(5);
        progressSection.setAlignment(Pos.CENTER);
        
        Label progressLabel = new Label("📈 Test Progress 🔥");
        progressLabel.setTextFill(Color.WHITE);
        progressLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 12));
        
        progressSection.getChildren().addAll(progressLabel, progressBar);

        headerBox.getChildren().addAll(titleLabel, statsBox, progressSection);
        return headerBox;
    }

    private void setupStyling() {
        String css = 
            ".question-section {" +
                "-fx-background-color: white;" +
                "-fx-background-radius: 12px;" +
                "-fx-padding: 20px;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 3);" +
            "}" +
            ".question-text {" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #2d3748;" +
                "-fx-wrap-text: true;" +
            "}" +
            ".option-button {" +
                "-fx-background-color: #f8f9ff;" +
                "-fx-border-color: #e1e5f0;" +
                "-fx-border-width: 2px;" +
                "-fx-border-radius: 10px;" +
                "-fx-background-radius: 10px;" +
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #2d3748;" +
                "-fx-alignment: center-left;" +
                "-fx-padding: 12px 20px;" +
                "-fx-wrap-text: true;" +
                "-fx-text-alignment: left;" +
                "-fx-min-width: 600px;" +
                "-fx-pref-width: 800px;" +
                "-fx-min-height: 50px;" +
                "-fx-pref-height: 55px;" +
                "-fx-max-height: 70px;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 4, 0, 0, 2);" +
            "}" +
            ".option-button:hover {" +
                "-fx-background-color: #e8f0ff;" +
                "-fx-border-color: " + PRIMARY_COLOR + ";" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);" +
            "}" +
            ".option-selected {" +
                "-fx-background-color: #e3f2fd;" +
                "-fx-border-color: " + INFO_COLOR + ";" +
                "-fx-border-width: 3;" +
            "}" +
            ".option-correct {" +
                "-fx-background-color: #e8f5e8;" +
                "-fx-border-color: " + SUCCESS_COLOR + ";" +
                "-fx-border-width: 3;" +
            "}" +
            ".option-incorrect {" +
                "-fx-background-color: #ffebee;" +
                "-fx-border-color: " + ERROR_COLOR + ";" +
                "-fx-border-width: 3;" +
            "}" +
            ".explanation-box {" +
                "-fx-background-color: #f0f4ff;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 20;" +
                "-fx-border-color: " + INFO_COLOR + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 10;" +
            "}" +
            ".explanation-text {" +
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #444;" +
                "-fx-wrap-text: true;" +
            "}" +
            ".mcq-progress {" +
                "-fx-accent: " + SUCCESS_COLOR + ";" +
            "}" +
            ".feedback-correct {" +
                "-fx-background-color: " + SUCCESS_COLOR + ";" +
                "-fx-text-fill: white;" +
                "-fx-padding: 10 20 10 20;" +
                "-fx-background-radius: 20;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
            "}" +
            ".feedback-incorrect {" +
                "-fx-background-color: " + ERROR_COLOR + ";" +
                "-fx-text-fill: white;" +
                "-fx-padding: 10 20 10 20;" +
                "-fx-background-radius: 20;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
            "}";

        mainLayout.getStylesheets().add("data:text/css;charset=utf-8," + css);
    }

    private void addHoverEffect(Button button) {
        if (!questionAnswered && !button.getStyleClass().contains("option-selected")) {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
            st.setToX(1.02);
            st.setToY(1.02);
            st.play();
        }
    }

    private void removeHoverEffect(Button button) {
        if (!questionAnswered && !button.getStyleClass().contains("option-selected")) {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        }
    }

    private void selectOption(int optionIndex) {
        if (questionAnswered) return;
        
        selectedOption = optionIndex;
        
        // Clear previous selections
        for (Button button : optionButtons) {
            button.getStyleClass().removeAll("option-selected");
        }
        
        // Mark selected option
        optionButtons[optionIndex].getStyleClass().add("option-selected");
        
        // Add selection animation
        ScaleTransition selectAnimation = new ScaleTransition(Duration.millis(150), optionButtons[optionIndex]);
        selectAnimation.setToX(1.05);
        selectAnimation.setToY(1.05);
        selectAnimation.setAutoReverse(true);
        selectAnimation.setCycleCount(2);
        selectAnimation.play();
        
        // Submit answer after short delay
        Timeline submitDelay = new Timeline(new KeyFrame(Duration.millis(500), e -> submitAnswer()));
        submitDelay.play();
    }

    private void submitAnswer() {
        if (selectedOption == -1 || questionAnswered) return;
        
        questionAnswered = true;
        boolean isCorrect = testEngine.submitAnswer(selectedOption);
        
        // Show correct/incorrect feedback
        showAnswerFeedback(isCorrect);
        
        // Color code the options
        for (int i = 0; i < optionButtons.length; i++) {
            Button button = optionButtons[i];
            button.getStyleClass().removeAll("option-selected");
            
            if (i == currentQuestion.getCorrectAnswerIndex()) {
                button.getStyleClass().add("option-correct");
            } else if (i == selectedOption && !isCorrect) {
                button.getStyleClass().add("option-incorrect");
            }
            
            // Disable all buttons
            button.setDisable(true);
        }
        
        // Show explanation
        showExplanation();
        
        // Enable next button
        nextButton.setDisable(false);
        
        // Update stats
        updateStats();
    }

    private void showAnswerFeedback(boolean isCorrect) {
        // Enhanced feedback messages with more emojis
        String[] correctMessages = {
            "✓ 🎉 Excellent! You're on fire! 🔥",
            "✨ 🏆 Perfect! You nailed it! 🚀",
            "🎆 Fantastic! Keep it up! ⭐",
            "🎈 Brilliant answer! 🤩",
            "🎇 Outstanding work! 💪"
        };
        
        String[] incorrectMessages = {
            "✗ 😔 Not quite right, but don't give up! 💪",
            "🤔 Close, but not quite there yet! 🚀",
            "💭 Oops! Better luck next time! ✨",
            "🙂 That's okay, learning is a journey! 🌈",
            "💡 Keep thinking, you've got this! 🎯"
        };
        
        String message = isCorrect ? 
            correctMessages[(int)(Math.random() * correctMessages.length)] :
            incorrectMessages[(int)(Math.random() * incorrectMessages.length)];
        
        Label feedbackLabel = new Label(message);
        feedbackLabel.getStyleClass().add(isCorrect ? "feedback-correct" : "feedback-incorrect");
        
        feedbackBox.getChildren().clear();
        feedbackBox.getChildren().add(feedbackLabel);
        feedbackBox.setVisible(true);
        
        // Animate feedback
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), feedbackLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), feedbackLabel);
        slideIn.setFromY(-50);
        slideIn.setToY(0);
        
        ParallelTransition feedbackAnimation = new ParallelTransition(fadeIn, slideIn);
        feedbackAnimation.play();
        
        // Add confetti effect for correct answers
        if (isCorrect) {
            createConfettiEffect();
        }
        
        // Hide feedback after delay
        Timeline hideTimer = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), feedbackLabel);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(ev -> feedbackBox.setVisible(false));
            fadeOut.play();
        }));
        hideTimer.play();
    }

    private void showExplanation() {
        explanationLabel.setText(currentQuestion.getExplanation());
        explanationBox.setVisible(true);
        
        // Animate explanation appearance
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), explanationBox);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }

    private void updateStats() {
        // Update question counter with emoji
        questionCounterLabel.setText(String.format("📝 Question %d of 50", 
            Math.min(testEngine.getCurrentQuestionNumber(), 50)));
        
        // Animate score update
        animateScoreUpdate();
        
        // Update difficulty with appropriate emoji
        String difficultyName = testEngine.getDifficultyName(testEngine.getCurrentDifficulty());
        String difficultyEmoji = getDifficultyEmoji(difficultyName);
        difficultyLabel.setText(difficultyEmoji + " Difficulty: " + difficultyName);
        
        // Update accuracy with dynamic emoji
        double accuracy = testEngine.getCurrentAccuracy() * 100;
        String accuracyEmoji = getAccuracyEmoji(accuracy);
        accuracyLabel.setText(String.format("%s Accuracy: %.1f%%", accuracyEmoji, accuracy));
        
        // Update progress
        double progress = (double) testEngine.getTotalQuestions() / 50.0;
        
        Timeline progressAnimation = new Timeline(
            new KeyFrame(Duration.millis(500), new KeyValue(progressBar.progressProperty(), progress))
        );
        progressAnimation.play();
    }


    private void handleNextQuestion() {
        if (testEngine.hasNextQuestion()) {
            loadNextQuestion();
        } else {
            finishTest();
        }
    }

    private void loadFirstQuestion() {
        currentQuestion = testEngine.getCurrentQuestion();
        if (currentQuestion != null) {
            displayQuestion();
            updateStats();
        }
    }

    private void loadNextQuestion() {
        currentQuestion = testEngine.getCurrentQuestion();
        if (currentQuestion != null) {
            displayQuestion();
            resetQuestionState();
            updateStats();
        } else {
            finishTest();
        }
    }

    private void displayQuestion() {
        // Animate question transition
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), questionLabel);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            questionLabel.setText(currentQuestion.getQuestion());
            
            String[] options = currentQuestion.getOptions();
            for (int i = 0; i < options.length; i++) {
                optionButtons[i].setText((char)('A' + i) + ". " + options[i]);
            }
            
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), questionLabel);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
        fadeOut.play();
    }

    private void resetQuestionState() {
        questionAnswered = false;
        selectedOption = -1;
        nextButton.setDisable(true);
        explanationBox.setVisible(false);
        feedbackBox.setVisible(false);
        
        // Reset button states
        for (Button button : optionButtons) {
            button.setDisable(false);
            button.getStyleClass().removeAll("option-selected", "option-correct", "option-incorrect");
        }
    }

    private void finishTest() {
        MCQTestEngine.TestResult result = testEngine.getTestResult();
        
        // Check for high score
        boolean isHighScore = highScoreManager.isNewHighScore(result.getFinalScore());
        
        if (isHighScore) {
            showHighScoreDialog(result);
        } else {
            showResultsDialog(result);
        }
    }

    private void showHighScoreDialog(MCQTestEngine.TestResult result) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("🎉 New High Score!");
        dialog.setHeaderText(String.format("Congratulations! You scored %d points!", result.getFinalScore()));
        
        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField nameField = new TextField();
        nameField.setPromptText("Enter your name");
        nameField.setPrefWidth(200);
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        
        dialog.getDialogPane().setContent(grid);
        
        Platform.runLater(() -> nameField.requestFocus());
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                return nameField.getText();
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(name -> {
            if (name != null && !name.trim().isEmpty()) {
                highScoreManager.addHighScore(name.trim(), result.getFinalScore(), 
                    result.getAccuracy(), result.getTotalQuestions(), result.getGrade());
            }
            showResultsDialog(result);
        });
    }

    private void showResultsDialog(MCQTestEngine.TestResult result) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Test Completed!");
        alert.setHeaderText("Your Test Results");
        
        String resultText = String.format(
            "Final Score: %d / %d points\n" +
            "Grade: %s\n" +
            "Correct Answers: %d\n" +
            "Wrong Answers: %d\n" +
            "Accuracy: %.1f%%\n\n" +
            "Questions by Difficulty:\n%s\n\n" +
            "Questions by Category:\n%s",
            result.getFinalScore(), result.getMaxPossibleScore(),
            result.getGrade(),
            result.getCorrectAnswers(),
            result.getWrongAnswers(),
            result.getAccuracy() * 100,
            formatDifficultyDistribution(result.getDifficultyDistribution()),
            formatCategoryDistribution(result.getCategoryDistribution())
        );
        
        alert.setContentText(resultText);
        alert.getDialogPane().setPrefWidth(400);
        alert.showAndWait();
    }

    private String formatDifficultyDistribution(java.util.Map<Integer, Integer> distribution) {
        StringBuilder sb = new StringBuilder();
        for (java.util.Map.Entry<Integer, Integer> entry : distribution.entrySet()) {
            String difficultyName = testEngine.getDifficultyName(entry.getKey());
            sb.append(String.format("  %s: %d questions\n", difficultyName, entry.getValue()));
        }
        return sb.toString();
    }

    private String formatCategoryDistribution(java.util.Map<String, Integer> distribution) {
        StringBuilder sb = new StringBuilder();
        for (java.util.Map.Entry<String, Integer> entry : distribution.entrySet()) {
            sb.append(String.format("  %s: %d questions\n", entry.getKey(), entry.getValue()));
        }
        return sb.toString();
    }

    public BorderPane getView() {
        return mainLayout;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    
    private void createConfettiEffect() {
        // Create multiple confetti particles
        for (int i = 0; i < 20; i++) {
            Label confetti = new Label(getRandomConfettiEmoji());
            confetti.setFont(Font.font(20));
            confetti.setLayoutX(Math.random() * 800);
            confetti.setLayoutY(-50);
            
            // Add confetti to the main layout
            if (mainLayout.getChildren().isEmpty() || !(mainLayout.getTop() instanceof Pane)) {
                return;
            }
            
            Pane overlay = new Pane();
            overlay.setMouseTransparent(true);
            overlay.setPrefSize(800, 600);
            overlay.getChildren().add(confetti);
            
            // Create falling animation
            TranslateTransition fall = new TranslateTransition(Duration.seconds(3 + Math.random() * 2), confetti);
            fall.setToY(700);
            fall.setToX((Math.random() - 0.5) * 200); // Random horizontal drift
            
            // Create rotation animation
            RotateTransition rotate = new RotateTransition(Duration.seconds(2 + Math.random()), confetti);
            rotate.setByAngle(360 + Math.random() * 720);
            
            // Create fade out
            FadeTransition fade = new FadeTransition(Duration.seconds(3), confetti);
            fade.setToValue(0);
            
            // Combine animations
            ParallelTransition confettiAnimation = new ParallelTransition(fall, rotate, fade);
            confettiAnimation.setOnFinished(e -> overlay.getChildren().remove(confetti));
            
            // Add overlay temporarily
            mainLayout.getChildren().add(overlay);
            confettiAnimation.play();
            
            // Remove overlay after animation
            Timeline removeOverlay = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
                mainLayout.getChildren().remove(overlay);
            }));
            removeOverlay.play();
        }
    }
    
    private String getRandomConfettiEmoji() {
        String[] confettiEmojis = {
            "🎉", "🎊", "✨", "🎆", "🎇",
            "🎈", "🎯", "⭐", "💫", "🔥",
            "🏆", "🥳", "🚀", "🌈", "💥"
        };
        return confettiEmojis[(int)(Math.random() * confettiEmojis.length)];
    }
    
    private String getDifficultyEmoji(String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "easy": return "🛫";
            case "medium": return "⚡";
            case "hard": return "🔥";
            case "expert": return "🏆";
            default: return "⚙️";
        }
    }
    
    private String getAccuracyEmoji(double accuracy) {
        if (accuracy >= 90) return "🏆"; // Trophy for 90%+
        else if (accuracy >= 80) return "🎆"; // Fireworks for 80%+
        else if (accuracy >= 70) return "🎯"; // Direct hit for 70%+
        else if (accuracy >= 60) return "✨"; // Sparkles for 60%+
        else if (accuracy >= 50) return "💡"; // Light bulb for 50%+
        else return "💪"; // Muscle for encouragement
    }
    
    private void animateScoreUpdate() {
        int targetScore = testEngine.getScore();
        
        // Add bouncing effect to score label
        ScaleTransition bounceScore = new ScaleTransition(Duration.millis(200), scoreLabel);
        bounceScore.setToX(1.2);
        bounceScore.setToY(1.2);
        bounceScore.setAutoReverse(true);
        bounceScore.setCycleCount(2);
        bounceScore.play();
        
        // Update score display with emoji based on score
        String scoreEmoji = "🏆";
        if (targetScore > 500) scoreEmoji = "🎆";
        if (targetScore > 1000) scoreEmoji = "🔥";
        if (targetScore > 1500) scoreEmoji = "✨";
        
        scoreLabel.setText(scoreEmoji + " Score: " + targetScore);
        animatedScore = targetScore;
    }
    
    private void setupResponsiveDesign() {
        // Add listeners for window size changes
        mainLayout.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            adjustLayoutForWidth(newWidth.doubleValue());
        });
        
        mainLayout.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            adjustLayoutForHeight(newHeight.doubleValue());
        });
    }
    
    private void adjustLayoutForWidth(double width) {
        // Adjust content width based on screen size
        if (width < 800) {
            // Small screen adjustments - make buttons more compact
            for (Button button : optionButtons) {
                button.setFont(Font.font(12));
                button.setPrefHeight(45);
                button.setMaxHeight(55);
                button.setMinWidth(400);
                button.setPrefWidth(500);
            }
            
            // Reduce spacing
            optionsContainer.setSpacing(8);
            
        } else if (width < 1100) {
            // Medium screen adjustments
            for (Button button : optionButtons) {
                button.setFont(Font.font(13));
                button.setPrefHeight(50);
                button.setMaxHeight(65);
                button.setMinWidth(500);
                button.setPrefWidth(650);
            }
            
            optionsContainer.setSpacing(9);
            
        } else {
            // Large screen - default settings
            for (Button button : optionButtons) {
                button.setFont(Font.font(14));
                button.setPrefHeight(55);
                button.setMaxHeight(70);
                button.setMinWidth(600);
                button.setPrefWidth(800);
            }
            
            optionsContainer.setSpacing(10);
        }
        
        // Adjust progress bar width
        progressBar.setPrefWidth(Math.min(width * 0.3, 300));
    }
    
    private void adjustLayoutForHeight(double height) {
        // Adjust padding and spacing based on height
        if (height < 500) {
            // Very compact layout for smaller heights
            ((VBox)mainLayout.getTop()).setPadding(new Insets(8));
            ((VBox)mainLayout.getTop()).setSpacing(6);
            
        } else if (height < 700) {
            // Medium height adjustments
            ((VBox)mainLayout.getTop()).setPadding(new Insets(12));
            ((VBox)mainLayout.getTop()).setSpacing(8);
            
        } else {
            // Full height - default settings
            ((VBox)mainLayout.getTop()).setPadding(new Insets(15));
            ((VBox)mainLayout.getTop()).setSpacing(10);
        }
    }
}
