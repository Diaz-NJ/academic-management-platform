@echo off
echo ========================================
echo Checking Deployment Status
echo ========================================
echo.

echo [1] Checking compiled classes...
if exist "bin\com\ptc\amp\controllers\AuthServlet.class" (
    echo   OK - AuthServlet.class found
) else (
    echo   ERROR - AuthServlet.class NOT FOUND
)

if exist "bin\com\ptc\amp\controllers\TaskServlet.class" (
    echo   OK - TaskServlet.class found
) else (
    echo   ERROR - TaskServlet.class NOT FOUND
)
echo.

echo [2] Checking WAR file exists...
if exist "academic-management-platform.war" (
    echo   OK - WAR file exists
    echo.
    echo [3] Checking WAR file contents...
    jar -tf academic-management-platform.war | findstr "AuthServlet"
    if %ERRORLEVEL% EQU 0 (
        echo   OK - AuthServlet found in WAR
    ) else (
        echo   ERROR - AuthServlet NOT in WAR file
    )
) else (
    echo   ERROR - WAR file not found!
)
echo.

echo [4] Checking Tomcat deployment...
if exist "C:\Program Files\Apache Software Foundation\Tomcat 9.0\webapps\academic-management-platform\WEB-INF\classes\com\ptc\amp\controllers\AuthServlet.class" (
    echo   OK - AuthServlet deployed to Tomcat
) else (
    echo   ERROR - AuthServlet NOT deployed to Tomcat
)
echo.

echo [5] Checking web.xml in Tomcat...
if exist "C:\Program Files\Apache Software Foundation\Tomcat 9.0\webapps\academic-management-platform\WEB-INF\web.xml" (
    echo   OK - web.xml found
    echo.
    echo Contents:
    type "C:\Program Files\Apache Software Foundation\Tomcat 9.0\webapps\academic-management-platform\WEB-INF\web.xml"
) else (
    echo   ERROR - web.xml NOT FOUND in Tomcat
)
echo.

echo [6] Checking libraries...
if exist "lib\mysql-connector-j-9.5.0.jar" (
    echo   OK - MySQL connector found
) else (
    echo   ERROR - MySQL connector NOT FOUND
)

if exist "lib\gson-2.10.1.jar" (
    echo   OK - Gson library found
) else (
    echo   ERROR - Gson library NOT FOUND
)
echo.

echo ========================================
echo Check Complete
echo ========================================
pause