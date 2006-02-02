@echo off
@rem usage is fedora-start profile 

goto checkEnv
:envOk

echo Starting Fedora server...

set TOMCAT_DIR=@tomcat.basename@
set TC=%FEDORA_HOME%\server\%TOMCAT_DIR%
set TC_COMMON=%TC%\common\lib
set AXIS_UTILITY_LIBS=@AxisUtility.windows.libs@
set SERVER_LIBS=@Server.windows.libs@
set SERVER_CONTROLLER_LIBS=@ServerController.windows.libs@
set OLD_JAVA_HOME=%JAVA_HOME%
set JAVA_HOME=%THIS_JAVA_HOME%
set DEPLOY=%FEDORA_HOME%\server\fedora-internal-use\deploy

@rem Before starting server, check for existence of configuration files.
@rem If not present, log which files are missing
@rem and inform user to run fedora-setup script before starting server.

if not exist %FEDORA_HOME%\server\config\fedora.fcfg goto noFedoraConfig
if not exist %FEDORA_HOME%\server\config\beSecurity.xml goto noBeSecurityConfig
if not exist %TC%\webapps\fedora\WEB-INF\web.xml goto noWebXmlConfig

if exist %FEDORA_HOME%\server\logs\startup.log goto logDirExists
mkdir %FEDORA_HOME%\server\logs > NUL

:logDirExists
if "%OS%" == "" goto runMinimized

:runInBackground
if "%1" == "" goto bgNoProfile
echo [DEBUG] running in background
"%JAVA_HOME%\bin\java" -cp %TC%\webapps\fedora\WEB-INF\classes;"%SERVER_LIBS%" -Djavax.net.ssl.trustStore="%FEDORA_HOME%\server\truststore" -Djavax.net.ssl.trustStorePassword=tomcat -Dfedora.home=%FEDORA_HOME% -Dtomcat.dir=%TOMCAT_DIR% fedora.server.BasicServer
start "fedoraBG" /B "%JAVA_HOME%\bin\java" -server -Xmn64m -Xms256m -Xmx256m -cp %TC%\bin\bootstrap.jar;"%JAVA_HOME%"\lib\tools.jar -Djavax.net.ssl.trustStore="%FEDORA_HOME%\server\truststore" -Djavax.net.ssl.trustStorePassword=tomcat -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl -Dfedora.home=%FEDORA_HOME% -Dfedora.serverProfile=%1 -Dclasspath=%TC%\bin\bootstrap.jar;"%JAVA_HOME%"\lib\tools.jar -Djava.endorsed.dirs=%TC%\common\endorsed -Djava.security.manager -Djava.security.policy=%TC%\conf\catalina.policy -Dcatalina.base=%TC% -Dcatalina.home=%TC% -Djava.io.tmpdir=%TC%\temp -Djava.security.auth.login.config=%TC%/conf/jaas.config -Djava.util.logging.config.file=%FEDORA_HOME%\server\config\logging.properties org.apache.catalina.startup.Bootstrap start
goto deploy

:bgNoProfile
"%JAVA_HOME%\bin\java" -cp %TC%\webapps\fedora\WEB-INF\classes;"%SERVER_LIBS%" -Djavax.net.ssl.trustStore="%FEDORA_HOME%\server\truststore" -Djavax.net.ssl.trustStorePassword=tomcat  -Dfedora.home=%FEDORA_HOME% -Dtomcat.dir=%TOMCAT_DIR% fedora.server.BasicServer
start "fedoraBGNP" /B "%JAVA_HOME%\bin\java" -server -Xmn64m -Xms256m -Xmx256m -cp %TC%\bin\bootstrap.jar;"%JAVA_HOME%"\lib\tools.jar -Djavax.net.ssl.trustStore="%FEDORA_HOME%\server\truststore" -Djavax.net.ssl.trustStorePassword=tomcat -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl -Dfedora.home=%FEDORA_HOME% -Dclasspath=%TC%\bin\bootstrap.jar;"%JAVA_HOME%"\lib\tools.jar -Djava.endorsed.dirs=%TC%\common\endorsed -Djava.security.manager -Djava.security.policy=%TC%\conf\catalina.policy -Dcatalina.base=%TC% -Dcatalina.home=%TC% -Djava.io.tmpdir=%TC%\temp -Djava.security.auth.login.config=%TC%/conf/jaas.config -Djava.util.logging.config.file=%FEDORA_HOME%\server\config\logging.properties org.apache.catalina.startup.Bootstrap start
goto deploy

:runMinimized
if "%1" == "" goto minNoProfile
"%JAVA_HOME%\bin\java" -cp %TC%\webapps\fedora\WEB-INF\classes;"%SERVER_LIBS%" -Djavax.net.ssl.trustStore="%FEDORA_HOME%\server\truststore" -Djavax.net.ssl.trustStorePassword=tomcat  -Dfedora.home=%FEDORA_HOME% -Dtomcat.dir=%TOMCAT_DIR% fedora.server.BasicServer
start "fedoraMinimized" /m "%JAVA_HOME%\bin\java" -server -Xmn64m -Xms256m -Xmx256m -cp %TC%\bin\bootstrap.jar;"%JAVA_HOME%"\lib\tools.jar -Djavax.net.ssl.trustStore="%FEDORA_HOME%\server\truststore" -Djavax.net.ssl.trustStorePassword=tomcat  -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl -Dfedora.home=%FEDORA_HOME% -Dfedora.serverProfile=%1 -Dclasspath=%TC%\bin\bootstrap.jar;"%JAVA_HOME%"\lib\tools.jar -Djava.endorsed.dirs=%TC%\common\endorsed -Djava.security.manager -Djava.security.policy=%TC%\conf\catalina.policy -Dcatalina.base=%TC% -Dcatalina.home=%TC% -Djava.io.tmpdir=%TC%\temp -Djava.security.auth.login.config=%TC%/conf/jaas.config -Djava.util.logging.config.file=%FEDORA_HOME%\server\config\logging.properties org.apache.catalina.startup.Bootstrap start
goto deploy

:minNoProfile
"%JAVA_HOME%\bin\java" -cp %TC%\webapps\fedora\WEB-INF\classes;"%SERVER_LIBS%" -Djavax.net.ssl.trustStore="%FEDORA_HOME%\server\truststore" -Djavax.net.ssl.trustStorePassword=tomcat  -Dfedora.home=%FEDORA_HOME% -Dtomcat.dir=%TOMCAT_DIR% fedora.server.BasicServer
start "fedoraMinimizedNP" /m "%JAVA_HOME%\bin\java" -server -Xmn64m -Xms256m -Xmx256m -cp %TC%\bin\bootstrap.jar;"%JAVA_HOME%"\lib\tools.jar -Djavax.net.ssl.trustStore="%FEDORA_HOME%\server\truststore" -Djavax.net.ssl.trustStorePassword=tomcat  -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl -Dfedora.home=%FEDORA_HOME% -Dclasspath=%TC%\bin\bootstrap.jar;"%JAVA_HOME%"\lib\tools.jar -Djava.endorsed.dirs=%TC%\common\endorsed -Djava.security.manager -Djava.security.policy=%TC%\conf\catalina.policy -Dcatalina.base=%TC% -Dcatalina.home=%TC% -Djava.io.tmpdir=%TC%\temp -Djava.security.auth.login.config=%TC%/conf/jaas.config -Djava.util.logging.config.file=%FEDORA_HOME%\server\config\logging.properties org.apache.catalina.startup.Bootstrap start

:deploy

echo Deploying API-M and API-A...
"%JAVA_HOME%\bin\java" -cp %AXIS_UTILITY_LIBS%;%TC%\webapps\fedora\WEB-INF\classes;%TC%\webapps\fedora\WEB-INF\lib\commons-httpclient-2.0.1.jar;%TC%\webapps\fedora\WEB-INF\lib\commons-logging.jar -Djavax.net.ssl.trustStore="%FEDORA_HOME%\server\truststore" -Djavax.net.ssl.trustStorePassword=tomcat -Dfedora.home=%FEDORA_HOME% -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.server.utilities.AxisUtility deploy %DEPLOY%\deployAPI-A.wsdd 60
if errorlevel 1 goto deployError

"%JAVA_HOME%\bin\java" -cp %AXIS_UTILITY_LIBS%;%TC%\webapps\fedora\WEB-INF\classes;%TC%\webapps\fedora\WEB-INF\lib\commons-httpclient-2.0.1.jar;%TC%\webapps\fedora\WEB-INF\lib\commons-logging.jar -Djavax.net.ssl.trustStore="%FEDORA_HOME%\server\truststore" -Djavax.net.ssl.trustStorePassword=tomcat -Dfedora.home=%FEDORA_HOME% -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.server.utilities.AxisUtility deploy %DEPLOY%\deploy.wsdd 60
if errorlevel 1 goto deployError

echo Initializing Fedora Server instance...
"%JAVA_HOME%\bin\java" -cp %TC%\webapps\fedora\WEB-INF\classes;%TC%\webapps\fedora\WEB-INF\lib\commons-httpclient-2.0.1.jar;%TC%\webapps\fedora\WEB-INF\lib\commons-logging.jar;%SERVER_CONTROLLER_LIBS% -Djavax.net.ssl.trustStore="%FEDORA_HOME%\server\truststore" -Djavax.net.ssl.trustStorePassword=tomcat -Dfedora.home=%FEDORA_HOME% fedora.server.utilities.ServerUtility startup

echo Finished.  To stop the server, use fedora-stop.
goto finish

:deployError
echo Error deploying (see above)... to stop the server, use fedora-stop.

:finish
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

:end

