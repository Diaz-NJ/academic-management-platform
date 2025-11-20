@echo off

REM Check for admin rights
net session >nul 2>&1
if %errorLevel% NEQ 0 (
    echo ========================================
    echo ERROR: Administrator rights required!
    echo ========================================
    echo.
    echo Please run this script as Administrator:
    echo 1. Right-click on this file
    echo 2. Select "Run as administrator"
    echo.
    pause
    exit /b 1
)

echo ========================================
echo Manual Deployment Script
echo ========================================
echo.

set TOMCAT_HOME=C:\Program Files\Apache Software Foundation\Tomcat 9.0
set APP_NAME=academic-management-platform

echo [1/6] Stopping Tomcat...
net stop Tomcat9 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Warning: Tomcat might not be running or already stopped
)
timeout /t 3 /nobreak >nul
echo Done.
echo.

echo [2/6] Cleaning old deployment...
if exist "%TOMCAT_HOME%\webapps\%APP_NAME%" (
    rmdir /s /q "%TOMCAT_HOME%\webapps\%APP_NAME%"
)
if exist "%TOMCAT_HOME%\webapps\%APP_NAME%.war" (
    del "%TOMCAT_HOME%\webapps\%APP_NAME%.war"
)
echo Done.
echo.

echo [3/6] Creating deployment directory...
mkdir "%TOMCAT_HOME%\webapps\%APP_NAME%"
echo Done.
echo.

echo [4/6] Extracting WAR file...
cd "%TOMCAT_HOME%\webapps\%APP_NAME%"
jar -xf "%~dp0%APP_NAME%.war"
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to extract WAR file!
    pause
    exit /b 1
)
echo Done.
echo.

echo [5/6] Verifying deployment...
if exist "WEB-INF\classes\com\ptc\amp\controllers\AuthServlet.class" (
    echo   OK - AuthServlet deployed
) else (
    echo   ERROR - AuthServlet NOT found!
    pause
    exit /b 1
)

if exist "WEB-INF\web.xml" (
    echo   OK - web.xml deployed
) else (
    echo   ERROR - web.xml NOT found!
    pause
    exit /b 1
)
echo.

echo [6/6] Starting Tomcat...
net start Tomcat9
timeout /t 5 /nobreak >nul
echo Done.
echo.

echo ========================================
echo Deployment Complete!
echo ========================================
echo.
echo Your application should be available at:
echo http://localhost:8080/%APP_NAME%/
echo.
pause
