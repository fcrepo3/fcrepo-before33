@echo off
@rem usage is to setup fedora server security configuration

if "%FEDORA_HOME%" == "" goto envErr

set TOMCAT_DIR=@tomcat.basename@
set TC=%FEDORA_HOME%\server\%TOMCAT_DIR%
set WEBAPP_DIR=%TC%\webapps\fedora\WEB-INF

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

rem Apply properties to fedora-base.fcfg to create fedora.fcfg
set FCFG_HOME=%FEDORA_HOME%\server\config
set MY_PROPS=%FCFG_HOME%\my.properties

set FEDORA_INTERNAL=%FEDORA_HOME%\server\fedora-internal-use

set FCFG_BASE=%FEDORA_INTERNAL%\config\fedora-base.fcfg
set PROPS=%FEDORA_INTERNAL%\config\fedora-%CONFIG_SUFFIX%.properties
set OUT=%FCFG_HOME%\fedora.fcfg

if exist "%MY_PROPS%" goto applyMyProps
goto applyProps
:applyMyProps
echo 
echo Applying
echo     %MY_PROPS%
"%JAVA_HOME%\bin\java" -cp %WEBAPP_DIR%\classes;"%TC%"\common\lib\jrdf-0.3.3.jar \ fedora.server.config.ServerConfiguration %FCFG_BASE% %MY_PROPS% > %OUT%
echo     %PROPS%
move %OUT% %OUT%.tmp
"%JAVA_HOME%\bin\java" -cp %WEBAPP_DIR%\classes fedora.server.config.ServerConfiguration %OUT%.tmp %PROP% > %OUT%
del %OUT%.tmp
goto copy

:applyProps
echo 
echo Applying
echo     %PROPS%
"%JAVA_HOME%\bin\java" -cp %WEBAPP_DIR%\classes fedora.server.config.ServerConfiguration %FCFG_BASE% %PROPS% > %OUT%
goto copy

:copy
echo Copying
echo    FROM: %FEDORA_INTERNAL%\config\beSecurity-%CONFIG_SUFFIX%.xml 
echo      TO: %FEDORA_HOME%\server\config\beSecurity.xml
copy %FEDORA_INTERNAL%\config\beSecurity-%CONFIG_SUFFIX%.xml %FEDORA_HOME%\server\config\beSecurity.xml
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
echo    fedora-%CONFIG_SUFFIX%.properties
echo    beSecurity-%CONFIG_SUFFIX%.xml
echo    web-%CONFIG_SUFFIX%.xml
goto end

:end
echo Exiting fedora-setup.