@echo off

goto checkEnv
:envOk

if not exist %FEDORA_HOME%\mckoi094\mckoidb.jar goto mckoiNotFound
if not exist %FEDORA_HOME%\mckoi094\data\DefaultDatabase.sf goto mckoiNotInitialized

echo Starting McKoi DB...

set OLD_JAVA_HOME=%JAVA_HOME%
set JAVA_HOME=%THIS_JAVA_HOME%

if "%OS%" == "" goto runMinimized

:runInBackground
start /B %JAVA_HOME%\bin\java -Xms32m -Xmx64m -cp %FEDORA_HOME%\mckoi094\gnu-regexp-1.1.4.jar -jar %FEDORA_HOME%\mckoi094\mckoidb.jar -conf %FEDORA_HOME%\mckoi094\db.conf
goto :doneRunning

:runMinimized
start /m %JAVA_HOME%\bin\java -Xms32m -Xmx64m -cp %FEDORA_HOME%\mckoi094\gnu-regexp-1.1.4.jar -jar %FEDORA_HOME%\mckoi094\mckoidb.jar -conf %FEDORA_HOME%\mckoi094\db.conf

:doneRunning
set JAVA_HOME=%OLD_JAVA_HOME%

echo To stop the server, use mckoi-stop.

goto end

:mckoiNotFound
echo ERROR: No mckoidb.jar found in %FEDORA_HOME%\mckoi094\
goto end

:mckoiNotInitialized
echo ERROR: McKoi database hasn't been initialized, run mckoi-init first.
goto end

:checkEnv
if "%FEDORA_HOME%" == "" goto noFedoraHome
if not exist %FEDORA_HOME%\config\fedora.fcfg goto configNotFound
if "%FEDORA_JAVA_HOME%" == "" goto tryJavaHome
set THIS_JAVA_HOME=%FEDORA_JAVA_HOME%
:checkJava
if not exist %THIS_JAVA_HOME%\bin\java.exe goto noJavaBin
goto envOk

:tryJavaHome
echo Warning: FEDORA_JAVA_HOME not set, falling back to JAVA_HOME
if "%JAVA_HOME%" == "" goto noJavaHome
set THIS_JAVA_HOME=%JAVA_HOME%
goto checkJava

:noFedoraHome
echo ERROR: Environment variable, FEDORA_HOME must be set.
goto end

:configNotFound
echo ERROR: FEDORA_HOME does not appear correctly set.
echo Configuration cannot be found at %FEDORA_HOME%\config\fedora.fcfg
goto end

:noJavaHome
echo ERROR: FEDORA_JAVA_HOME was not defined, nor was (the fallback) JAVA_HOME.
goto end

:noJavaBin
echo ERROR: java.exe was not found in %THIS_JAVA_HOME%
echo Make sure FEDORA_JAVA_HOME or JAVA_HOME is set correctly.
goto end

:end