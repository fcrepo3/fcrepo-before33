@echo off
@rem usage is to setup fedora server security configuration

if "%FEDORA_HOME%" == "" goto envErr

set TOMCAT_DIR=@tomcat.basename@
set WEBAPP_DIR=%FEDORA_HOME%\server\%TOMCAT_DIR%\webapps\fedora\WEB-INF

if "%1" == "" goto usage
if "%1" == "ssl-authenticate-apim" goto secureAPIM
if "%1" == "ssl-authenticate-all" goto secureALL
if "%1" == "no-ssl-authenticate-apim" goto unsecureAPIM
if "%1" == "no-ssl-authenticate-all" goto unsecureALL
goto usage

:secureAPIM
set CONFIG_SUFFIX=secure-apim
goto setup

:secureALL
set CONFIG_SUFFIX=secure-all
goto setup

:unsecureAPIM
set CONFIG_SUFFIX=unsecure-apim
goto setup

:unsecureALL
set CONFIG_SUFFIX=unsecure-all
goto setup

:setup
echo Copying
echo    FROM: %FEDORA_HOME%\server\config\fedora-%CONFIG_SUFFIX%.fcfg 
echo      TO: %FEDORA_HOME%\server\config\fedora.fcfg
copy %FEDORA_HOME%\server\config\fedora-%CONFIG_SUFFIX%.fcfg %FEDORA_HOME%\server\config\fedora.fcfg
echo Copying
echo    FROM: %FEDORA_HOME%\server\config\beSecurity-%CONFIG_SUFFIX%.xml 
echo      TO: %FEDORA_HOME%\server\config\beSecurity.xml
copy %FEDORA_HOME%\server\config\beSecurity-%CONFIG_SUFFIX%.xml %FEDORA_HOME%\server\config\beSecurity.xml
echo Copying
echo    FROM: %WEBAPP_DIR%\web-%CONFIG_SUFFIX%.xml 
echo      TO: %WEBAPP_DIR%\web.xml
copy %WEBAPP_DIR%\web-%CONFIG_SUFFIX%.xml %WEBAPP_DIR%\web.xml
goto success



:usage
echo.
echo Usage: fedora-setup [configuration-name]
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
goto end

:envErr
echo ERROR: Environment variable, FEDORA_HOME must be set.

:success
echo.
echo Fedora security setup complete!
echo Configuration files in play are:
echo    fedora-%CONFIG_SUFFIX%.fcfg
echo    beSecurity-%CONFIG_SUFFIX%.xml
echo    web-%CONFIG_SUFFIX%.xml
goto end

:end
echo Exiting fedora-setup.