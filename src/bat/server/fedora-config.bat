@echo off

if "%FEDORA_HOME%" == "" goto envErr

set TOMCAT_DIR=@tomcat.basename@
set WEBAPP_DIR=%FEDORA_HOME%\server\%TOMCAT_DIR%\webapps\fedora\WEB-INF

java -Dfedora.home="%FEDORA_HOME%" -cp "%WEBAPP_DIR%\classes" fedora.server.config.ConfigApp %1
goto end

:envErr
echo ERROR: Environment variable, FEDORA_HOME must be set.

:end
