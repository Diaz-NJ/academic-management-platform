@echo off
echo ========================================
echo Building Academic Management Platform
echo ========================================
echo.

REM Clean previous builds
echo [1/7] Cleaning previous builds...
if exist "build" rmdir /s /q build
if exist "bin" rmdir /s /q bin
if exist "academic-management-platform.war" del academic-management-platform.war
mkdir bin
echo Done.
echo.

REM Compile Java files
echo [2/7] Compiling Java source files...
javac -cp "lib\*" -d bin -sourcepath src\backend\java src\backend\java\com\ptc\amp\controllers\*.java src\backend\java\com\ptc\amp\models\*.java src\backend\java\com\ptc\amp\dao\*.java src\backend\java\com\ptc\amp\config\*.java src\backend\java\com\ptc\amp\utils\*.java
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
)
echo Done.
echo.

REM Create build directory structure
echo [3/7] Creating WAR structure...
mkdir build
mkdir build\WEB-INF
mkdir build\WEB-INF\classes
mkdir build\WEB-INF\lib
echo Done.
echo.

REM Copy compiled classes
echo [4/7] Copying compiled classes...
xcopy /s /y bin\* build\WEB-INF\classes\
echo Done.
echo.

REM Copy libraries (excluding servlet-api because Tomcat provides it)
echo [5/7] Copying libraries...
copy lib\mysql-connector-java-8.0.33.jar build\WEB-INF\lib\
copy lib\gson-2.10.1.jar build\WEB-INF\lib\
echo Done.
echo.

REM Copy frontend files
echo [6/7] Copying frontend files...
xcopy /s /y src\frontend\* build\
echo Done.
echo.

REM Copy web.xml
echo [7/7] Copying web.xml...
copy src\backend\webapp\WEB-INF\web.xml build\WEB-INF\web.xml
echo Done.
echo.

REM Create WAR file
echo Creating WAR file...
cd build
jar -cvf ..\academic-management-platform.war *
cd ..
echo.

echo ========================================
echo BUILD SUCCESSFUL!
echo ========================================
echo.
echo WAR file created: academic-management-platform.war
echo File size:
dir academic-management-platform.war | find "academic-management-platform.war"
echo.
echo Next steps:
echo 1. Install Apache Tomcat 9 if not already installed
echo 2. Copy academic-management-platform.war to Tomcat webapps folder
echo 3. Start Tomcat
echo 4. Access at: http://localhost:8080/academic-management-platform/
echo.
echo Press any key to open the project folder...
pause
explorer .