@echo off
echo ========================================
echo Building Academic Management Platform
echo ========================================
echo.

REM Clean previous builds
echo [1/8] Cleaning previous builds...
if exist "build" rmdir /s /q build
if exist "bin" rmdir /s /q bin
if exist "academic-management-platform.war" del academic-management-platform.war
mkdir bin
echo Done.
echo.

REM Check if lib folder exists
if not exist "lib" (
    echo ERROR: lib folder not found!
    echo Please create a lib folder and add:
    echo - mysql-connector-java-8.0.33.jar
    echo - gson-2.10.1.jar
    echo - servlet-api.jar
    pause
    exit /b 1
)

REM Compile Java files
echo [2/8] Compiling Java source files...

REM Compile in correct dependency order
echo Compiling models...
javac -cp "lib\*" -d bin src\backend\java\com\ptc\amp\models\*.java
if %ERRORLEVEL% NEQ 0 goto :compile_error

echo Compiling utils...
javac -cp "lib\*;bin" -d bin src\backend\java\com\ptc\amp\utils\*.java
if %ERRORLEVEL% NEQ 0 goto :compile_error

echo Compiling config...
javac -cp "lib\*;bin" -d bin src\backend\java\com\ptc\amp\config\*.java
if %ERRORLEVEL% NEQ 0 goto :compile_error

echo Compiling DAO...
javac -cp "lib\*;bin" -d bin src\backend\java\com\ptc\amp\dao\*.java
if %ERRORLEVEL% NEQ 0 goto :compile_error

echo Compiling controllers...
javac -cp "lib\*;bin" -d bin src\backend\java\com\ptc\amp\controllers\*.java
if %ERRORLEVEL% NEQ 0 goto :compile_error

echo Done.
echo.

REM Create build directory structure
echo [3/8] Creating WAR structure...
mkdir build
mkdir build\WEB-INF
mkdir build\WEB-INF\classes
mkdir build\WEB-INF\lib
echo Done.
echo.

REM Copy compiled classes
echo [4/8] Copying compiled classes...
xcopy /s /y bin\* build\WEB-INF\classes\
echo Done.
echo.

REM Copy libraries
echo [5/8] Copying libraries...
if exist "lib\mysql-connector-j-9.5.0.jar" (
    copy lib\mysql-connector-j-9.5.0.jar build\WEB-INF\lib\
) else (
    echo WARNING: mysql-connector-java-8.0.33.jar not found!
)

if exist "lib\gson-2.10.1.jar" (
    copy lib\gson-2.10.1.jar build\WEB-INF\lib\
) else (
    echo WARNING: gson-2.10.1.jar not found!
)
echo Done.
echo.

REM Copy frontend files
echo [6/8] Copying frontend files...
xcopy /s /y src\frontend\* build\
echo Done.
echo.

REM Copy web.xml
echo [7/8] Copying web.xml...
if exist "src\backend\webapp\WEB-INF\web.xml" (
    copy src\backend\webapp\WEB-INF\web.xml build\WEB-INF\web.xml
) else if exist "src\backend\WEB-INF\web.xml" (
    copy src\backend\WEB-INF\web.xml build\WEB-INF\web.xml
) else (
    echo ERROR: web.xml not found!
    pause
    exit /b 1
)
echo Done.
echo.

REM Create WAR file
echo [8/8] Creating WAR file...
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
echo 1. Make sure MySQL is running
echo 2. Make sure database 'academic_management_db' exists
echo 3. Copy academic-management-platform.war to Tomcat webapps folder
echo 4. Start Tomcat
echo 5. Access at: http://localhost:8080/academic-management-platform/
echo.
pause
exit /b 0

:compile_error
echo.
echo [ERROR] Compilation failed!
echo Please check:
echo 1. All JAR files are in lib folder
echo 2. Java source files have no syntax errors
echo 3. JAVA_HOME is set correctly
pause
exit /b 1