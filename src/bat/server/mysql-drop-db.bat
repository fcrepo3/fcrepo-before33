@echo off
rem
rem ----- MS-DOS batch script that will drop the specified Fedora database and then
rem ----- recreate it. It is used for testing when you need to get a clean copy of
rem ----- of all the database entities. 
rem
rem ----- mysql_home      - the location where MySQL is installed
rem ----- mysql_dba_user  - the name of the MySQL user with dba privileges (default: root)
rem ----- mysql_dba_pass  - the password for the MySQL user with dba privileges (default" none)
rem ----- mysql_db_name   - the name of the Fedora database (default: fedora20)

rem ----- check for input arguments

if "%1" == "" goto showUsage
if "%2" == "" goto showUsage
if "%3" == "" goto showUsage
if "%4" == "" goto showUsage

set mysql_home=%1
echo MySQL install directory: %mysql_home%

set mysql_dba_user=%2
echo MySQL dba username: %mysql_dba_user%

set mysql_dba_pass=%3
echo Mysql dba password: %mysql_dba_password%

set fedora_db_name=%4
echo Fedora database name: %fedora_db_name%

set path=%path%;%mysql_home%\bin
echo mySQL home: %mysql_home%

echo.
echo Dropping Fedora database: %fedora_db_name%
echo.

rem ----- Drop Fedora database using specified database name
mysqladmin -u %mysql_dba_user% -p%mysql_dba_pass% -h localhost drop %fedora_db_name%

echo.
echo Creating Fedora database: %fedora_db_name%
echo.

rem ----- Create Fedora database using specified database name
mysqladmin -u %mysql_dba_user% -p%mysql_dba_pass% -h localhost create %fedora_db_name%

echo.
echo Fedora database re-initialization complete!
echo.
goto end


:showUsage
echo.
echo Usage: mysql-drop-db.bat mysqlHome mysqlDBAUser mysqlDBAPass fedoraDbName
echo.
echo mysqlHome - The location where mySQL is installed (e.g., c:\mysql)
echo mysqlDBAUser - mySQL username with DBA authority  (root if new install)
echo mysqlDBAPass - mySQL password for DBA username    (none if new install)
echo fedoraDbName - Fedora database name (use fedora20 as default)
echo.
goto end

:end