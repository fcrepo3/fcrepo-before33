@echo off

if "%FEDORA_HOME%" == "" goto envErr

if not exist %FEDORA_HOME%\mckoi094\mckoidb.jar goto mckoiNotFound

if exist %FEDORA_HOME%\mckoi094\data\DefaultDatabase.sf goto mckoiDBInstalled

if "%1" == "" goto showUsage
if "%2" == "" goto showUsage

echo Initializing McKoi DB...

java -cp %FEDORA_HOME%\mckoi094\gnu-regexp-1.1.4.jar -jar %FEDORA_HOME%\mckoi094\mckoidb.jar -conf %FEDORA_HOME%\mckoi094\db.conf -create "%1" "%2"

echo Finished.

goto end

:envErr
echo ERROR: Environment variable, FEDORA_HOME must be set.
goto end

:mckoiNotFound
echo ERROR: No mckoidb.jar found in %FEDORA_HOME%\mckoi094\  
echo Make sure FEDORA_HOME is set correctly.
goto end

:mckoiDBInstalled
echo ERROR: McKoi database already initialized.  
echo Remove %FEDORA_HOME%\mckoi094\data to delete.
goto end

:showUsage
echo Usage: mckoi-init adminUser adminPass
echo Use your own user and password values, and remember them for later.
goto end

:end