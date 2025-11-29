@echo off
echo Starting MCQ Test Application...
echo.

REM Check if Java is available
java -version >nul 2>&1
if errorlevel 1 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java 11+ and try again
    pause
    exit /b 1
)

REM Try to run using Maven first
echo Attempting to run with Maven...
mvn javafx:run@mcq-test >nul 2>&1
if not errorlevel 1 (
    echo Success! Application started with Maven
    exit /b 0
)

echo Maven not available, trying alternative method...
echo.

REM Alternative: Try to run the main visualizer app
echo Starting main application with MCQ tab...
mvn javafx:run >nul 2>&1
if not errorlevel 1 (
    echo Success! Main application started - Click on 'MCQ Test' tab
    exit /b 0
)

echo.
echo ========================================
echo  INSTRUCTIONS TO RUN THE APPLICATION
echo ========================================
echo.
echo Option 1: Using an IDE
echo   - Open the project in NetBeans, IntelliJ, or Eclipse
echo   - Import as Maven project
echo   - Run: com.teachingaid.MCQTestApp
echo.
echo Option 2: Install Maven
echo   - Download from: https://maven.apache.org/download.cgi
echo   - Add to PATH and run: mvn javafx:run@mcq-test
echo.
echo Option 3: Check existing run.bat
echo   - Try running the existing run.bat file
echo   - Look for 'MCQ Test' tab in the application
echo.
echo ========================================

pause
