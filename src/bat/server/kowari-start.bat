@echo off

goto EndOfComment
  kowari-start.bat
    Starts the Kowari server. 
    Kowari provides the RDF store for the Fedora ResourceIndex
    
    If Kowari will be running on a different host (i.e. different server) 
    than the Fedora server, make sure to set the KOWARI_HOSTNAME below to a
    hostname or ip address accessible by the Fedora server--i.e., do
    not use localhost.
    
    Change the ports as needed, making sure to update any corresponding
    entries in fedora.fcfg.
    
:EndOfComment

set KOWARI_HOSTNAME=localhost
set KOWARI_HTTP_PORT=8081
set KOWARI_RMI_PORT=1099
set KOWARI_HOME=%FEDORA_HOME%\server\kowari
set KOWARI_JAR=kowari-1.0.4.jar

goto checkEnv

:envOk

echo Starting Kowari Server...

set OLD_JAVA_HOME=%JAVA_HOME%
set JAVA_HOME=%THIS_JAVA_HOME%

%JAVA_HOME%\bin\java -jar %KOWARI_HOME%\%KOWARI_JAR% --serverhost %KOWARI_HOSTNAME% --path %KOWARI_HOME% --servername fedoraResourceIndex --port %KOWARI_HTTP_PORT% --rmiport %KOWARI_RMI_PORT%

:doneRunning
set JAVA_HOME=%OLD_JAVA_HOME%
echo ...

goto end

:kowariNotFound
echo ERROR: No %KOWARI_JAR% found in %KOWARI_HOME%
goto end

:checkEnv
if "%FEDORA_HOME%" == "" goto noFedoraHome
if not exist %FEDORA_HOME%\server\config\fedora.fcfg goto configNotFound
if "%FEDORA_JAVA_HOME%" == "" goto tryJavaHome
set THIS_JAVA_HOME=%FEDORA_JAVA_HOME%
if not exist %KOWARI_HOME%\%KOWARI_JAR% goto kowariNotFound

:tryJavaHome
if "%JAVA_HOME%" == "" goto noJavaHome
set THIS_JAVA_HOME=%JAVA_HOME%
goto checkJava

:checkJava
if not exist %THIS_JAVA_HOME%\bin\java.exe goto noJavaBin
goto envOk

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

:end