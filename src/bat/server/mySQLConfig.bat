@echo off
cls
rem
rem ----- MS-DOS batch script that will configure an exisiting Windows-based MySQL
rem ----- database installation for use with Fedora. It assumes that MySQL has been 
rem ----- successfully installed and has a MySQL username with dba privileges. The
rem ----- following environment variables should be set to the appropriate values
rem ----- desired for this installation.
rem
rem ----- mysql_dba_user  - the name of the MySQL user with dba privileges (default: root)
rem ----- mysql_dba_pass  - the password for the MySQL user with dba privileges (default" none)
rem ----- mysql_db_name   - the name of the Fedora database (default: FedoraObjects)
rem ----- fedoradba_user  - the name of MySQL user admin for Fedora database (default: fedoraAdmin)
rem ----- fedora_dba_pass - the password of MySQL user admin for Fedora database (default: fedoraAdmin)
rem ----- mysql_home      - the location where MySQL is installed
rem

rem ***********************************************
rem ***** BEGIN OF USER-CONFIGURABLE SETTINGS *****
rem ***********************************************

set fedora_db_name=FedoraObjects
echo Fedora database name: %fedora_db_name%

set mysql_dba_user=root
echo MySQL dba username: %mysql_dba_user%

set mysql_dba_pass=
echo Mysql dba password: %mysql_dba_password%

set fedora_dba_user=fedoraAdmin
echo Fedora dba username: %fedora_dba_user%

set fedora_dba_pass=fedoraAdmin
echo Fedora dba password: %fedora_dba_pass%

set mysql_home=c:\mysql
echo MySQL install directory: %mysql_home%

rem **********************************************
rem ***** END OF USER-CONFIGURABLE PARAMETERS *****
rem **********************************************

set path=%path%;%mysql_home%\bin
rem -- Create Fedora database using specified database name
mysqladmin -u %mysql_dba_user% -p%mysql_dba_pass% -h localhost create %fedora_db_name%

rem ----- Generate MySQL commands to assign username and password to Fedora database
echo >mysqlConfig.sql #
echo >>mysqlConfig.sql # Configure MySQL with the proper username and password for the Fedora database by 
echo >>mysqlConfig.sql # 1) adding a username of %fedora_dba_user% with no global permissions
echo >>mysqlConfig.sql # 2) assigning initial password of %fedora_dba_pass% for username %fedora_dba_user%
echo >>mysqlConfig.sql # 3) granting the fedoraAdmin username DBA permissions on the Fedora Database named 'FedoraObjects'
echo >>mysqlConfig.sql #
echo >>mysqlConfig.sql grant all privileges on %fedora_db_name%.* to %fedora_dba_user%@localhost identified by '%fedora_dba_pass%' with grant option;
echo >>mysqlConfig.sql grant all privileges on %fedora_db_name%.* to %fedora_dba_user%@'%%' identified by '%fedora_dba_pass%' with grant option;
echo >>mysqlConfig.sql #
echo >>mysqlConfig.sql # Display results for verification
echo >>mysqlConfig.sql #
echo >>mysqlConfig.sql select * from user;
echo >>mysqlConfig.sql select * from db;

rem ----- Assign specified username and password for Fedora database
mysql -u %mysql_dba_user% -p%mysql_dba_pass% -h localhost -D mysql <mysqlConfig.sql