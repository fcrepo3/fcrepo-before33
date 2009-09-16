@echo off

REM Purpose: Uninstall libs previously installed via installLibs
REM Usage  : uninstallLibs.bat PATH_TO_LOCAL_REPO
REM Example: uninstallLibs.bat "C:\Documents and Settings\Username\.m2\repository"

if "%1" == "" goto errorNoArg

if not exist "%1" goto errorNoDir

rmdir /s /q "%1\org\duraspace"
rmdir /s /q "%1\org\fedorarepo"
exit /b 0

:errorNoArg
echo Error: One argument required (path to .m2/repository)
exit /b 1

:errorNoDir
echo Error: Local repository directory (%1) does not exist
exit /b 1

