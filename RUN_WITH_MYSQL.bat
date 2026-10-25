@echo off
title CampusStay - Hostel Complaint Management System (MySQL Mode)
color 0B

echo ========================================================
echo    CAMPUSSTAY HOSTEL COMPLAINT SYSTEM (MySQL Mode)
echo ========================================================
echo.
echo Ensure MySQL server is active on localhost:3306
echo Database: hostel_complaints_db
echo.

if not defined JAVA_HOME (
    if exist "C:\Program Files\Java\jdk-23" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-23"
    ) else if exist "C:\Program Files\Java\jdk-21" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-21"
    ) else if exist "C:\Program Files\Java\jdk-17" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-17"
    )
)

echo Using Java at: %JAVA_HOME%
echo Starting Spring Boot with MySQL profile...
echo.

start /min cmd /c "timeout /t 8 /nobreak >nul && start http://localhost:9090"

call mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=mysql"

pause
