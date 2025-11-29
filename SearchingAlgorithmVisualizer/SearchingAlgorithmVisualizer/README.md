# 🔍 Searching Algorithm Visualizer

An interactive JavaFX-based teaching aid for visualizing and understanding Linear Search and Binary Search algorithms.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
- [Architecture](#architecture)
- [Educational Content](#educational-content)
- [PDF Export](#pdf-export)
- [Building and Running](#building-and-running)
- [Contributing](#contributing)
- [License](#license)

## 🎯 Overview

This application serves as an educational tool designed to help students and educators understand searching algorithms through visual representation. It provides step-by-step visualization of both Linear Search and Binary Search algorithms, complete with animations, explanations, and performance analysis.

## ✨ Features

### Core Functionality
- **Interactive Visualization**: Step-by-step animated visualization of searching algorithms
- **Dual Algorithm Support**: Linear Search and Binary Search implementations
- **Real-time Controls**: Play, Pause, Step, and Reset functionality
- **Speed Control**: Adjustable animation speed for different learning paces
- **Input Validation**: Robust error handling and user input validation

### Educational Features
- **Algorithm Information**: Detailed explanations of how each algorithm works
- **Time Complexity Analysis**: Big O notation and performance comparisons
- **Step-by-Step Breakdown**: Detailed trace of algorithm execution
- **Educational Insights**: Key learning points and real-world applications

### User Interface
- **Modern Design**: Clean, attractive JavaFX interface with gradient backgrounds
- **Responsive Layout**: Adaptive design that works on different screen sizes
- **Color-Coded Elements**: Visual feedback with meaningful colors
- **Intuitive Controls**: User-friendly input fields and control buttons

### Export Functionality
- **PDF Reports**: Comprehensive analysis reports using iText library
- **Professional Layout**: Well-formatted PDF with charts, tables, and explanations
- **Educational Content**: Includes algorithm theory, complexity analysis, and insights

## 🔧 Prerequisites

- **Java 11** or higher
- **Maven 3.6+** for build management
- **JavaFX 21** (included as dependency)
- **iText 8** (included as dependency)

## 🚀 Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd SearchingAlgorithmVisualizer
   ```

2. **Build the project**
   ```bash
   mvn clean compile
   ```

3. **Run the application**
   ```bash
   mvn javafx:run
   ```

## 📖 Usage

### Getting Started
1. Launch the application
2. Enter array values in the input field (comma-separated integers)
3. Enter the value you want to search for
4. Select the algorithm (Linear Search or Binary Search)
5. Click "Start" to begin visualization

### Controls
- **▶ Start**: Begin algorithm visualization
- **⏸ Pause**: Pause the current visualization
- **⏯ Step**: Execute algorithm step by step
- **⟲ Reset**: Reset to initial state
- **📄 Export PDF**: Generate comprehensive analysis report

### Input Format
- **Array Elements**: Enter integers separated by commas (e.g., `64, 34, 25, 12, 22, 11, 90`)
- **Search Value**: Single integer value to search for
- **Algorithm**: Choose between Linear Search and Binary Search

### Special Notes
- Binary Search requires sorted arrays; the application will offer to sort automatically
- Use step mode for detailed examination of algorithm execution
- Animation speed can be adjusted using the slider

## 🏗 Architecture

### Project Structure
```
src/
├── main/
│   ├── java/com/teachingaid/
│   │   ├── SearchingVisualizerApp.java     # Main application class
│   │   ├── algorithms/
│   │   │   ├── LinearSearch.java           # Linear search implementation
│   │   │   └── BinarySearch.java           # Binary search implementation
│   │   ├── ui/
│   │   │   ├── MainViewController.java     # Main UI controller
│   │   │   └── ArrayVisualizationPane.java # Array visualization component
│   │   └── pdf/
│   │       └── PDFExporter.java            # PDF generation utility
│   └── resources/
│       └── styles.css                      # CSS styling
└── pom.xml                                 # Maven configuration
```

### Key Components

#### SearchingVisualizerApp
- Main JavaFX Application class
- Handles application startup and scene setup
- Configures styling and window properties

#### MainViewController
- Central controller managing UI interactions
- Coordinates between visualization pane and algorithms
- Handles user input validation and event management

#### ArrayVisualizationPane
- Custom JavaFX component for array visualization
- Manages animated elements and visual feedback
- Provides methods for highlighting and state changes

#### Algorithm Classes (LinearSearch, BinarySearch)
- Implement searching algorithms with visualization hooks
- Provide step-by-step execution with callbacks
- Include educational methods for algorithm information

#### PDFExporter
- Generates comprehensive analysis reports using iText
- Creates professional layouts with tables, charts, and explanations
- Includes algorithm theory and performance analysis

## 📚 Educational Content

### Linear Search
- **Concept**: Sequential search through array elements
- **Time Complexity**: O(n) in worst case, O(1) in best case
- **Space Complexity**: O(1)
- **Use Cases**: Small datasets, unsorted arrays

### Binary Search
- **Concept**: Divide-and-conquer approach on sorted arrays
- **Time Complexity**: O(log n) in all cases
- **Space Complexity**: O(1) for iterative implementation
- **Use Cases**: Large sorted datasets, frequent searches

### Comparison
The application provides visual and theoretical comparisons between algorithms, helping students understand when to use each approach.

## 📄 PDF Export

The PDF export feature generates comprehensive reports including:

1. **Title Page**: Professional header with generation timestamp
2. **Input Data Analysis**: Array properties and search parameters
3. **Algorithm Information**: Detailed theoretical background
4. **Step-by-Step Execution**: Complete trace of algorithm execution
5. **Performance Analysis**: Time complexity comparisons and practical insights
6. **Educational Insights**: Key learning points and applications

Reports are formatted using iText library with professional styling, tables, and color-coded sections.

## 🔨 Building and Running

### Development Mode
```bash
# Clean and compile
mvn clean compile

# Run with JavaFX plugin
mvn javafx:run

# Run tests (if available)
mvn test
```

### Production Build
```bash
# Create executable JAR
mvn clean package

# Run the JAR
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar target/searching-algorithm-visualizer-1.0.0.jar
```

### IDE Setup
For development in IDEs like IntelliJ IDEA or Eclipse:
1. Import as Maven project
2. Ensure JavaFX is configured in module path
3. Set main class to `com.teachingaid.SearchingVisualizerApp`

## 🎨 Customization

### Styling
The application uses CSS for styling. Modify `src/main/resources/styles.css` to:
- Change color schemes
- Adjust button styles
- Modify layout properties
- Update animation effects

### Algorithm Extension
To add new searching algorithms:
1. Create new algorithm class in `algorithms` package
2. Implement visualization interface
3. Update `MainViewController` to include new algorithm
4. Add algorithm information and educational content

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- JavaFX team for the excellent UI framework
- iText team for PDF generation capabilities
- Educational institutions for inspiration and feedback
- Open source community for continued support

## 📞 Support

For questions, issues, or suggestions:
- Create an issue on GitHub
- Check the documentation
- Review existing discussions

---

**Happy Learning! 🎓**

This tool was created to make algorithm learning more interactive and engaging. We hope it helps in your educational journey!
