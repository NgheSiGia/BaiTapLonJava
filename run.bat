@echo off
chcp 65001 >nul
cd /d "%~dp0"
if exist out rmdir /s /q out
mkdir out
javac -encoding UTF-8 -cp "lib/*" -d out src\*.java
if errorlevel 1 pause & exit /b 1
if "%1"=="web" (java -cp "out;lib/*" Web) else if "%1"=="demo" (java -cp "out;lib/*" Main --demo) else if "%1"=="reset" (java -cp "out;lib/*" Main --reset-db) else (java -cp "out;lib/*" Main)
pause
