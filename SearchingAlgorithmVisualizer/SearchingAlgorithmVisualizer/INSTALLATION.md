# 🚀 Installation Guide - Searching Algorithm Visualizer

This guide will help you set up and run the Searching Algorithm Visualizer on your Windows system.

## ✅ Prerequisites

### Required Software
1. **Java 11 or higher** ✓ (Currently installed: Java 23)
2. **Apache Maven** ✓ (Automatically installed to: `C:\Users\HP\apache-maven-3.9.6`)

## 📦 What's Already Set Up

✅ **Maven 3.9.6** - Downloaded and installed to user directory  
✅ **Environment Variables** - Configured for this session  
✅ **Dependencies** - JavaFX 21 and iText 8 configured in `pom.xml`  
✅ **Project Structure** - Complete source code with all components  

## 🚀 Quick Start

### Option 1: Using the Automated Script (Recommended)
1. Open PowerShell or Command Prompt
2. Navigate to the project directory: `C:\Users\HP\SearchingAlgorithmVisualizer`
3. Run the batch script:
   ```batch
   .\run.bat
   ```
   This script will automatically:
   - Check Java installation
   - Set up Maven environment variables
   - Compile the project
   - Launch the application

### Option 2: Manual Steps
1. **Set Environment Variables** (if not using run.bat):
   ```batch
   set PATH=C:\Users\HP\apache-maven-3.9.6\bin;%PATH%
   set JAVA_HOME=C:\Program Files\Java\jdk-23
   ```

2. **Compile the Project**:
   ```batch
   mvn clean compile
   ```

3. **Run the Application**:
   ```batch
   mvn javafx:run
   ```

## 🔧 Development Commands

### Build Commands
```batch
# Clean and compile
mvn clean compile

# Run the application
mvn javafx:run

# Package into JAR (optional)
mvn clean package
```

### Verification Commands
```batch
# Check Java version
java -version

# Check Maven version
mvn -version

# Verify dependencies
mvn dependency:tree
```

## 📁 Project Structure
```
SearchingAlgorithmVisualizer/
├── src/main/java/com/teachingaid/
│   ├── SearchingVisualizerApp.java          # Main application
│   ├── algorithms/
│   │   ├── LinearSearch.java                # Linear search implementation
│   │   └── BinarySearch.java                # Binary search implementation
│   ├── ui/
│   │   ├── MainViewController.java          # Main UI controller
│   │   └── ArrayVisualizationPane.java      # Visualization component
│   └── pdf/
│       └── PDFExporter.java                 # PDF export functionality
├── src/main/resources/
│   └── styles.css                          # UI styling
├── pom.xml                                 # Maven configuration
├── run.bat                                 # Automated run script
└── README.md                               # Project documentation
```

## 🎯 Application Features

### Core Features
- **Interactive Visualization** - Step-by-step algorithm animation
- **Dual Algorithms** - Linear Search and Binary Search
- **Control System** - Play, Pause, Step, Reset functionality
- **PDF Export** - Generate comprehensive analysis reports
- **Educational Content** - Algorithm explanations and complexity analysis

### User Interface
- **Modern Design** - Gradient headers and professional styling
- **Three-Panel Layout** - Controls, visualization, and information
- **Real-time Feedback** - Status updates and progress indicators
- **Responsive Design** - Adapts to different screen sizes

## ⚠️ Troubleshooting

### Common Issues

#### Issue: "mvn: command not found"
**Solution**: Run the `run.bat` script which sets up the environment automatically.

#### Issue: Java version compatibility warnings
**Solution**: The warnings are normal when using Java 23 with source/target 11. The application will run correctly.

#### Issue: CSS parsing warnings
**Solution**: These are minor CSS warnings that don't affect functionality. The application will work normally.

#### Issue: Application window doesn't appear
**Solution**: 
1. Check that JavaFX modules are loaded correctly
2. Try running with the batch script instead of manual commands
3. Ensure Windows Defender/Firewall isn't blocking the application

### Performance Tips
- For better performance, consider using Java 11-17 (though Java 23 works fine)
- Close other resource-intensive applications when running the visualizer
- The application works best on systems with at least 4GB RAM

## 🔄 Updates and Maintenance

### Adding New Features
1. Modify source files in `src/main/java/com/teachingaid/`
2. Recompile using `mvn clean compile`
3. Run with `mvn javafx:run`

### Modifying UI Styling
1. Edit `src/main/resources/styles.css`
2. Changes will be applied on next application restart

### Dependencies
All required dependencies are specified in `pom.xml`:
- JavaFX 21.0.1 (UI framework)
- iText 8.0.2 (PDF generation)

## 📞 Support

If you encounter any issues:
1. Check this troubleshooting section
2. Verify Java and Maven installations
3. Review the console output for specific error messages
4. Try running the automated `run.bat` script

## 🎓 Educational Usage

This application is designed for:
- Computer Science students learning about search algorithms
- Educators teaching data structures and algorithms
- Anyone interested in understanding algorithm visualization

The combination of visual learning, interactive controls, and professional documentation makes it an excellent educational tool.

---

**Happy Learning! 🚀**
