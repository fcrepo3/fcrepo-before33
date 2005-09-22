@echo off
@rem usage is to setup fedora server security configuration

if "%FEDORA_HOME%" == "" goto envErr

set TOMCAT_DIR=@tomcat.basename@
set WEBAPP_DIR=%FEDORA_HOME%\server\%TOMCAT_DIR%\webapps\fedora\WEB-INF

if "%1" == "" goto usage
if "%1" == "secure-apim" goto setup
if "%1" == "secure-all" goto setup
if "%1" == "unsecure-apim" goto setup
if "%1" == "unsecure-all" goto setup
goto usage

:setup
echo Copying
echo    FROM: %FEDORA_HOME%\server\config\fedora-%1.fcfg 
echo      TO: %FEDORA_HOME%\server\config\fedora.fcfg
copy %FEDORA_HOME%\server\config\fedora-%1.fcfg %FEDORA_HOME%\server\config\fedora.fcfg
echo Copying
echo    FROM: %FEDORA_HOME%\server\config\beSecurity-%1.xml 
echo      TO: %FEDORA_HOME%\server\config\beSecurity.xml
copy %FEDORA_HOME%\server\config\beSecurity-%1.xml %FEDORA_HOME%\server\config\beSecurity.xml
echo Copying
echo    FROM: %WEBAPP_DIR%\web-%1.xml 
echo      TO: %WEBAPP_DIR%\web.xml
copy %WEBAPP_DIR%\web-%1.xml %WEBAPP_DIR%\web.xml
goto end



:usage
echo Usage: fedora-setup configuration-name
echo.
echo     where configuration-name must be one of the following:
echo         secure-apim   - API-M with basicAuth and SSL; API-A with no basicAuth and no SSL
echo         secure-all    - API-M with basicAuth and SSL; API-A with basicAuth and SSL 
echo         unsecure-apim - API-M with basicAuth but no SSL; API-A with no basicAuth and no SSL
echo         unsecure-all  - API-M with basicAuth but no SSL; API-A with basicAuth but no SSL 
goto end

:envErr
echo ERROR: Environment variable, FEDORA_HOME must be set.

:end
echo.
echo Fedora security setup complete!
echo Configuration files in play are:
echo    fedora-%1.fcfg
echo    beSecurity-%1.xml
echo    web-%1.xml