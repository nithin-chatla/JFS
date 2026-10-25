@echo off
title CampusStay - Hostel Complaint Management System
color 0A

echo ========================================================
echo    CAMPUSSTAY HOSTEL COMPLAINT MANAGEMENT SYSTEM
echo ========================================================
echo.
echo [1/3] Checking Java installation...

:: Auto-detect JAVA_HOME if not set
if not defined JAVA_HOME (
    if exist "C:\Program Files\Java\jdk-23" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-23"
    ) else if exist "C:\Program Files\Java\jdk-21" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-21"
    ) else if exist "C:\Program Files\Java\jdk-17" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-17"
    )
)

echo [2/3] Using Java at: %JAVA_HOME%
echo.
echo [3/3] Starting Spring Boot (Persistent Database Mode)...
echo.
echo --------------------------------------------------------
echo  Once started, open your web browser at:
echo  --^> http://localhost:9090
echo.
echo  Database Storage:
echo   * Local persistent storage active in: ./data/hostel_db
echo   * H2 Console: http://localhost:9090/h2-console
echo.
echo  Initial Authorized Credentials:
echo   * Chief Warden (Admin) : nithin@gmail.com / 810671
echo   * Maintenance Staff    : Recruited directly from Admin Panel (/admin/staff)
echo   * Students / Residents : Register directly at http://localhost:9090/register
echo.
echo  (Press Ctrl + C in this window to stop the server)
echo --------------------------------------------------------
echo.

:: Automatically open browser after 8 seconds in the background
start /min cmd /c "timeout /t 8 /nobreak >nul && start http://localhost:9090"

:: Start Spring Boot
call mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=h2"

pause
