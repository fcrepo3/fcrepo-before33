@echo off

goto checkEnv
:envOk

rem McKoi environment variables
set MCKOI_BASENAME=@mckoi.basename@
set MCKOI_HOME="%FEDORA_HOME%"/server/"%MCKOI_BASENAME%"
set MCKOI_CLASSPATH="%MCKOI_HOME%"/gnu-regexp-1.1.4.jar
set MCKOIDB_JAR="%MCKOI_HOME%"/mckoidb.jar
set MCKOI_CONF="%MCKOI_HOME%"/db.conf
set MCKOI_DB="%MCKOI_HOME%"/data/DefaultDatabase_sf.koi
set MCKOI_PORT=9157

if not exist "%MCKOIDB_JAR%" goto mckoiNotFound
if not exist "%MCKOI_DB%" goto mckoiNotInitialized

echo Starting McKoi DB...

set OLD_JAVA_HOME=%JAVA_HOME%
set JAVA_HOME=%THIS_JAVA_HOME%

if "%OS%" == "" goto runMinimized

:runInBackground
start "mcKoiBG" /B "%JAVA_HOME%\bin\java" -Xms64m -Xmx96m -cp "%MCKOI_CLASSPATH%" -jar "%MCKOIDB_JAR%" -conf "%MCKOI_CONF%"
goto :doneRunning

:runMinimized
start "mckoiMin" /m "%JAVA_HOME%\bin\java" -Xms64m -Xmx96m -cp "%MCKOI_CLASSPATH%" -jar "%MCKOIDB_JAR%" -conf "%MCKOI_CONF%"

:doneRunning
set JAVA_HOME=%OLD_JAVA_HOME%

echo To stop the server, use mckoi-stop.

goto end

:mckoiNotFound
echo ERROR: No mckoidb.jar found in %MCKOI_HOME%
goto end

:mckoiNotInitialized
echo ERROR: McKoi database hasn't been initialized, run mckoi-init first.
goto end

:checkEnv
if "%FEDORA_HOME%" == "" goto noFedoraHome
if not exist "%FEDORA_HOME%\server\config\fedora.fcfg" goto configNotFound
if "%FEDORA_JAVA_HOME%" == "" goto tryJavaHome
set THIS_JAVA_HOME=%FEDORA_JAVA_HOME%
:checkJava
if not exist "%THIS_JAVA_HOME%\bin\java.exe" goto noJavaBin
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