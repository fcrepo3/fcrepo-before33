@echo off

if "%FEDORA_HOME%" == "" goto envErr

rem McKoi environment variables
set MCKOI_BASENAME=@mckoi.basename@
set MCKOI_HOME=%FEDORA_HOME%/server/%MCKOI_BASENAME%
set MCKOI_CLASSPATH=%MCKOI_HOME%/gnu-regexp-1.1.4.jar
set MCKOIDB_JAR=%MCKOI_HOME%/mckoidb.jar
set MCKOI_CONF=%MCKOI_HOME%/db.conf
set MCKOI_DB=%MCKOI_HOME%/data/DefaultDatabase_sf.koi
set MCKOI_PORT=9157

if not exist "%MCKOIDB_JAR%" goto mckoiNotFound

if not exist "%MCKOI_DB%" goto mckoiDBNotInstalled

if "%1" == "" goto showUsage
if "%2" == "" goto showUsage

echo Launching McKoi SQL interface...

java -cp "%MCKOIDB_JAR%" com.mckoi.tools.JDBCQueryTool -u "%1" -p "%2" -url "jdbc:mckoi://127.0.0.1:%MCKOI_PORT%/"

echo Finished.

goto end

:envErr
echo ERROR: Environment variable, FEDORA_HOME must be set.
goto end

:mckoiNotFound
echo ERROR: No mckoidb.jar found in %FEDORA_HOME%\server\mckoi094\
goto end

:mckoiDBNotInstalled
echo ERROR: McKoi database hasn't been initialized, run mckoi-init first.
goto end

:showUsage
echo Usage: mckoi-admin adminUsername adminPassword
goto end

:end
