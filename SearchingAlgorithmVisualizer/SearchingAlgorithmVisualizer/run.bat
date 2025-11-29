@echo off
echo Searching Algorithm Visualizer
echo ===============================

echo.
echo Checking Java installation...
java -version
if %ERRORLEVEL% NEQ 0 (
    echo Java is not installed or not in PATH
    pause
    exit /b 1
)

echo.
echo Setting up environment for this session...
set PATH=C:\Users\HP\apache-maven-3.9.6\bin;%PATH%
set JAVA_HOME=C:\Program Files\Java\jdk-23

echo.
echo Checking Maven installation...
mvn -version
if %ERRORLEVEL% NEQ 0 (
    echo Maven setup failed. Please check the installation.
    pause
    exit /b 1
)

echo.
echo Building and running the application...
echo.

echo Step 1: Compiling the project...
mvn clean compile
if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo.
echo Step 2: Launching the application...
echo Close the application window to return to this command prompt.
echo.

mvn javafx:run

echo.
echo Application closed.
pause
