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

if exist "%MCKOI_DB%" goto mckoiDBInstalled

if "%1" == "" goto showUsage
if "%2" == "" goto showUsage

echo Initializing McKoi DB...

java -cp "%MCKOI_CLASSPATH%" -jar "%MCKOIDB_JAR%" -conf "%MCKOI_CONF%" -create "%1" "%2"

echo Finished.

goto end

:envErr
echo ERROR: Environment variable, FEDORA_HOME must be set.
goto end

:mckoiNotFound
echo ERROR: No mckoidb.jar found in %MCKOI_HOME%  
echo Make sure FEDORA_HOME is set correctly.
goto end

:mckoiDBInstalled
echo ERROR: McKoi database already initialized.  
echo Remove %MCKOI_HOME%\data to delete.
goto end

:showUsage
echo Usage: mckoi-init adminUser adminPass
echo Use your own user and password values, and remember them for later.
goto end

:end