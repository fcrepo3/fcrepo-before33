@echo off

goto checkEnv

:envOk
set _CP=%CATALINA_HOME%\webapps\fedora\WEB-INF\classes
set _ED=%CATALINA_HOME%\webapps\fedora\WEB-INF\lib;%CATALINA_HOME%\common\endorsed;%CATALINA_HOME%\common\lib
"%JAVA_HOME%\bin\java" -server -Xmn64m -Xms256m -Xmx256m -cp "%_CP%" -Djava.endorsed.dirs="%_ED%" -Dfedora.home="%FEDORA_HOME%" -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl -Dfedora.serverProfile=%1 fedora.server.utilities.rebuild.Rebuild %1
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
