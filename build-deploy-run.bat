@echo off
echo ========================================
echo Build, Deploy, and Run
echo ========================================
echo.

echo Step 1: Building application...
call build-and-deploy.bat
if %ERRORLEVEL% NEQ 0 (
    echo Build failed!
    pause
    exit /b 1
)
echo.

echo Step 2: Deploying to Tomcat...
call deploy-to-tomcat.bat
if %ERRORLEVEL% NEQ 0 (
    echo Deployment failed!
    pause
    exit /b 1
)
echo.

echo ========================================
echo ALL DONE!
echo ========================================
pause