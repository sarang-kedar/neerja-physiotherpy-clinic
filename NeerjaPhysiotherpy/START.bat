@echo off
REM Quick Start - Minimal version
cd /d "%~dp0"
start "NEERJA CLINIC" java -jar target\NeerjaPhysiotherpy-1.0.0.jar
timeout /t 8
start "" http://localhost:8080/neerjaPhysiotherpyClinic/login.html
pause
