@echo off

goto checkEnv
:envOk

echo Starting Fedora server...

set TC=%FEDORA_HOME%\server\tomcat41
set OLD_JAVA_HOME=%JAVA_HOME%
set JAVA_HOME=%THIS_JAVA_HOME%

if exist %FEDORA_HOME%\server\logs\startup.log goto logDirExists
mkdir %FEDORA_HOME%\server\logs > NUL

:logDirExists
if "%OS%" == "" goto runMinimized

:runInBackground
if "%1" == "" goto bgNoProfile
%JAVA_HOME%\bin\java -cp %TC%\webapps\fedora\WEB-INF\classes -Dfedora.home=%FEDORA_HOME% fedora.server.BasicServer
start /B %JAVA_HOME%\bin\java -Xms96m -Xmx128m -Xnoagent -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n -Djava.compiler=none -cp %TC%\bin\bootstrap.jar -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl -Dfedora.home=%FEDORA_HOME% -Dfedora.serverProfile=%1 -Dclasspath=%TC%\bin\bootstrap.jar -Djava.endorsed.dirs=%TC%\bin -Djava.security.manager -Djava.security.policy=%TC%\conf\catalina.policy -Dcatalina.base=%TC% -Dcatalina.home=%TC% -Djava.io.tmpdir=%TC%\temp org.apache.catalina.startup.Bootstrap start
goto deploy
:bgNoProfile
%JAVA_HOME%\bin\java -cp %TC%\webapps\fedora\WEB-INF\classes -Dfedora.home=%FEDORA_HOME% fedora.server.BasicServer
start /B %JAVA_HOME%\bin\java -Xms96m -Xmx128m -Xnoagent -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n -Djava.compiler=none -cp %TC%\bin\bootstrap.jar -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl -Dfedora.home=%FEDORA_HOME% -Dclasspath=%TC%\bin\bootstrap.jar -Djava.endorsed.dirs=%TC%\bin -Djava.security.manager -Djava.security.policy=%TC%\conf\catalina.policy -Dcatalina.base=%TC% -Dcatalina.home=%TC% -Djava.io.tmpdir=%TC%\temp org.apache.catalina.startup.Bootstrap start
goto deploy

:runMinimized
if "%1" == "" goto minNoProfile
%JAVA_HOME%\bin\java -cp %TC%\webapps\fedora\WEB-INF\classes -Dfedora.home=%FEDORA_HOME% fedora.server.BasicServer
start /m %JAVA_HOME%\bin\java -Xms96m -Xmx128m -Xnoagent -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n -Djava.compiler=none -cp %TC%\bin\bootstrap.jar -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl -Dfedora.home=%FEDORA_HOME% -Dfedora.serverProfile=%1 -Dclasspath=%TC%\bin\bootstrap.jar -Djava.endorsed.dirs=%TC%\bin -Djava.security.manager -Djava.security.policy=%TC%\conf\catalina.policy -Dcatalina.base=%TC% -Dcatalina.home=%TC% -Djava.io.tmpdir=%TC%\temp org.apache.catalina.startup.Bootstrap start
goto deploy
:minNoProfile
%JAVA_HOME%\bin\java -cp %TC%\webapps\fedora\WEB-INF\classes -Dfedora.home=%FEDORA_HOME% fedora.server.BasicServer
start /m %JAVA_HOME%\bin\java -Xms96m -Xmx128m -Xnoagent -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n -Djava.compiler=none -cp %TC%\bin\bootstrap.jar -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl -Dfedora.home=%FEDORA_HOME% -Dclasspath=%TC%\bin\bootstrap.jar -Djava.endorsed.dirs=%TC%\bin -Djava.security.manager -Djava.security.policy=%TC%\conf\catalina.policy -Dcatalina.base=%TC% -Dcatalina.home=%TC% -Djava.io.tmpdir=%TC%\temp org.apache.catalina.startup.Bootstrap start

:deploy
set C=%TC%\common\lib
set CP=%C%\xerces2-2.0.2.jar;%C%\saaj.jar;%C%\commons-discovery.jar;%C%\axis.jar;%C%\commons-logging.jar;%C%\jaxrpc.jar;%C%\wsdl4j.jar;%C%\tt-bytecode.jar
echo Deploying API-M and API-A...
%JAVA_HOME%\bin\java -cp %CP%;%TC%\webapps\fedora\WEB-INF\classes -Dfedora.home=%FEDORA_HOME% -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.server.utilities.AxisUtility deploy %FEDORA_HOME%\config\deployAPI-A.wsdd 15
if errorlevel 1 goto deployError

%JAVA_HOME%\bin\java -cp %CP%;%TC%\webapps\fedora\WEB-INF\classes -Dfedora.home=%FEDORA_HOME% -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.server.utilities.AxisUtility deploy %FEDORA_HOME%\config\deploy.wsdd 15
if errorlevel 1 goto deployError

echo Initializing Fedora Server instance...
%JAVA_HOME%\bin\java -cp %TC%\webapps\fedora\WEB-INF\classes;%TC%\common\lib\servlet.jar -Dfedora.home=%FEDORA_HOME% fedora.server.ServerController startup

echo Starting jdb...
%JAVA_HOME%\bin\jdb -connect com.sun.jdi.SocketAttach:hostname=localhost,port=8000

echo Finished.  To stop the server, use fedora-stop.
goto finish

:deployError
echo Error deploying (see above)... to stop the server, use fedora-stop.

:finish
set JAVA_HOME=%OLD_JAVA_HOME%

goto end

:checkEnv
if "%FEDORA_HOME%" == "" goto noFedoraHome
if not exist %FEDORA_HOME%\server\config\fedora.fcfg goto configNotFound
if "%FEDORA_JAVA_HOME%" == "" goto tryJavaHome
set THIS_JAVA_HOME=%FEDORA_JAVA_HOME%
:checkJava
if not exist %THIS_JAVA_HOME%\bin\java.exe goto noJavaBin
if not exist %THIS_JAVA_HOME%\bin\orbd.exe goto badJavaVersion
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

:end

