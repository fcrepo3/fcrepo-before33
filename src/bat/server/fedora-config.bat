@echo off

if "%FEDORA_HOME%" == "" goto envErr

set TOMCAT_DIR=@tomcat.basename@
set WEBAPP_DIR=%FEDORA_HOME%\server\%TOMCAT_DIR%\webapps\fedora\WEB-INF
set TC_COMMON=%FEDORA_HOME%\server\%TOMCAT_DIR%\common

java -Dfedora.home="%FEDORA_HOME%" -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl -cp "%WEBAPP_DIR%\classes;%TC_COMMON%\lib\xercesImpl.jar" fedora.server.config.ConfigApp %1
goto end

:envErr
echo ERROR: Environment variable, FEDORA_HOME must be set.

:end
