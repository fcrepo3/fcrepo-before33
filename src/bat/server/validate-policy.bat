@echo off

goto checkEnv

:envOk
if "%1" == "" goto noFileError
set _CP=%CATALINA_HOME%\webapps\fedora\WEB-INF\classes
set _ED=%CATALINA_HOME%\webapps\fedora\WEB-INF\lib;%CATALINA_HOME%\common\endorsed;%CATALINA_HOME%\common\lib
set _SC=%FEDORA_HOME%\server\xsd\cs-xacml-schema-policy-01.xsd
"%JAVA_HOME%\bin\java" -cp "%_CP%" -Djava.endorsed.dirs="%_ED%" -Dfedora.home="%FEDORA_HOME%" -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl -Dcom.sun.xacml.PolicySchema="%_SC%" fedora.server.security.ValidatePolicy "%1"
if errorlevel 1 goto validationFailed
echo Validation successful
set _CP=
set _ED=
set _SC=
goto end

:checkEnv
if "%JAVA_HOME%" == "" goto noJavaHome
if "%FEDORA_HOME%" == "" goto noFedoraHome
if "%CATALINA_HOME%" == "" goto noCatalinaHome
goto envOk

:noFileError
echo ERROR: Must supply filename.
goto endWithError

:noJavaHome
echo ERROR: Environment variable, JAVA_HOME must be set.
goto endWithError

:noFedoraHome
echo ERROR: Environment variable, FEDORA_HOME must be set.
goto endWithError

:noCatalinaHome
echo ERROR: Environment variable, CATALINA_HOME must be set.
goto endWithError

:validationFailed
echo Validation failed

:endWithError
exit /B 1

:end
