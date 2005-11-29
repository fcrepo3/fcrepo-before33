@echo off
@rem usage is fedora-service-install

goto checkEnv
:envOk

echo Installing Fedora server as Windows Service...

set OLD_JAVA_HOME=%JAVA_HOME%
set JAVA_HOME=%THIS_JAVA_HOME%

@rem Before starting server, check for existence of configuration files.
@rem If not present, log which files are missing
@rem and inform user to run fedora-setup script before starting server.

if not exist %FEDORA_HOME%\server\config\fedora-service.ini goto noFedoraConfig

if exist %FEDORA_HOME%\server\logs\startup.log goto logDirExists
mkdir %FEDORA_HOME%\server\logs > NUL

:logDirExists
fedora-service -i

echo Finished.  Server will automatically start when machine is booted.
echo To start server now, execute   fedora-service-start
goto finish

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
echo ERROR: Fedora Service configuration cannot be found at %FEDORA_HOME%\server\config\fedora-service.ini
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
echo ERROR: Fedora Service configuration cannot be found at %FEDORA_HOME%\server\config\fedora-service.ini
goto noConfig

:noConfig
echo.
echo   It appears that the fedora-service has not been properly installed.
echo   Re-extract the fedora-service.zip file to the correct directory.
echo   The correct directory for your setup is: %FEDORA_HOME%
echo.
goto end

:end

