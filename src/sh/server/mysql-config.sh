#!/bin/sh
#
# Bourne shell script that will configure an exisiting Unix-based MySQL database 
# installation for use with Fedora. It assumes that MySQL has been successfully 
# installed and has an existing MySQL username with dba privileges. If this is a 
# new installation of MySQL, the default DBA username is usually "root" with no 
# password(use "" to indicate no password). If this is an existing MySQL installation, 
# use the appropriate username and password for the user that has DBA authority on 
# the MySQL installation or check with your MySQL database administrator to accomplish
# the tasks performed by this script. Fedora requires the following for a MySQL
# database:
#
# 1) An empty database. The name used must agree with the name used in the Fedora
#    fedora.fcfg configuratino file. The default name in the configuration file
#    is "fedora20". The database must initially be empty and then Fedora will
#    automatically generate the required tables.
# 2) A MySQL username and password with dba privileges on the Fedora database must 
#    exist for the specified database. The username and password must agree with
#    those in the Fedora fedora.fcfg file. The default username and password in the
#    configuration file are "fedoraAdmin" and "fedoraAdmin".
#
# 3) NOTE: If using MySQL 4.1.x, you must also specify the default character
#    set for the Fedora database as "utf8" and the default collation as
#    "utf8_bin". To set these parameters with the script, you must
#    include the MySQL 4.1 flag which is the 7th optional argument. Any
#    value may be used since the script just checks to see if there are
#    six or seven arguments present.
# 
# The script expects the following input arguments:
#
#
# arg 1 - the location where MySQL is installed (e.g., /usr/local/mysql)
# arg 2 - the name of the MySQL user with dba privileges (e.g., root)
# arg 3 - the password for the MySQL user with dba privileges (e.g., none)
# arg 4 - the name of MySQL user admin for Fedora database (e.g., fedoraAdmin)
# arg 5 - the password of MySQL user admin for Fedora database (e.g., fedoraAdmin)
# arg 6 - the name of the Fedora database (e.g., fedora20)
# arg 7 - optional MySQL 4.1 flag. Any string value will indicate that
#         you are running MySQL 4.1.x.
#
# check for six or seven input arguments
if [ "$#" -lt 6 ] || [ "$#" -gt 7 ]; then
  echo
  echo "Usage: mysqlConfig.sh mysql_home mysql_dba_user mysql_dba_pass fedora_dba_user fedora_dba_pass fedora_db_name"
  echo "mysql_home      - the path where MySQL is installed (e.g., /usr/local/mysql)"
  echo "mysql_dba_user  - the name of the MySQL user with dba privileges (e.g.,  root)"
  echo "mysql_dba_pass  - the password for the MySQL user with dba privileges"
  echo "fedoradba_user  - the name of MySQL user admin for Fedora database (e.g., fedoraAdmin)"
  echo "fedora_dba_pass - the password of MySQL user admin for Fedora database (e.g., fedoraAdmin)"
  echo "mysql_db_name   - the name of the Fedora database (e.g., fedora20)"
  echo "mysql41_flag    - optional MySQL 4.1 flag. Any string value will indicate that you are running MySQL 4.1.x"
  echo
  exit 1
elif [ "$#" = 6 ]; then
  mysql41_flag=""
else
  mysql41_flag=$7
fi

echo
echo "MySQL install directory: $1"
echo "MySQL dba username: $2"
echo "Mysql dba password: $3"
echo "Fedora dba username: $4"
echo "Fedora dba password: $5"
echo "Fedora database name:$6"
if [ "$mysql41_flag" = "" ]; then
  echo "MySQL 4.1 flag is: OFF"
else
  echo "MySQL 4.1 flag is: ON"
fi
echo

echo
echo "Creating Fedora database: $6"
echo

# Create Fedora database using specified database name

(exec $1/bin/mysqladmin -u $2 -p$3 -h localhost create $6)

# Generate MySQL commands to assign username and password to Fedora database
# The commands are written to the file mysqlConfig.sql in the current directory.

echo "#" >mysqlConfig.sql
echo "# Configure MySQL with the proper username and password for the Fedora database by" >>mysqlConfig.sql
echo "# 1\) adding a username of $4 with no global permissions" >>mysqlConfig.sql
echo "# 2\) assigning initial password of $5 for username $4" >>mysqlConfig.sql
echo "# 3\) granting the fedoraAdmin username DBA permissions on the Fedora Database named 'fedora20'" >>mysqlConfig.sql
echo "#" >>mysqlConfig.sql
echo "grant all privileges on $6.* to $4@localhost identified by '$5' with grant option;" >>mysqlConfig.sql
echo "grant all privileges on $6.* to $4@'%' identified by '$5' with grant option;" >>mysqlConfig.sql
echo "#" >>mysqlConfig.sql
echo "# Display results for verification" >>mysqlConfig.sql
echo "#" >>mysqlConfig.sql
echo "select * from user;" >>mysqlConfig.sql
echo "select * from db;" >>mysqlConfig.sql

if [ "$mysql41_flag" != "" ]; then
  echo
  echo "MySQL 4.1 flag is ON."
  echo "Adding default character set and collation changes for MySQL 4.1.x databases:"
  echo
  echo "Fedora database $6 DEFAULT CHARACTER SET: utf8"
  echo "Fedora database $6 DEFAULT COLLATION: utf8_bin"
  echo
  echo "#" >>mysqlConfig.sql
  echo "# Adding default character set and collation changes for MySQL 4.1.x databases:" >>mysqlConfig.sql
  echo "#" >>mysqlConfig.sql
  echo "# Fedora database $6 DEFAULT CHARACTER SET: utf8" >>mysqlConfig.sql
  echo "# Fedora database $6 DEFAULT COLLATION: utf8_bin" >>mysqlConfig.sql
  echo "#" >>mysqlConfig.sql
  echo "alter database $6 default character set utf8;" >>mysqlConfig.sql
  echo "alter database $6 default collate utf8_bin;" >>mysqlConfig.sql
  echo "#" >>mysqlConfig.sql
  echo "# Show Fedora database character set" >>mysqlConfig.sql
  echo "show create database $6;" >>mysqlConfig.sql
fi

echo
echo "Assigning username and passwords for Fedora database"
echo

# Assign specified username and password for Fedora database

(exec $1/bin/mysql -u $2 -p$3 -h localhost -D mysql <mysqlConfig.sql)

echo
echo "Database initialization complete!"
echo

chmod 600 mysqlConfig.sql

echo "Remove mysqlConfig.sql temp file? (y or n)"
read ans
if [ $ans = "y" ]; then
  rm mysqlConfig.sql
fi
exit 0
