@echo off

if "%FEDORA_HOME%" == "" goto envErr

if not exist %FEDORA_HOME%\mckoi094\mckoidb.jar goto mckoiNotFound

if not exist %FEDORA_HOME%\mckoi094\data\DefaultDatabase.sf goto mckoiDBNotInstalled

echo Starting McKoi DB...

javaw -cp %FEDORA_HOME%\mckoi094\gnu-regexp-1.1.4.jar -jar %FEDORA_HOME%\mckoi094\mckoidb.jar -conf %FEDORA_HOME%\mckoi094\db.conf

echo Finished.

goto end

:envErr
echo ERROR: Environment variable, FEDORA_HOME must be set.
goto end

:mckoiNotFound
echo ERROR: No mckoidb.jar found in %FEDORA_HOME%\mckoi094\
goto end

:mckoiDBNotInstalled
echo ERROR: McKoi database hasn't been initialized, run mckoi-init first.
goto end

:end