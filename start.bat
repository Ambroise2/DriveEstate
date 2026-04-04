@echo off
title DriveEstate — Kenya's Premier Cars & Land Marketplace

echo.
echo  ╔══════════════════════════════════════════════════════╗
echo  ║          DriveEstate — Kenya Marketplace             ║
echo  ║          Java 21 + Spring Boot 3.2 Enterprise        ║
echo  ╚══════════════════════════════════════════════════════╝
echo.

where java >nul 2>&1 || (echo [ERROR] Java not found. Install Java 21 from https://adoptium.net & pause & exit /b)
where mvn >nul 2>&1 || (echo [ERROR] Maven not found. Install from https://maven.apache.org & pause & exit /b)

echo [INFO] Building DriveEstate...
call mvn package -DskipTests -q

if %ERRORLEVEL% neq 0 (
    echo [ERROR] Build failed. See output above.
    pause & exit /b 1
)

echo.
echo [OK] Build successful! Starting server...
echo.
echo   Client Portal:   http://localhost:8080
echo   Admin Panel:     http://localhost:8080/admin
echo   DB Console:      http://localhost:8080/h2-console
echo.
echo   Admin login:  admin@driveestate.co.ke / Admin@1234
echo   Seller login: james@example.com       / Password@123
echo.
echo   Press Ctrl+C to stop the server
echo ─────────────────────────────────────────────────────────

java -jar target\driveestate-1.0.0.jar
pause
