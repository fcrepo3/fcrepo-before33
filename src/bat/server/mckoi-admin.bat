@echo off

if "%FEDORA_HOME%" == "" goto envErr

if not exist %FEDORA_HOME%\server\mckoi094\mckoidb.jar goto mckoiNotFound

if not exist %FEDORA_HOME%\server\mckoi094\data\DefaultDatabase.sf goto mckoiDBNotInstalled

if "%1" == "" goto showUsage
if "%2" == "" goto showUsage

echo Launching McKoi SQL interface...

java -cp %FEDORA_HOME%\server\mckoi094\mckoidb.jar com.mckoi.tools.JDBCQueryTool -u "%1" -p "%2"

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