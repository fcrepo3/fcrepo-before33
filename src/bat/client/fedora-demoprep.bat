@echo off

goto checkEnv
:envOk

set OLD_JAVA_HOME=%JAVA_HOME%
set JAVA_HOME=%THIS_JAVA_HOME%

:runMinimized
%JAVA_HOME%\bin\java -Xms64m -Xmx96m -cp %FEDORA_HOME%\client;%FEDORA_HOME%\client\client.jar -Dfedora.home=%FEDORA_HOME% fedora.client.ingest.AutoIngestor %1 %2 %3 %4 %FEDORA_HOME%\demo\std-img-bdef.xml "first import"
%JAVA_HOME%\bin\java -Xms64m -Xmx96m -cp %FEDORA_HOME%\client;%FEDORA_HOME%\client\client.jar -Dfedora.home=%FEDORA_HOME% fedora.client.ingest.AutoIngestor %1 %2 %3 %4 %FEDORA_HOME%\demo\std-img-mech.xml "first import"

set JAVA_HOME=%OLD_JAVA_HOME%

goto end

:checkEnv
if "%FEDORA_HOME%" == "" goto noFedoraHome
if not exist %FEDORA_HOME%\client\client.jar goto clientNotFound
if "%FEDORA_JAVA_HOME%" == "" goto tryJavaHome
set THIS_JAVA_HOME=%FEDORA_JAVA_HOME%
:checkJava
if not exist %THIS_JAVA_HOME%\bin\java.exe goto noJavaBin
if not exist %THIS_JAVA_HOME%\bin\orbd.exe goto badJavaVersion
goto envOk

:tryJavaHome
echo Warning: FEDORA_JAVA_HOME not set, falling back to JAVA_HOME
if "%JAVA_HOME%" == "" goto noJavaHome
set THIS_JAVA_HOME=%JAVA_HOME%
goto checkJava

:noFedoraHome
echo ERROR: Environment variable, FEDORA_HOME must be set.
goto end

:clientNotFound
echo ERROR: FEDORA_HOME does not appear correctly set.
echo Client cannot be found at %FEDORA_HOME%\client\client.jar
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

:end

