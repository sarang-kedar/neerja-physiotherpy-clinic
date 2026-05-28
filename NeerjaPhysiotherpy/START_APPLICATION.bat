@echo off
REM ============================================================================
REM NEERJA PHYSIOTHERAPY CLINIC - Application Startup Script
REM This script starts the application in background and opens the login URL
REM ============================================================================

setlocal enabledelayedexpansion

REM Set application variables
set JAR_FILE=target\NeerjaPhysiotherpy-1.0.0.jar
set APP_URL=http://localhost:8080/neerjaPhysiotherpyClinic/login.html
set APP_PORT=8080

REM Change to the application directory
cd /d "%~dp0"

REM Check if Java is installed
echo Checking Java installation...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo.
    echo ============================================================================
    echo ERROR: Java is not installed or not in PATH
    echo ============================================================================
    echo Please install Java and add it to your PATH environment variable
    echo Download from: https://www.oracle.com/java/technologies/downloads/
    echo.
    pause
    exit /b 1
)

REM Check if JAR file exists
if not exist "%JAR_FILE%" (
    echo.
    echo ============================================================================
    echo ERROR: JAR file not found at: %JAR_FILE%
    echo ============================================================================
    echo Please build the application first:
    echo   mvn clean package
    echo.
    pause
    exit /b 1
)

REM Kill any existing process on port 8080 (optional - comment out if not needed)
echo Checking for existing processes on port %APP_PORT%...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%APP_PORT%') do (
    taskkill /pid %%a /f >nul 2>&1
)

REM Start the application in background
echo.
echo ============================================================================
echo Starting NEERJA PHYSIOTHERAPY CLINIC Application...
echo ============================================================================
echo JAR File: %JAR_FILE%
echo Port: %APP_PORT%
echo Login URL: %APP_URL%
echo.

REM Launch the JAR in background using start command
start "NEERJA PHYSIOTHERAPY CLINIC" java -jar "%JAR_FILE%"

REM Wait for the application to start (adjust time if needed)
echo Waiting for application to start...
timeout /t 8 /nobreak

REM Check if the port is responding
echo Checking if application is running...
for /f %%a in ('timeout /t 2 /nobreak ^| find "Elapsed"') do (
    netstat -ano | findstr :%APP_PORT% >nul
    if !errorlevel! equ 0 (
        echo Application started successfully!
        goto open_browser
    )
)

REM Open the login URL in default browser
:open_browser
echo.
echo ============================================================================
echo Opening login page in default browser...
echo ============================================================================
timeout /t 2 /nobreak

REM Open URL in default browser
start "" "%APP_URL%"

REM Success message
echo.
echo ============================================================================
echo SUCCESS! Application is running in background
echo ============================================================================
echo Application URL: %APP_URL%
echo.
echo To stop the application, use one of these methods:
echo   1. Close the "NEERJA PHYSIOTHERAPY CLINIC" console window
echo   2. Use Task Manager to stop "java.exe"
echo   3. Run: taskkill /im java.exe /f
echo.
echo ============================================================================
echo.

REM Keep the script window open
pause
exit /b 0
