@echo off

goto checkEnv

:envOk
set _CP=%CATALINA_HOME%\webapps\fedora\WEB-INF\classes
set _ED=%CATALINA_HOME%\webapps\fedora\WEB-INF\lib;%CATALINA_HOME%\common\endorsed;%CATALINA_HOME%\common\lib
"%JAVA_HOME%\bin\java" -cp "%_CP%" -Djavax.net.ssl.trustStore="%FEDORA_HOME%\server\truststore" -Djavax.net.ssl.trustStorePassword=tomcat -Djava.endorsed.dirs="%_ED%" -Dfedora.home="%FEDORA_HOME%" fedora.server.utilities.ServerUtility %1 %2 %3
if errorlevel 1 goto endWithError
set _CP=
set _ED=
goto end

:checkEnv
if "%JAVA_HOME%" == "" goto noJavaHome
if "%FEDORA_HOME%" == "" goto noFedoraHome
if "%CATALINA_HOME%" == "" goto noCatalinaHome
goto envOk

:noJavaHome
echo ERROR: Environment variable, JAVA_HOME must be set.
goto endWithError

:noFedoraHome
echo ERROR: Environment variable, FEDORA_HOME must be set.
goto endWithError

:noCatalinaHome
echo ERROR: Environment variable, CATALINA_HOME must be set.
goto endWithError

:endWithError
exit /B 1

:end
