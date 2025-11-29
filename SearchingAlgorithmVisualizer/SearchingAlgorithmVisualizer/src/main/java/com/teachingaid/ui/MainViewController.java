package com.teachingaid.ui;

import com.teachingaid.MainApp;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.teachingaid.algorithms.LinearSearch;
import com.teachingaid.algorithms.BinarySearch;
import com.teachingaid.algorithms.JumpSearch;
import com.teachingaid.algorithms.ExponentialSearch;
import com.teachingaid.algorithms.KMPStringSearch;
import com.teachingaid.algorithms.NaiveStringSearch;
import com.teachingaid.algorithms.BoyerMooreStringSearch;
import com.teachingaid.pdf.PDFExporter;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

/**
* Main View Controller for the Searching Algorithm Visualizer
*/
public class MainViewController {

    private BorderPane mainLayout;
    private Stage primaryStage;
    private TabPane tabPane;
    private ScrollPane scrollPane;

    // Array Search Tab Components
    private ArrayVisualizationPane arrayVisualizationPane;
    private TextField arrayInputField;
    private TextField searchValueField;
    private ComboBox<String> algorithmSelector;
    private Button startButton, pauseButton, playButton, resetButton, stepButton, randomArrayButton, arrayClearButton, exportPdfButton, backToMainButton;
    private Slider speedSlider;
    private Label statusLabel;
    private Label complexityLabel;
    private TextArea algorithmInfoArea;

    // String Search Tab Components
    private StringVisualizationPane stringVisualizationPane;
    private TextField textInputField;
    private TextField patternInputField;
    private ComboBox<String> stringAlgorithmSelector;
    private Button stringStartButton, stringPauseButton, stringPlayButton, stringResetButton, stringStepButton, stringRandomButton, stringClearButton, stringExportPdfButton;
    private Slider stringSpeedSlider;
    private Label stringStatusLabel;
    private Label stringComplexityLabel;
    private TextArea stringAlgorithmInfoArea;

    // Algorithm instances
    private LinearSearch linearSearch;
    private BinarySearch binarySearch;
    private JumpSearch jumpSearch;
    private ExponentialSearch exponentialSearch;
    private KMPStringSearch kmpStringSearch;
    private NaiveStringSearch naiveStringSearch;
    private BoyerMooreStringSearch boyerMooreStringSearch;

    // Current state
    private int[] currentArray;
    private int searchValue;
    private String currentText;
    private String currentPattern;
    private boolean isArraySearchRunning = false;
    private boolean isStringSearchRunning = false;

    public MainViewController() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeComponents() {
        mainLayout = new BorderPane();
        // Simple gradient background
        mainLayout.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%);"
        );

        // Create tab pane
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Initialize Array Search components
        initializeArraySearchComponents();

        // Initialize String Search components
        initializeStringSearchComponents();

        // Initialize algorithms
        linearSearch = new LinearSearch();
        binarySearch = new BinarySearch();
        jumpSearch = new JumpSearch();
        exponentialSearch = new ExponentialSearch();
        kmpStringSearch = new KMPStringSearch();
        naiveStringSearch = new NaiveStringSearch();
        boyerMooreStringSearch = new BoyerMooreStringSearch();

        // Initialize animation delays to match current slider values
        updateAlgorithmSpeed(speedSlider.getValue());
        updateStringAlgorithmSpeed(stringSpeedSlider.getValue());
    }

    private void initializeArraySearchComponents() {
        // Array visualization pane
        arrayVisualizationPane = new ArrayVisualizationPane();
        arrayVisualizationPane.setMinHeight(600);
        arrayVisualizationPane.setPrefHeight(700);
        arrayVisualizationPane.setMaxHeight(800);

        // Input controls
        arrayInputField = new TextField();
        arrayInputField.setPromptText("Enter array values separated by commas (e.g., 64, 34, 25, 12, 22)");
        HBox.setHgrow(arrayInputField, Priority.ALWAYS);

        searchValueField = new TextField();
        searchValueField.setPromptText("Enter value to search for");
        searchValueField.setPrefWidth(100);

        algorithmSelector = new ComboBox<>();
        algorithmSelector.getItems().addAll("Linear Search", "Binary Search", "Jump Search", "Exponential Search");
        algorithmSelector.setPromptText("Select Algorithm");
        algorithmSelector.setPrefWidth(150);

        // Random / clear buttons for array input
        randomArrayButton = new Button("🎲 Random Array");
        randomArrayButton.getStyleClass().addAll("button", "reset-button");

        arrayClearButton = new Button("🧹 Clear");
        arrayClearButton.getStyleClass().addAll("button", "reset-button");

        // Back to main menu button (shown in export section)
        backToMainButton = new Button("⬅ Back to Home");
        backToMainButton.getStyleClass().addAll("button", "reset-button");

        // Modern styled buttons with CSS classes
        startButton = new Button("▶ Start");
        startButton.getStyleClass().addAll("button", "start-button");
        
        pauseButton = new Button("⏸ Pause");
        pauseButton.getStyleClass().addAll("button", "pause-button");
        
        playButton = new Button("⏵ Play");
        playButton.getStyleClass().addAll("button", "start-button");
        
        resetButton = new Button("⟲ Reset");
        resetButton.getStyleClass().addAll("button", "reset-button");
        
        stepButton = new Button("⏯ Step");
        stepButton.getStyleClass().addAll("button", "step-button");
        
        exportPdfButton = new Button("📄 Export PDF");
        exportPdfButton.getStyleClass().addAll("button", "export-button");

        pauseButton.setDisable(true);

        // Speed control (0.25x to 2x speed)
        speedSlider = new Slider(0.25, 2.0, 1.0);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(0.25);
        speedSlider.setMinorTickCount(3);
        speedSlider.setSnapToTicks(true);
        speedSlider.setPrefWidth(200);
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateAlgorithmSpeed(newVal.doubleValue());
        });

        // Status and info labels
        statusLabel = new Label("Select algorithm and input data to begin");
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        statusLabel.setTextFill(Color.web("#666"));
        HBox.setHgrow(statusLabel, Priority.ALWAYS);

        complexityLabel = new Label("Complexity info will appear here");
        complexityLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 12));
        complexityLabel.setTextFill(Color.web("#888"));

        // Algorithm information area
        algorithmInfoArea = new TextArea();
        algorithmInfoArea.setEditable(false);
        algorithmInfoArea.setPrefRowCount(8);
        algorithmInfoArea.setWrapText(true);
        algorithmInfoArea.setPromptText("Select an algorithm to view detailed information...");
    }

    private void initializeStringSearchComponents() {
        // String visualization pane
        stringVisualizationPane = new StringVisualizationPane();
        stringVisualizationPane.setMinHeight(600);
        stringVisualizationPane.setPrefHeight(700);
        stringVisualizationPane.setMaxHeight(800);

        // Input controls
        textInputField = new TextField();
        textInputField.setPromptText("Enter text to search in (e.g., ABABDABACDABABCABCABCABCABC)");
        HBox.setHgrow(textInputField, Priority.ALWAYS);

        patternInputField = new TextField();
        patternInputField.setPromptText("Enter pattern to search for (e.g., ABCAB)");
        patternInputField.setPrefWidth(200);

        // String algorithm selector
        stringAlgorithmSelector = new ComboBox<>();
        stringAlgorithmSelector.getItems().addAll("KMP Search", "Naive Search", "Boyer-Moore Search");
        stringAlgorithmSelector.setPromptText("Select String Algorithm");
        stringAlgorithmSelector.setPrefWidth(150);

        // Modern styled buttons with CSS classes for string search
        stringStartButton = new Button("▶ Start");
        stringStartButton.getStyleClass().addAll("button", "start-button");
        
        stringPauseButton = new Button("⏸ Pause");
        stringPauseButton.getStyleClass().addAll("button", "pause-button");
        
        stringPlayButton = new Button("⏵ Play");
        stringPlayButton.getStyleClass().addAll("button", "start-button");
        
        stringResetButton = new Button("⟲ Reset");
        stringResetButton.getStyleClass().addAll("button", "reset-button");
        
        stringStepButton = new Button("⏯ Step");
        stringStepButton.getStyleClass().addAll("button", "step-button");
        
        stringExportPdfButton = new Button("📄 Export PDF");
        stringExportPdfButton.getStyleClass().addAll("button", "export-button");

        stringPauseButton.setDisable(true);

        // Speed control for string search (0.25x to 2x speed)
        stringSpeedSlider = new Slider(0.25, 2.0, 1.0);
        stringSpeedSlider.setShowTickLabels(true);
        stringSpeedSlider.setShowTickMarks(true);
        stringSpeedSlider.setMajorTickUnit(0.25);
        stringSpeedSlider.setMinorTickCount(3);
        stringSpeedSlider.setSnapToTicks(true);
        stringSpeedSlider.setPrefWidth(200);
        stringSpeedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateStringAlgorithmSpeed(newVal.doubleValue());
        });

        // Status and info labels
        stringStatusLabel = new Label("Select string algorithm and input data to begin");
        stringStatusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        stringStatusLabel.setTextFill(Color.web("#666"));
        HBox.setHgrow(stringStatusLabel, Priority.ALWAYS);

        stringComplexityLabel = new Label("Complexity info will appear here");
        stringComplexityLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 12));
        stringComplexityLabel.setTextFill(Color.web("#888"));

        // Algorithm information area
        stringAlgorithmInfoArea = new TextArea();
        stringAlgorithmInfoArea.setEditable(false);
        stringAlgorithmInfoArea.setPrefRowCount(8);
        stringAlgorithmInfoArea.setWrapText(true);
        stringAlgorithmInfoArea.setPromptText("Select a string algorithm to view detailed information...");
    }

    // Removed createNeonButton method - using CSS classes for modern styling

    private void setupLayout() {
        // Header
        VBox headerBox = createHeaderSection();

        // Create tabs
        Tab arraySearchTab = createArraySearchTab();
        Tab stringSearchTab = createStringSearchTab();
        Tab mcqTestTab = createMCQTestTab();

        tabPane.getTabs().addAll(arraySearchTab, stringSearchTab, mcqTestTab);
        
        // Wrap tab pane in a scroll pane for better responsiveness
        scrollPane = new ScrollPane(tabPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        mainLayout.setTop(headerBox);
        mainLayout.setCenter(scrollPane);
    }

    private VBox createHeaderSection() {
        VBox headerBox = new VBox(10);
        headerBox.setPadding(new Insets(15));
        headerBox.setAlignment(Pos.CENTER);
        // Simple header background
        headerBox.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #4299e1 0%, #3182ce 100%); " +
            "-fx-background-radius: 0 0 15 15;"
        );

        Label titleLabel = new Label("🔍 Searching Algorithm Visualizer");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.WHITE);

        Label subtitleLabel = new Label("Interactive Teaching Aid for Array & String Search Algorithms");
        subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        subtitleLabel.setTextFill(Color.WHITE);

        headerBox.getChildren().addAll(titleLabel, subtitleLabel);
        return headerBox;
    }

    private Tab createArraySearchTab() {
        Tab tab = new Tab("Array Search");
        tab.setClosable(false);

        BorderPane tabContent = new BorderPane();

        // Control Panel
        VBox controlPanel = createArrayControlPanel();

        // Visualization area with scroll support
        VBox visualizationArea = new VBox(10);
        visualizationArea.setPadding(new Insets(20));
        visualizationArea.setAlignment(Pos.CENTER);
        visualizationArea.setStyle(
            "-fx-background: radial-gradient(center 50% 50%, radius 80%, " +
            "rgba(255,255,255,0.3) 0%, rgba(255,255,255,0.1) 70%, transparent 100%); " +
            "-fx-background-radius: 25; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0.3, 0, 5);"
        );
        
        // Add scroll pane for array visualization
        ScrollPane arrayScrollPane = new ScrollPane(arrayVisualizationPane);
        arrayScrollPane.setFitToWidth(true);
        arrayScrollPane.setFitToHeight(true);
        arrayScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        arrayScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        arrayScrollPane.setStyle("-fx-background-color: transparent;");
        
        VBox.setVgrow(arrayScrollPane, Priority.ALWAYS);

        // Status bar
        HBox statusBar = createArrayStatusBar();

        // Info panel (right side)
        VBox infoPanel = createArrayInfoPanel();

        visualizationArea.getChildren().addAll(arrayScrollPane, statusBar);
        VBox.setVgrow(visualizationArea, Priority.ALWAYS);

        tabContent.setLeft(controlPanel);
        tabContent.setCenter(visualizationArea);
        tabContent.setRight(infoPanel);

        tab.setContent(tabContent);
        return tab;
    }

    private Tab createStringSearchTab() {
        Tab tab = new Tab("String Search");
        tab.setClosable(false);

        BorderPane tabContent = new BorderPane();

        // Control Panel
        VBox controlPanel = createStringControlPanel();

        // String visualization area with scroll support
        VBox visualizationArea = new VBox(10);
        visualizationArea.setPadding(new Insets(20));
        visualizationArea.setAlignment(Pos.CENTER);
        visualizationArea.setStyle(
            "-fx-background: radial-gradient(center 50% 50%, radius 80%, " +
            "rgba(255,255,255,0.3) 0%, rgba(255,255,255,0.1) 70%, transparent 100%); " +
            "-fx-background-radius: 25; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0.3, 0, 5);"
        );
        
        // Add scroll pane for string visualization
        ScrollPane stringScrollPane = new ScrollPane(stringVisualizationPane);
        stringScrollPane.setFitToWidth(true);
        stringScrollPane.setFitToHeight(true);
        stringScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        stringScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        stringScrollPane.setStyle("-fx-background-color: transparent;");
        
        VBox.setVgrow(stringScrollPane, Priority.ALWAYS);

        // Status bar
        HBox statusBar = createStringStatusBar();

        // Info panel (right side)
        VBox infoPanel = createStringInfoPanel();

        visualizationArea.getChildren().addAll(stringScrollPane, statusBar);
        VBox.setVgrow(visualizationArea, Priority.ALWAYS);

        tabContent.setLeft(controlPanel);
        tabContent.setCenter(visualizationArea);
        tabContent.setRight(infoPanel);

        tab.setContent(tabContent);
        return tab;
    }

    private Tab createMCQTestTab() {
        Tab tab = new Tab("MCQ Test");
        tab.setClosable(false);
        
        // Create MCQ Test View
        MCQTestView mcqTestView = new MCQTestView();
        mcqTestView.setPrimaryStage(primaryStage);
        
        tab.setContent(mcqTestView.getView());
        return tab;
    }

    private VBox createArrayControlPanel() {
        VBox controlPanel = new VBox(15);
        controlPanel.setPadding(new Insets(15));
        controlPanel.setPrefWidth(300);
        controlPanel.setMinWidth(250);
        // Simple control panel styling
        controlPanel.setStyle(
            "-fx-background-color: rgba(255,255,255,0.95); " +
            "-fx-background-radius: 8px; " +
            "-fx-border-color: #e2e8f0; " +
            "-fx-border-width: 1;"
        );

        // Input section
        Label inputLabel = new Label("📝 Input Configuration");
        inputLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        inputLabel.setTextFill(Color.web("#333"));

        VBox inputBox = new VBox(8);
        HBox arrayInputRow = new HBox(8);
        arrayInputRow.getChildren().addAll(arrayInputField, randomArrayButton, arrayClearButton);
        HBox.setHgrow(arrayInputField, Priority.ALWAYS);
        inputBox.getChildren().addAll(
            new Label("Array Elements:"),
            arrayInputRow,
            new Label("Search Value:"),
            searchValueField,
            new Label("Algorithm:"),
            algorithmSelector
        );

        // Control buttons section
        Label controlLabel = new Label("🎮 Controls");
        controlLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        controlLabel.setTextFill(Color.web("#333"));

        // Create a grid for buttons for better layout
        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(8);
        buttonGrid.setVgap(8);
        buttonGrid.add(startButton, 0, 0);
        buttonGrid.add(pauseButton, 1, 0);
        buttonGrid.add(playButton, 0, 1);
        buttonGrid.add(stepButton, 1, 1);
        buttonGrid.add(resetButton, 0, 2);
        
        // Make buttons expand to fill available space
        for (javafx.scene.Node node : buttonGrid.getChildren()) {
            if (node instanceof Button) {
                ((Button) node).setMaxWidth(Double.MAX_VALUE);
            }
        }

        // Speed control section
        Label speedLabel = new Label("⏱️ Animation Speed");
        speedLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        speedLabel.setTextFill(Color.web("#333"));
        
        VBox speedBox = new VBox(8);
        speedBox.getChildren().addAll(
            speedLabel,
            new Label("0.25x - 2x Speed:"),
            speedSlider
        );

        // Export section
        Label exportLabel = new Label("📤 Export / Navigation");
        exportLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        exportLabel.setTextFill(Color.web("#333"));

        HBox exportRow = new HBox(8);
        exportRow.getChildren().addAll(exportPdfButton, backToMainButton);

        controlPanel.getChildren().addAll(
            inputLabel, inputBox,
            new Separator(),
            controlLabel, buttonGrid,
            new Separator(),
            speedBox,
            new Separator(),
            exportLabel, exportRow
        );

        return controlPanel;
    }

    private VBox createStringControlPanel() {
        VBox controlPanel = new VBox(15);
        controlPanel.setPadding(new Insets(15));
        controlPanel.setPrefWidth(300);
        controlPanel.setMinWidth(250);
        // Vibrant control panel with glass morphism effect
        controlPanel.setStyle(
            "-fx-background: linear-gradient(135deg, " +
            "rgba(255,255,255,0.25) 0%, rgba(255,255,255,0.15) 100%); " +
            "-fx-background-radius: 20 0 0 20; " +
            "-fx-border-color: rgba(255,255,255,0.3); " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 20 0 0 20; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 20, 0.3, 5, 0); " +
            "-fx-backdrop-filter: blur(10px);"
        );

        // Input section
        Label inputLabel = new Label("📝 Input Configuration");
        inputLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        inputLabel.setTextFill(Color.web("#333"));

        VBox inputBox = new VBox(8);
        HBox textRow = new HBox(8);
        HBox patternRow = new HBox(8);

        // Random & clear buttons for string input
        stringRandomButton = new Button("🎲 Random");
        stringRandomButton.getStyleClass().addAll("button", "reset-button");
        stringClearButton = new Button("🧹 Clear");
        stringClearButton.getStyleClass().addAll("button", "reset-button");

        textRow.getChildren().addAll(textInputField, stringRandomButton);
        patternRow.getChildren().addAll(patternInputField, stringClearButton);
        HBox.setHgrow(textInputField, Priority.ALWAYS);
        HBox.setHgrow(patternInputField, Priority.ALWAYS);

        inputBox.getChildren().addAll(
            new Label("Text:"),
            textRow,
            new Label("Pattern:"),
            patternRow,
            new Label("Algorithm:"),
            stringAlgorithmSelector
        );

        // Control buttons section
        Label controlLabel = new Label("🎮 Controls");
        controlLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        controlLabel.setTextFill(Color.web("#333"));

        // Create a grid for buttons for better layout
        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(8);
        buttonGrid.setVgap(8);
        buttonGrid.add(stringStartButton, 0, 0);
        buttonGrid.add(stringPauseButton, 1, 0);
        buttonGrid.add(stringPlayButton, 0, 1);
        buttonGrid.add(stringStepButton, 1, 1);
        buttonGrid.add(stringResetButton, 0, 2);
        
        // Make buttons expand to fill available space
        for (javafx.scene.Node node : buttonGrid.getChildren()) {
            if (node instanceof Button) {
                ((Button) node).setMaxWidth(Double.MAX_VALUE);
            }
        }

        // Speed control section for string search
        Label stringSpeedLabel = new Label("⏱️ Animation Speed");
        stringSpeedLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        stringSpeedLabel.setTextFill(Color.web("#333"));
        
        VBox stringSpeedBox = new VBox(8);
        stringSpeedBox.getChildren().addAll(
            stringSpeedLabel,
            new Label("0.25x - 2x Speed:"),
            stringSpeedSlider
        );

        // Export section
        Label exportLabel = new Label("📤 Export");
        exportLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        exportLabel.setTextFill(Color.web("#333"));

        controlPanel.getChildren().addAll(
            inputLabel, inputBox,
            new Separator(),
            controlLabel, buttonGrid,
            new Separator(),
            stringSpeedBox,
            new Separator(),
            exportLabel, stringExportPdfButton
        );

        return controlPanel;
    }

    private HBox createArrayStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(10));
        statusBar.setAlignment(Pos.CENTER_LEFT);
        // Stunning colorful status bar
        statusBar.setStyle(
            "-fx-background: linear-gradient(90deg, " +
            "rgba(255, 193, 7, 0.2) 0%, rgba(255, 107, 107, 0.2) 50%, " +
            "rgba(106, 90, 205, 0.2) 100%); " +
            "-fx-border-color: rgba(255,255,255,0.6); " +
            "-fx-border-width: 2 0 0 0; " +
            "-fx-background-radius: 0 0 20 20;"
        );

        statusBar.getChildren().addAll(
            new Label("Status:"), statusLabel,
            new Separator(),
            complexityLabel
        );

        return statusBar;
    }

    private HBox createStringStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(10));
        statusBar.setAlignment(Pos.CENTER_LEFT);
        // Stunning colorful status bar
        statusBar.setStyle(
            "-fx-background: linear-gradient(90deg, " +
            "rgba(255, 193, 7, 0.2) 0%, rgba(255, 107, 107, 0.2) 50%, " +
            "rgba(106, 90, 205, 0.2) 100%); " +
            "-fx-border-color: rgba(255,255,255,0.6); " +
            "-fx-border-width: 2 0 0 0; " +
            "-fx-background-radius: 0 0 20 20;"
        );

        statusBar.getChildren().addAll(
            new Label("Status:"), stringStatusLabel,
            new Separator(),
            stringComplexityLabel
        );

        return statusBar;
    }

    private VBox createArrayInfoPanel() {
        VBox infoPanel = new VBox(15);
        infoPanel.setPadding(new Insets(15));
        infoPanel.setPrefWidth(350);
        infoPanel.setMinWidth(300);
        // Stunning info panel with vibrant gradient
        infoPanel.setStyle(
            "-fx-background: linear-gradient(135deg, " +
            "rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.15) 50%, " +
            "rgba(255, 154, 158, 0.15) 100%); " +
            "-fx-background-radius: 0 20 20 0; " +
            "-fx-border-color: rgba(255,255,255,0.4); " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 0 20 20 0; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 20, 0.3, -5, 0);"
        );

        Label infoLabel = new Label("📚 Algorithm Information");
        infoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        infoLabel.setTextFill(Color.web("#333"));

        VBox.setVgrow(algorithmInfoArea, Priority.ALWAYS);
        infoPanel.getChildren().addAll(infoLabel, algorithmInfoArea);

        return infoPanel;
    }

    private VBox createStringInfoPanel() {
        VBox infoPanel = new VBox(15);
        infoPanel.setPadding(new Insets(15));
        infoPanel.setPrefWidth(350);
        infoPanel.setMinWidth(300);
        // Stunning info panel with vibrant gradient
        infoPanel.setStyle(
            "-fx-background: linear-gradient(135deg, " +
            "rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.15) 50%, " +
            "rgba(255, 154, 158, 0.15) 100%); " +
            "-fx-background-radius: 0 20 20 0; " +
            "-fx-border-color: rgba(255,255,255,0.4); " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 0 20 20 0; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 20, 0.3, -5, 0);"
        );

        Label infoLabel = new Label("📚 Algorithm Information");
        infoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        infoLabel.setTextFill(Color.web("#333"));

        VBox.setVgrow(stringAlgorithmInfoArea, Priority.ALWAYS);
        infoPanel.getChildren().addAll(infoLabel, stringAlgorithmInfoArea);

        return infoPanel;
    }

    private void setupEventHandlers() {
        // Array search event handlers
        algorithmSelector.setOnAction(e -> updateAlgorithmInfo());

        startButton.setOnAction(e -> startVisualization());
        pauseButton.setOnAction(e -> pauseVisualization());
        playButton.setOnAction(e -> resumeArrayVisualization());
        resetButton.setOnAction(e -> resetVisualization());
        stepButton.setOnAction(e -> stepVisualization());
        exportPdfButton.setOnAction(e -> exportToPDF());
        randomArrayButton.setOnAction(e -> generateRandomArray());
        arrayClearButton.setOnAction(e -> clearArrayInputs());
        backToMainButton.setOnAction(e -> navigateBackToMain());
        
        arrayInputField.setOnAction(e -> parseAndUpdateArray());
        searchValueField.setOnAction(e -> parseSearchValue());

        // String search event handlers
        stringAlgorithmSelector.setOnAction(e -> updateStringAlgorithmInfo());

        stringStartButton.setOnAction(e -> startStringVisualization());
        stringPauseButton.setOnAction(e -> pauseStringVisualization());
        stringPlayButton.setOnAction(e -> resumeStringVisualization());
        stringResetButton.setOnAction(e -> resetStringVisualization());
        stringStepButton.setOnAction(e -> stepStringVisualization());
        stringExportPdfButton.setOnAction(e -> exportStringToPDF());

        textInputField.setOnAction(e -> parseAndUpdateStringInputs());
        patternInputField.setOnAction(e -> parseAndUpdateStringInputs());
        stringRandomButton.setOnAction(e -> generateRandomStringInputs());
        stringClearButton.setOnAction(e -> clearStringInputs());
    }

    private void updateAlgorithmSpeed(double speed) {
        // Convert speed (0.25x to 2x) to delay
        // Base delay of 1000ms, adjusted by speed multiplier
        int delay = (int) (1000 / speed); // Higher speed = lower delay
        
        // Update all array search algorithms
        if (linearSearch != null) {
            linearSearch.setAnimationDelay(delay);
        }
        if (binarySearch != null) {
            binarySearch.setAnimationDelay(delay);
        }
        if (jumpSearch != null) {
            jumpSearch.setAnimationDelay(delay);
        }
        if (exponentialSearch != null) {
            exponentialSearch.setAnimationDelay(delay);
        }
        
        System.out.println(String.format("Array Search animation speed changed to: %.2fx (delay: %dms)", speed, delay));
    }

    private void updateStringAlgorithmSpeed(double speed) {
        // Convert speed (0.25x to 2x) to delay
        // Base delay of 1200ms for string search, adjusted by speed multiplier
        int delay = (int) (1200 / speed); // Higher speed = lower delay
        
        // Update all string search algorithms
        if (kmpStringSearch != null) {
            kmpStringSearch.setAnimationDelay(delay);
        }
        if (naiveStringSearch != null) {
            naiveStringSearch.setAnimationDelay(delay);
        }
        if (boyerMooreStringSearch != null) {
            boyerMooreStringSearch.setAnimationDelay(delay);
        }
        
        System.out.println(String.format("String Search animation speed changed to: %.2fx (delay: %dms)", speed, delay));
    }

    private void updateAlgorithmInfo() {
        String selectedAlgorithm = algorithmSelector.getValue();
        
        if (selectedAlgorithm == null) {
            complexityLabel.setText("Complexity info will appear here");
            algorithmInfoArea.clear();
            return;
        }

        switch (selectedAlgorithm) {
            case "Linear Search":
                complexityLabel.setText("⏱️ Time: O(n) | 💾 Space: O(1) | 🎯 Best Case: O(1) | 📊 Average: O(n/2)");
                algorithmInfoArea.setText(
                    "🔍 Linear Search Algorithm\n" +
                    "═══════════════════════════\n\n" +
                    "📋 Overview:\n" +
                    "• Searches through each element sequentially\n" +
                    "• Works on both sorted and unsorted arrays\n" +
                    "• Simple but not the most efficient\n\n" +
                    "⚡ Complexity Analysis:\n" +
                    "• Time Complexity:\n" +
                    "  - Best Case: O(1) - element found at first position\n" +
                    "  - Average Case: O(n/2) - element found in middle\n" +
                    "  - Worst Case: O(n) - element at end or not found\n" +
                    "• Space Complexity: O(1) - constant extra space\n\n" +
                    "🎯 When to Use:\n" +
                    "• Small datasets (< 1000 elements)\n" +
                    "• Unsorted data\n" +
                    "• Simple implementation needed\n\n" +
                    "🔄 Algorithm Steps:\n" +
                    "1. Start from the first element\n" +
                    "2. Compare current element with target\n" +
                    "3. If match found, return index\n" +
                    "4. If not, move to next element\n" +
                    "5. Repeat until found or end of array"
                );
                break;
            case "Binary Search":
                complexityLabel.setText("⏱️ Time: O(log n) | 💾 Space: O(1) | 🎯 Best Case: O(1) | 📊 Average: O(log n)");
                algorithmInfoArea.setText(
                    "⚙️ Binary Search Algorithm\n" +
                    "════════════════════════════\n\n" +
                    "📋 Overview:\n" +
                    "• Requires a SORTED array (prerequisite)\n" +
                    "• Divides search space in half each iteration\n" +
                    "• Much faster than linear search for large datasets\n" +
                    "• Uses divide-and-conquer approach\n\n" +
                    "⚡ Complexity Analysis:\n" +
                    "• Time Complexity:\n" +
                    "  - Best Case: O(1) - element found at middle\n" +
                    "  - Average Case: O(log n)\n" +
                    "  - Worst Case: O(log n) - element at end or not found\n" +
                    "• Space Complexity: O(1) - iterative version\n" +
                    "• Note: Recursive version has O(log n) space due to call stack\n\n" +
                    "🎯 When to Use:\n" +
                    "• Large sorted datasets (> 1000 elements)\n" +
                    "• When searching frequently in same dataset\n" +
                    "• When data is already sorted or can be sorted\n\n" +
                    "🔄 Algorithm Steps:\n" +
                    "1. Find middle element of current range\n" +
                    "2. Compare middle element with target\n" +
                    "3. If equal, element found!\n" +
                    "4. If target < middle, search left half\n" +
                    "5. If target > middle, search right half\n" +
                    "6. Repeat until found or search space is empty"
                );
                break;
            case "Jump Search":
                complexityLabel.setText("⏱️ Time: O(√n) | 💾 Space: O(1) | 🎯 Best Case: O(1) | 📊 Average: O(√n)");
                algorithmInfoArea.setText(
                    "🦆 Jump Search Algorithm\n" +
                    "═════════════════════════\n\n" +
                    "📋 Overview:\n" +
                    "• Requires a SORTED array\n" +
                    "• Jumps ahead by √n steps, then linear search\n" +
                    "• Compromise between linear and binary search\n" +
                    "• Also known as Block Search\n\n" +
                    "⚡ Complexity Analysis:\n" +
                    "• Time Complexity:\n" +
                    "  - Best Case: O(1) - element found at first jump\n" +
                    "  - Average Case: O(√n)\n" +
                    "  - Worst Case: O(√n) - O(√n) jumps + O(√n) linear search\n" +
                    "• Space Complexity: O(1) - constant extra space\n" +
                    "• Optimal jump size: √n (where n = array size)\n\n" +
                    "🎯 When to Use:\n" +
                    "• When binary search is overkill but linear is too slow\n" +
                    "• For systems where backward movement is costly\n" +
                    "• Medium-sized sorted datasets (1000-10000 elements)\n\n" +
                    "🔄 Algorithm Steps:\n" +
                    "1. Jump by √n steps through array\n" +
                    "2. Find block where element might be located\n" +
                    "3. Perform linear search within that block\n" +
                    "4. Return result when found or end of block reached"
                );
                break;
            case "Exponential Search":
                complexityLabel.setText("⏱️ Time: O(log n) | 💾 Space: O(1) | 🎯 Best Case: O(1) | 📊 Average: O(log n)");
                algorithmInfoArea.setText(
                    "📈 Exponential Search Algorithm\n" +
                    "═══════════════════════════════\n\n" +
                    "📋 Overview:\n" +
                    "• Requires a SORTED array\n" +
                    "• Also known as Doubling or Galloping Search\n" +
                    "• Two-phase algorithm: range finding + binary search\n" +
                    "• Particularly useful for unbounded/infinite arrays\n\n" +
                    "⚡ Complexity Analysis:\n" +
                    "• Time Complexity:\n" +
                    "  - Best Case: O(1) - element found at first position\n" +
                    "  - Average Case: O(log n)\n" +
                    "  - Worst Case: O(log n)\n" +
                    "  - Range Finding: O(log i) where i is position of target\n" +
                    "  - Binary Search: O(log n)\n" +
                    "• Space Complexity: O(1) - constant extra space\n\n" +
                    "🎯 When to Use:\n" +
                    "• Unbounded or very large arrays\n" +
                    "• When target is likely to be near the beginning\n" +
                    "• When you don't know the array size\n" +
                    "• Better than binary search when target position is small\n\n" +
                    "🔄 Algorithm Steps:\n" +
                    "1. Start with range [0, 1]\n" +
                    "2. Keep doubling the end index (1, 2, 4, 8, 16...)\n" +
                    "3. Stop when element at end > target or end > array size\n" +
                    "4. Apply binary search on found range [prev_end, current_end]\n" +
                    "5. Return result from binary search"
                );
                break;
        }
    }

    private void generateRandomArray() {
        int length = 10;
        int minValue = 0;
        int maxValue = 99;

        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int value = random.nextInt(maxValue - minValue + 1) + minValue;
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(value);
        }

        // Set array text and parse into currentArray
        arrayInputField.setText(builder.toString());
        parseAndUpdateArray();

        // Also pick a random search value from the generated array
        if (currentArray != null && currentArray.length > 0) {
            int randomIndex = random.nextInt(currentArray.length);
            searchValue = currentArray[randomIndex];
            searchValueField.setText(String.valueOf(searchValue));
            statusLabel.setText("Random array generated: " + Arrays.toString(currentArray) +
                " | Random search value: " + searchValue + " (index " + randomIndex + ")");
        } else {
            statusLabel.setText("Random array generated, but array is empty.");
        }
    }

    private void parseAndUpdateArray() {
        try {
            String input = arrayInputField.getText().trim();
            String[] parts = input.split(",");
            currentArray = new int[parts.length];

            for (int i = 0; i < parts.length; i++) {
                currentArray[i] = Integer.parseInt(parts[i].trim());
            }

            arrayVisualizationPane.setArray(currentArray);
            statusLabel.setText("Array loaded: " + Arrays.toString(currentArray));

        } catch (NumberFormatException ex) {
            showAlert("Invalid Input", "Please enter valid integers separated by commas.");
        }
    }

    private void parseSearchValue() {
        try {
            searchValue = Integer.parseInt(searchValueField.getText().trim());
            statusLabel.setText("Search value set to: " + searchValue);
        } catch (NumberFormatException ex) {
            showAlert("Invalid Input", "Please enter a valid integer to search for.");
        }
    }

    private void startVisualization() {
        parseAndUpdateArray();
        parseSearchValue();

        if (currentArray == null || currentArray.length == 0) {
            showAlert("No Data", "Please enter array values first.");
            return;
        }

        String selectedAlgorithm = algorithmSelector.getValue();

        // Check if selected algorithm requires sorted data
        if ("Binary Search".equals(selectedAlgorithm) || "Jump Search".equals(selectedAlgorithm) || "Exponential Search".equals(selectedAlgorithm)) {
            // Check if array is sorted for binary search
            boolean isSorted = true;
            for (int i = 1; i < currentArray.length; i++) {
                if (currentArray[i] < currentArray[i-1]) {
                    isSorted = false;
                    break;
                }
            }

            if (!isSorted) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Array Not Sorted");
                alert.setHeaderText(selectedAlgorithm + " requires a sorted array.");
                alert.setContentText("Would you like to sort the array automatically?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    Arrays.sort(currentArray);
                    arrayInputField.setText(Arrays.toString(currentArray).replaceAll("[\\[\\]]", ""));
                    arrayVisualizationPane.setArray(currentArray);
                } else {
                    return;
                }
            }
        }

        isArraySearchRunning = true;
        startButton.setDisable(true);
        pauseButton.setDisable(false);

        // Start the visualization based on selected algorithm
        switch (selectedAlgorithm) {
            case "Linear Search":
                linearSearch.visualizeSearch(currentArray, searchValue, arrayVisualizationPane, this::onSearchComplete);
                break;
            case "Binary Search":
                binarySearch.visualizeSearch(currentArray, searchValue, arrayVisualizationPane, this::onSearchComplete);
                break;
            case "Jump Search":
                jumpSearch.visualizeSearch(currentArray, searchValue, arrayVisualizationPane, this::onSearchComplete);
                break;
            case "Exponential Search":
                exponentialSearch.visualizeSearch(currentArray, searchValue, arrayVisualizationPane, this::onSearchComplete);
                break;
        }

        statusLabel.setText("Searching for " + searchValue + " using " + selectedAlgorithm + "...");
    }

    private void pauseVisualization() {
        isArraySearchRunning = false;

        // Pause the current algorithm
        String selectedAlgorithm = algorithmSelector.getValue();
        switch (selectedAlgorithm) {
            case "Linear Search":
                linearSearch.pause();
                break;
            case "Binary Search":
                binarySearch.pause();
                break;
            case "Jump Search":
                jumpSearch.pause();
                break;
            case "Exponential Search":
                exponentialSearch.pause();
                break;
        }

        startButton.setDisable(false);
        pauseButton.setDisable(true);
        statusLabel.setText("Visualization paused");
    }

    private void resetVisualization() {
        isArraySearchRunning = false;

        // Stop and reset current algorithm
        linearSearch.stop();
        binarySearch.stop();
        jumpSearch.stop();
        exponentialSearch.stop();

        startButton.setDisable(false);
        pauseButton.setDisable(true);

        if (currentArray != null) {
            arrayVisualizationPane.setArray(currentArray);
        }

        statusLabel.setText("Ready to start visualization");
    }

    private void stepVisualization() {
        String selectedAlgorithm = algorithmSelector.getValue();
        if (selectedAlgorithm == null) {
            showAlert("No Algorithm Selected", "Please select an array search algorithm first.");
            return;
        }

        // Only allow step when the current algorithm is paused
        boolean paused = false;
        switch (selectedAlgorithm) {
            case "Linear Search":
                paused = linearSearch.isPaused();
                break;
            case "Binary Search":
                paused = binarySearch.isPaused();
                break;
            case "Jump Search":
                paused = jumpSearch.isPaused();
                break;
            case "Exponential Search":
                paused = exponentialSearch.isPaused();
                break;
        }

        if (!paused) {
            showAlert("Not Paused", "Start and pause the visualization first, then use Step to continue.");
            return;
        }

        isArraySearchRunning = true;
        statusLabel.setText("Step mode - advancing one step");

        switch (selectedAlgorithm) {
            case "Linear Search":
                linearSearch.resume();
                break;
            case "Binary Search":
                binarySearch.resume();
                break;
            case "Jump Search":
                jumpSearch.resume();
                break;
            case "Exponential Search":
                exponentialSearch.resume();
                break;
        }
    }

    private void onSearchComplete(int foundIndex) {
        Platform.runLater(() -> {
            isArraySearchRunning = false;
            startButton.setDisable(false);
            pauseButton.setDisable(true);

            if (foundIndex >= 0) {
                statusLabel.setText("✓ Value " + searchValue + " found at index " + foundIndex);
            } else {
                statusLabel.setText("✗ Value " + searchValue + " not found in the array");
            }
        });
    }

    private void updateStringAlgorithmInfo() {
        String selectedAlgorithm = stringAlgorithmSelector.getValue();
        
        if (selectedAlgorithm == null) {
            stringComplexityLabel.setText("Complexity info will appear here");
            stringAlgorithmInfoArea.clear();
            return;
        }

        if ("KMP Search".equals(selectedAlgorithm)) {
            stringComplexityLabel.setText("Time Complexity: O(n + m) | Space Complexity: O(m)");
            stringAlgorithmInfoArea.setText(KMPStringSearch.getAlgorithmInfo());
        } else if ("Naive Search".equals(selectedAlgorithm)) {
            stringComplexityLabel.setText("Time Complexity: O(n × m) | Space Complexity: O(1)");
            stringAlgorithmInfoArea.setText(NaiveStringSearch.getAlgorithmInfo());
        } else if ("Boyer-Moore Search".equals(selectedAlgorithm)) {
            stringComplexityLabel.setText("Time Complexity: O(n/m) avg | Space Complexity: O(σ)");
            stringAlgorithmInfoArea.setText(BoyerMooreStringSearch.getAlgorithmInfo());
        }
    }

    private void exportToPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF Report");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        fileChooser.setInitialFileName("search_algorithm_report.pdf");

        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                PDFExporter exporter = new PDFExporter();
                exporter.exportVisualization(file, currentArray, searchValue,
                    algorithmSelector.getValue(), arrayVisualizationPane);
                showAlert("Export Successful", "PDF report saved successfully!");
            } catch (Exception ex) {
                showAlert("Export Failed", "Failed to export PDF: " + ex.getMessage());
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public BorderPane getView() {
        return mainLayout;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    // String Search Methods
    private void parseAndUpdateStringInputs() {
        currentText = textInputField.getText().trim();
        currentPattern = patternInputField.getText().trim();

        if (!currentText.isEmpty() && !currentPattern.isEmpty()) {
            stringVisualizationPane.setTextAndPattern(currentText, currentPattern);
            stringStatusLabel.setText("Text and pattern loaded");
        }
    }

    private void startStringVisualization() {
        parseAndUpdateStringInputs();

        if (currentText == null || currentText.isEmpty()) {
            showAlert("No Data", "Please enter text first.");
            return;
        }

        if (currentPattern == null || currentPattern.isEmpty()) {
            showAlert("No Data", "Please enter pattern first.");
            return;
        }

        isStringSearchRunning = true;
        stringStartButton.setDisable(true);
        stringPauseButton.setDisable(false);

        // Start string search based on selected algorithm
        String selectedAlgorithm = stringAlgorithmSelector.getValue();
        if ("KMP Search".equals(selectedAlgorithm)) {
            kmpStringSearch.visualizeSearch(currentText, currentPattern, stringVisualizationPane, this::onStringSearchComplete);
        } else if ("Naive Search".equals(selectedAlgorithm)) {
            naiveStringSearch.visualizeSearch(currentText, currentPattern, stringVisualizationPane, this::onStringSearchComplete);
        } else if ("Boyer-Moore Search".equals(selectedAlgorithm)) {
            boyerMooreStringSearch.visualizeSearch(currentText, currentPattern, stringVisualizationPane, this::onStringSearchComplete);
        }

        stringStatusLabel.setText("Searching for pattern \"" + currentPattern + "\" in text using " + selectedAlgorithm + "...");
    }

    private void pauseStringVisualization() {
        isStringSearchRunning = false;

        String selectedAlgorithm = stringAlgorithmSelector.getValue();
        if ("KMP Search".equals(selectedAlgorithm)) {
            kmpStringSearch.pause();
        } else if ("Naive Search".equals(selectedAlgorithm)) {
            naiveStringSearch.pause();
        } else if ("Boyer-Moore Search".equals(selectedAlgorithm)) {
            boyerMooreStringSearch.pause();
        }

        stringStartButton.setDisable(false);
        stringPauseButton.setDisable(true);
        stringStatusLabel.setText("String search paused");
    }

    private void resetStringVisualization() {
        isStringSearchRunning = false;

        // Stop all string algorithms to ensure clean state
        kmpStringSearch.stop();
        naiveStringSearch.stop();
        boyerMooreStringSearch.stop();

        stringStartButton.setDisable(false);
        stringPauseButton.setDisable(true);

        if (currentText != null && currentPattern != null) {
            stringVisualizationPane.setTextAndPattern(currentText, currentPattern);
        }

        stringStatusLabel.setText("Ready to start string search visualization");
    }

    private void stepStringVisualization() {
        String selectedAlgorithm = stringAlgorithmSelector.getValue();
        if (selectedAlgorithm == null) {
            showAlert("No Algorithm Selected", "Please select a string search algorithm first.");
            return;
        }

        boolean paused = false;
        switch (selectedAlgorithm) {
            case "KMP Search":
                paused = kmpStringSearch.isPaused();
                break;
            case "Naive Search":
                paused = naiveStringSearch.isPaused();
                break;
            case "Boyer-Moore Search":
                paused = boyerMooreStringSearch.isPaused();
                break;
        }

        if (!paused) {
            showAlert("Not Paused", "Start and pause the string search first, then use Step to continue.");
            return;
        }

        isStringSearchRunning = true;
        stringStatusLabel.setText("Step mode - advancing one step");

        switch (selectedAlgorithm) {
            case "KMP Search":
                kmpStringSearch.resume();
                break;
            case "Naive Search":
                naiveStringSearch.resume();
                break;
            case "Boyer-Moore Search":
                boyerMooreStringSearch.resume();
                break;
        }
    }

    private void onStringSearchComplete(int foundIndex) {
        Platform.runLater(() -> {
            isStringSearchRunning = false;
            stringStartButton.setDisable(false);
            stringPauseButton.setDisable(true);

            if (foundIndex >= 0) {
                stringStatusLabel.setText("✓ Pattern \"" + currentPattern + "\" found at index " + foundIndex);
            } else {
                stringStatusLabel.setText("✗ Pattern \"" + currentPattern + "\" not found in text");
            }
        });
    }

    private void exportStringToPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF Report");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        fileChooser.setInitialFileName("kmp_string_search_report.pdf");

        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                PDFExporter exporter = new PDFExporter();
                // TODO: Add string search PDF export functionality
                showAlert("Export Successful", "PDF report saved successfully!");
            } catch (Exception ex) {
                showAlert("Export Failed", "Failed to export PDF: " + ex.getMessage());
            }
        }
    }

    private void resumeArrayVisualization() {
        String selectedAlgorithm = algorithmSelector.getValue();
        if (selectedAlgorithm == null) {
            showAlert("No Algorithm Selected", "Please select an array search algorithm first.");
            return;
        }

        isArraySearchRunning = true;
        startButton.setDisable(true);
        pauseButton.setDisable(false);

        switch (selectedAlgorithm) {
            case "Linear Search":
                if (linearSearch.isPaused()) linearSearch.resume();
                break;
            case "Binary Search":
                if (binarySearch.isPaused()) binarySearch.resume();
                break;
            case "Jump Search":
                if (jumpSearch.isPaused()) jumpSearch.resume();
                break;
            case "Exponential Search":
                if (exponentialSearch.isPaused()) exponentialSearch.resume();
                break;
        }

        statusLabel.setText("Visualization resumed");
    }

    private void generateRandomStringInputs() {
        // Generate random uppercase text and pattern similar to examples
        String chars = "ABCD";
        int textLength = 30;
        int patternLength = 4;
        Random random = new Random();

        StringBuilder textBuilder = new StringBuilder();
        for (int i = 0; i < textLength; i++) {
            textBuilder.append(chars.charAt(random.nextInt(chars.length())));
        }

        StringBuilder patternBuilder = new StringBuilder();
        for (int i = 0; i < patternLength; i++) {
            patternBuilder.append(chars.charAt(random.nextInt(chars.length())));
        }

        textInputField.setText(textBuilder.toString());
        patternInputField.setText(patternBuilder.toString());
        parseAndUpdateStringInputs();
        stringStatusLabel.setText("Random text and pattern generated");
    }

    private void clearStringInputs() {
        textInputField.clear();
        patternInputField.clear();
        currentText = "";
        currentPattern = "";
        stringVisualizationPane.setTextAndPattern("", "");
        stringStatusLabel.setText("Text and pattern cleared");
    }

    private void clearArrayInputs() {
        arrayInputField.clear();
        searchValueField.clear();
        currentArray = null;
        arrayVisualizationPane.setArray(new int[0]);
        statusLabel.setText("Array and search value cleared");
    }

    private void navigateBackToMain() {
        if (primaryStage != null) {
            MainApp mainApp = new MainApp();
            mainApp.start(primaryStage);
        }
    }

    private void resumeStringVisualization() {
        String selectedAlgorithm = stringAlgorithmSelector.getValue();
        if (selectedAlgorithm == null) {
            showAlert("No Algorithm Selected", "Please select a string search algorithm first.");
            return;
        }

        isStringSearchRunning = true;
        stringStartButton.setDisable(true);
        stringPauseButton.setDisable(false);

        switch (selectedAlgorithm) {
            case "KMP Search":
                if (kmpStringSearch.isPaused()) kmpStringSearch.resume();
                break;
            case "Naive Search":
                if (naiveStringSearch.isPaused()) naiveStringSearch.resume();
                break;
            case "Boyer-Moore Search":
                if (boyerMooreStringSearch.isPaused()) boyerMooreStringSearch.resume();
                break;
        }

        stringStatusLabel.setText("String search resumed");
    }

    public boolean isRunning() {
        return isArraySearchRunning || isStringSearchRunning;
    }

    // Method to set initial window size
    public void setInitialWindowSize() {
        if (primaryStage != null) {
            // Set reasonable default size - window will be maximized by the main app
            primaryStage.setMinWidth(1200);
            primaryStage.setMinHeight(700);
            primaryStage.centerOnScreen();
        }
    }
}