@echo off
echo ========================================
echo Deploying to Apache Tomcat
echo ========================================
echo.

REM Set Tomcat path - CHANGE THIS if your Tomcat is installed elsewhere
set TOMCAT_HOME=C:\Program Files\Apache Software Foundation\Tomcat 9.0

REM Check if Tomcat exists
if not exist "%TOMCAT_HOME%" (
    echo ERROR: Tomcat not found at: %TOMCAT_HOME%
    echo Please update TOMCAT_HOME variable in this script
    pause
    exit /b 1
)

REM Check if WAR file exists
if not exist "academic-management-platform.war" (
    echo ERROR: WAR file not found!
    echo Please run build-and-deploy.bat first
    pause
    exit /b 1
)

echo [1/5] Stopping Tomcat...
net stop Tomcat9 2>nul
if %ERRORLEVEL% EQU 0 (
    echo Tomcat stopped successfully
) else (
    echo Tomcat was not running or not installed as service
)
echo.

echo [2/5] Cleaning old deployment...
if exist "%TOMCAT_HOME%\webapps\academic-management-platform" (
    rmdir /s /q "%TOMCAT_HOME%\webapps\academic-management-platform"
    echo Old deployment folder removed
)
if exist "%TOMCAT_HOME%\webapps\academic-management-platform.war" (
    del "%TOMCAT_HOME%\webapps\academic-management-platform.war"
    echo Old WAR file removed
)
echo.

echo [3/5] Copying new WAR file...
copy academic-management-platform.war "%TOMCAT_HOME%\webapps\"
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to copy WAR file
    echo Make sure you have administrator permissions
    pause
    exit /b 1
)
echo WAR file copied successfully
echo.

echo [4/5] Starting Tomcat...
net start Tomcat9
if %ERRORLEVEL% NEQ 0 (
    echo WARNING: Could not start Tomcat as service
    echo Starting Tomcat manually...
    cd "%TOMCAT_HOME%\bin"
    start startup.bat
    cd "%~dp0"
)
echo.

echo [5/5] Waiting for deployment (20 seconds)...
timeout /t 20 /nobreak
echo.

echo ========================================
echo DEPLOYMENT COMPLETE!
echo ========================================
echo.
echo Your application should be available at:
echo http://localhost:8080/academic-management-platform/
echo.
echo Opening in browser...
start http://localhost:8080/academic-management-platform/
echo.
pause