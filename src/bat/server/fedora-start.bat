@echo off
@rem usage is fedora-start profile 

goto checkEnv
:envOk

echo Starting the Fedora server...

set TOMCAT_DIR=@tomcat.basename@
set TC=%FEDORA_HOME%\server\%TOMCAT_DIR%
set TC_COMMON=%TC%\common\lib
set AXIS_UTILITY_LIBS=@AxisUtility.windows.libs@
set SERVER_LIBS=@Server.windows.libs@
set SERVER_CONTROLLER_LIBS=@ServerController.windows.libs@
set OLD_JAVA_HOME=%JAVA_HOME%
set JAVA_HOME=%THIS_JAVA_HOME%

@rem Before starting server, check for existence of configuration files.
@rem If not present, log which files are missing
@rem and inform user to run fedora-setup script before starting server.

if not exist %FEDORA_HOME%\server\config\fedora.fcfg goto noFedoraConfig
if not exist %FEDORA_HOME%\server\config\beSecurity.xml goto noBeSecurityConfig
if not exist %TC%\webapps\fedora\WEB-INF\web.xml goto noWebXmlConfig

if exist %FEDORA_HOME%\server\logs\startup.log goto initStartup
mkdir %FEDORA_HOME%\server\logs > NUL

:initStartup
"%JAVA_HOME%\bin\java" -cp %TC%\webapps\fedora\WEB-INF\classes;"%SERVER_LIBS%" -Djavax.net.ssl.trustStore="%FEDORA_HOME%\server\truststore" -Djavax.net.ssl.trustStorePassword=tomcat -Dfedora.home=%FEDORA_HOME% -Dtomcat.dir=%TOMCAT_DIR% fedora.server.BasicServer
if errorlevel 1 goto endWithError
"%JAVA_HOME%\bin\java" -cp %TC%\webapps\fedora\WEB-INF\classes;"%SERVER_LIBS%" -Djavax.net.ssl.trustStore="%FEDORA_HOME%\server\truststore" -Djavax.net.ssl.trustStorePassword=tomcat -Dfedora.home=%FEDORA_HOME% -Dtomcat.dir=%TOMCAT_DIR% fedora.server.utilities.status.ServerStatusTool init
if errorlevel 1 goto endWithError

if "%OS%" == "" goto runMinimized

:runInBackground
if "%1" == "" goto bgNoProfile
echo [DEBUG] running in background
start "fedoraBG" /B "%JAVA_HOME%\bin\java" -server -Xmn64m -Xms256m -Xmx256m -cp %TC%\bin\bootstrap.jar;"%JAVA_HOME%"\lib\tools.jar -Djavax.net.ssl.trustStore="%FEDORA_HOME%\server\truststore" -Djavax.net.ssl.trustStorePassword=tomcat -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl -Dfedora.home=%FEDORA_HOME% -Dfedora.serverProfile=%1 -Dclasspath=%TC%\bin\bootstrap.jar;"%JAVA_HOME%"\lib\tools.jar -Djava.endorsed.dirs=%TC%\common\endorsed -Djava.security.manager -Djava.security.policy=%TC%\conf\catalina.policy -Dcatalina.base=%TC% -Dcatalina.home=%TC% -Djava.io.tmpdir=%TC%\temp -Djava.security.auth.login.config=%TC%/conf/jaas.config -Djava.util.logging.config.file=%FEDORA_HOME%\server\config\logging.properties %FEDORA_JAVA_OPTIONS% org.apache.catalina.startup.Bootstrap start
goto finishStartup

:bgNoProfile
start "fedoraBGNP" /B "%JAVA_HOME%\bin\java" -server -Xmn64m -Xms256m -Xmx256m -cp %TC%\bin\bootstrap.jar;"%JAVA_HOME%"\lib\tools.jar -Djavax.net.ssl.trustStore="%FEDORA_HOME%\server\truststore" -Djavax.net.ssl.trustStorePassword=tomcat -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl -Dfedora.home=%FEDORA_HOME% -Dclasspath=%TC%\bin\bootstrap.jar;"%JAVA_HOME%"\lib\tools.jar -Djava.endorsed.dirs=%TC%\common\endorsed -Djava.security.manager -Djava.security.policy=%TC%\conf\catalina.policy -Dcatalina.base=%TC% -Dcatalina.home=%TC% -Djava.io.tmpdir=%TC%\temp -Djava.security.auth.login.config=%TC%/conf/jaas.config -Djava.util.logging.config.file=%FEDORA_HOME%\server\config\logging.properties %FEDORA_JAVA_OPTIONS% org.apache.catalina.startup.Bootstrap start
goto finishStartup

:runMinimized
if "%1" == "" goto minNoProfile
start "fedoraMinimized" /m "%JAVA_HOME%\bin\java" -server -Xmn64m -Xms256m -Xmx256m -cp %TC%\bin\bootstrap.jar;"%JAVA_HOME%"\lib\tools.jar -Djavax.net.ssl.trustStore="%FEDORA_HOME%\server\truststore" -Djavax.net.ssl.trustStorePassword=tomcat  -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl -Dfedora.home=%FEDORA_HOME% -Dfedora.serverProfile=%1 -Dclasspath=%TC%\bin\bootstrap.jar;"%JAVA_HOME%"\lib\tools.jar -Djava.endorsed.dirs=%TC%\common\endorsed -Djava.security.manager -Djava.security.policy=%TC%\conf\catalina.policy -Dcatalina.base=%TC% -Dcatalina.home=%TC% -Djava.io.tmpdir=%TC%\temp -Djava.security.auth.login.config=%TC%/conf/jaas.config -Djava.util.logging.config.file=%FEDORA_HOME%\server\config\logging.properties %FEDORA_JAVA_OPTIONS% org.apache.catalina.startup.Bootstrap start
goto finishStartup

:minNoProfile
start "fedoraMinimizedNP" /m "%JAVA_HOME%\bin\java" -server -Xmn64m -Xms256m -Xmx256m -cp %TC%\bin\bootstrap.jar;"%JAVA_HOME%"\lib\tools.jar -Djavax.net.ssl.trustStore="%FEDORA_HOME%\server\truststore" -Djavax.net.ssl.trustStorePassword=tomcat  -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl -Dfedora.home=%FEDORA_HOME% -Dclasspath=%TC%\bin\bootstrap.jar;"%JAVA_HOME%"\lib\tools.jar -Djava.endorsed.dirs=%TC%\common\endorsed -Djava.security.manager -Djava.security.policy=%TC%\conf\catalina.policy -Dcatalina.base=%TC% -Dcatalina.home=%TC% -Djava.io.tmpdir=%TC%\temp -Djava.security.auth.login.config=%TC%/conf/jaas.config -Djava.util.logging.config.file=%FEDORA_HOME%\server\config\logging.properties %FEDORA_JAVA_OPTIONS% org.apache.catalina.startup.Bootstrap start

:finishStartup
"%JAVA_HOME%\bin\java" -cp %TC%\webapps\fedora\WEB-INF\classes;"%SERVER_LIBS%" -Djavax.net.ssl.trustStore="%FEDORA_HOME%\server\truststore" -Djavax.net.ssl.trustStorePassword=tomcat -Dfedora.home=%FEDORA_HOME% -Dtomcat.dir=%TOMCAT_DIR% fedora.server.utilities.status.ServerStatusTool watch-startup
if errorlevel 1 goto abortStartup

echo Finished.  To stop server, use fedora-stop.
set JAVA_HOME=%OLD_JAVA_HOME%

goto end

:checkEnv
if "%FEDORA_HOME%" == "" goto noFedoraHome
if not exist %FEDORA_HOME%\server\config goto configNotFound
if "%FEDORA_JAVA_HOME%" == "" goto tryJavaHome
set THIS_JAVA_HOME=%FEDORA_JAVA_HOME%

:checkJava
if not exist "%THIS_JAVA_HOME%\bin\java.exe" goto noJavaBin
if not exist "%THIS_JAVA_HOME%\bin\orbd.exe" goto badJavaVersion
goto envOk

:tryJavaHome
if "%JAVA_HOME%" == "" goto noJavaHome
set THIS_JAVA_HOME=%JAVA_HOME%
goto checkJava

:noFedoraHome
echo ERROR: Environment variable, FEDORA_HOME must be set.
goto end

:configNotFound
echo ERROR: FEDORA_HOME does not appear correctly set.
echo Configuration cannot be found at %FEDORA_HOME%\server\config\fedora.fcfg
goto end

:noJavaHome
echo ERROR: FEDORA_JAVA_HOME was not defined, nor was (the fallback) JAVA_HOME.
goto end

:noJavaBin
echo ERROR: java.exe was not found in %THIS_JAVA_HOME%
echo Make sure FEDORA_JAVA_HOME or JAVA_HOME is set correctly.
goto end

:badJavaVersion
echo ERROR: java was found in %THIS_JAVA_HOME%, but it was not version 1.4
echo Make sure FEDORA_JAVA_HOME or JAVA_HOME points to a 1.4JRE/JDK base.
goto end

:noFedoraConfig
echo ERROR: Unable to locate file: %FEDORA_HOME%\server\config\fedora.fcfg
if not exist %FEDORA_HOME%\server\config\beSecurity.xml goto noBeSecurityConfig
if not exist %TC%\webapps\fedora\WEB-INF\web.xml goto noWebXmlConfig
goto noConfig

:noBeSecurityConfig
echo ERROR: Unable to locate file: %FEDORA_HOME%\server\config\beSecurity.xml
if not exist %TC%\webapps\fedora\WEB-INF\web.xml goto noWebXmlConfig
goto noConfig

:noWebXmlConfig
echo ERROR: Unable to locate file: %TC%\webapps\fedora\WEB-INF\web.xml
goto noConfig

:noConfig
echo.
echo   It appears that the server has not been initially configured.
echo   Run the fedora-setup.bat script to initially configure the server
echo   with the desired configuration. e.g.,
echo.
echo   fedora-setup [configuration-name]
echo.
echo   where [configuration-name] must be one of the following:
echo.
echo       ssl-authenticate-apim - API-M with basicAuth and SSL
echo                             - API-A with no basicAuth and no SSL
echo.
echo       ssl-authenticate-all  - API-M with basicAuth and SSL
echo                             - API-A with basicAuth and SSL 
echo.
echo       no-ssl-authenticate-apim - API-M with basicAuth but no SSL
echo                                - API-A with no basicAuth and no SSL
echo.
echo       no-ssl-authenticate-all  - API-M with basicAuth but no SSL
echo                                - API-A with basicAuth but no SSL  
echo.
goto end

:abortStartup
echo Stopping Tomcat due to startup failure...
@ping 127.0.0.1 -n 5 -w 1000>nul
"%JAVA_HOME%\bin\java" -Xms64m -Xmx96m -cp %TC%\bin\bootstrap.jar -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl -Dfedora.home=%FEDORA_HOME% -Dclasspath=%TC%\bin\bootstrap.jar -Djava.endorsed.dirs=%TC%\common\endorsed -Djava.security.manager -Djava.security.policy=%TC%\conf\catalina.policy -Dcatalina.base=%TC% -Dcatalina.home=%TC% -Djava.io.tmpdir=%TC%\temp org.apache.catalina.startup.Bootstrap stop

:endWithError
exit /B 1

:end

