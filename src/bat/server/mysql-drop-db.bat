@echo off
rem
rem ----- MS-DOS batch script that will drop the specified Fedora database and then
rem ----- recreate it. It is used for testing when you need to get a clean copy of
rem ----- of all the database entities. 
rem
rem ----- mysql_home      - the location where MySQL is installed (use double quotes if path contains spaces, e.g. "C:\Program Files\MySQL\MySQL server 4.1")
rem ----- mysql_dba_user  - the name of the MySQL user with dba privileges (default: root)
rem ----- mysql_dba_pass  - the password for the MySQL user with dba privileges (default" none)
rem ----- mysql_db_name   - the name of the Fedora database (default: fedora20)
rem ----- mysql41_flag    - optional MySQL 4.1 flag. Any string value will indicate that you are running MySQL 4.1.x.

rem ----- check for input arguments


if "%4" == "" goto showUsage

set mysql_home=%1
echo MySQL install directory: %mysql_home%

set mysql_dba_user=%2
echo MySQL dba username: %mysql_dba_user%

set mysql_dba_pass=%3
echo Mysql dba password: %mysql_dba_password%

set fedora_db_name=%4
echo Fedora database name: %fedora_db_name%

set mysql41_flag=%5

if "%mysql41_flag%" ==  "" echo MySQL flag is: OFF
if "%mysql41_flag%" neq "" echo MySQL flag is: ON

set path=%path%;%mysql_home%\bin
echo mySQL home: %mysql_home%

echo.
echo Dropping Fedora database: %fedora_db_name%
echo.

rem ----- Drop Fedora database using specified database name
mysqladmin -u %mysql_dba_user% -p%mysql_dba_pass% -f -h localhost drop %fedora_db_name%

echo.
echo Creating Fedora database: %fedora_db_name%
echo.

rem ----- Create Fedora database using specified database name
mysqladmin -u %mysql_dba_user% -p%mysql_dba_pass% -h localhost create %fedora_db_name%


if "%mysql41_flag%" == "" goto mysqlOld

echo.
echo MySQL 4.1 flag is ON.
echo Set default character set and collation changes for MySQL 4.1.x databases:
echo.
echo Fedora database %fedora_db_name% DEFAULT CHARACTER SET: utf8;
echo Fedora database %fedora_db_name% DEFAULT COLLATION: utf8_bin;
echo.
echo >mysqlRecreateConfig.sql alter database %fedora_db_name% default character set utf8;
echo >>mysqlRecreateConfig.sql alter database %fedora_db_name% default collate utf8_bin;
echo >>mysqlRecreateConfig.sql show create database %fedora_db_name%;

echo.
rem ----- Set default character set and collation for Fedora database
mysql -u %mysql_dba_user% -p%mysql_dba_pass% -h localhost <mysqlRecreateConfig.sql


:mysqlOld
echo.
echo Fedora database re-initialization complete!
echo.
goto end


:showUsage
echo.
echo Usage: mysql-drop-db.bat mysqlHome mysqlDBAUser mysqlDBAPass fedoraDbName
echo.
echo mysqlHome - The location where mySQL is installed (use double quotes if path contains spaces, e.g. "C:\Program Files\MySQL\MySQL server 4.1")
echo mysqlDBAUser - mySQL username with DBA authority  (root if new install)
echo mysqlDBAPass - mySQL password for DBA username    (none if new install)
echo fedoraDbName - Fedora database name (use fedora20 as default)
echo.
goto end

:end