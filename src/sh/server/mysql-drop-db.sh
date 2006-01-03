#!/bin/sh
#
# Bourne shell script that may be useful for testing or when migrating from earlier
# releases and you need to wipe the Fedora relational database clean and start with an 
# empty Fedora database. The script is designed to remove the specified Fedora MySQL 
# database and then recreate a new database. 
#
#    NOTE: If using MySQL 4.1.x, you must also specify the default character
#    set for the Fedora database as "utf8" and the default collation as
#    "utf8_bin". To set these parameters with the script, you must
#    include the MySQL 4.1 flag which is the 5th optional argument. Any
#    value may be used since the script just checks to see if there are
#    six or seven arguments present.

#
# Usage: mysql-drop-db.sh mysql_home mysql_dba_user mysql_dba_pass fedora_db_name
#
#   mysql_home      - the path where MySQL is installed (e.g., /usr/local/mysql)
#   mysql_dba_user  - the name of the MySQL user with dba privileges (e.g.,  root)
#   mysql_dba_pass  - the password for the MySQL user with dba privileges
#   fedora_db_name  - the name of the Fedora database to be removed (e.g., fedora20)
#   mysql41_flag    - optional MySQL 4.1 flag. Any string value will indicate that
#                     you are running MySQL 4.1.x.
#

# check for four input arguments

if [ "$#" -lt 4 ] || [ "$#" -gt 5 ]; then
 echo "Usage: mysql-drop-db mysql_home mysql_dba_user mysql_dba_pass fedora_db_name mysql41_flag"
  echo "mysql_home      - the path where MySQL is installed (e.g., /usr/local/mysql)"
  echo "mysql_dba_user  - the name of the MySQL user with dba privileges (e.g.,  root)"
  echo "mysql_dba_pass  - the password for the MySQL user with dba privileges"
  echo "fedora_db_name  - the name of the Fedora database (e.g., fedora20)"
  echo "mysql41_flag    - optional MySQL 4.1 flag. Any string value will indicate that you are running MySQL 4.1.x"

  exit 1
elif [ "$#" = 4 ]; then
  mysql41_flag=""
else
  mysql41_flag=$5
fi

echo
echo "MySQL install directory: $1"
echo "MySQL dba username: $2"
echo "Mysql dba password: $3"
echo "Fedora database name:$4"
if [ "$mysql41_flag" = "" ]; then
  echo "MySQL 4.1 flag is: OFF"
else
  echo "MySQL 4.1 flag is: ON"
fi
echo

echo
echo "Dropping Fedora database: $4"
echo

# Drop Fedora database using specified database name

(exec $1/bin/mysqladmin -u $2 -p$3 -f -h localhost drop $4)

echo
echo "Recreating Fedora database: $4"
echo

# Create Fedora database using specified database name

(exec $1/bin/mysqladmin -u $2 -p$3 -h localhost create $4)


# Generate MySQL commands to set default character set and collation for Fedora database
# The commands are written to the file mysqlRecreateConfig.sql in the current directory.

if [ "$mysql41_flag" != "" ]; then
  echo
  echo "MySQL 4.1 flag is ON."
  echo "Adding default character set and collation changes for MySQL 4.1.x databases:"
  echo
  echo "Fedora database $4 DEFAULT CHARACTER SET: utf8"
  echo "Fedora database $4 DEFAULT COLLATION: utf8_bin"
  echo
  echo "#" >mysqlRecreateConfig.sql
  echo "# Adding default character set and collation changes for MySQL 4.1.x databases:" >>mysqlRecreateConfig.sql
  echo "#" >>mysqlRecreateConfig.sql
  echo "# Fedora database $4 DEFAULT CHARACTER SET: utf8" >>mysqlRecreateConfig.sql
  echo "# Fedora database $4 DEFAULT COLLATION: utf8_bin" >>mysqlRecreateConfig.sql
  echo "#" >>mysqlRecreateConfig.sql
  echo "alter database $4 default character set utf8;" >>mysqlRecreateConfig.sql
  echo "alter database $4 default collate utf8_bin;" >>mysqlRecreateConfig.sql
  echo "#" >>mysqlRecreateConfig.sql
  echo "# Show Fedora database character set" >>mysqlRecreateConfig.sql
  echo "show create database $4;" >>mysqlRecreateConfig.sql

  echo
  echo "Setting default charaset and collation for Fedora database"
  echo

  # Assign default character set and collation for Fedora database

  (exec $1/bin/mysql -u $2 -p$3 -h localhost -D mysql <mysqlRecreateConfig.sql)
  chmod 600 mysqlRecreateConfig.sql

  echo "Remove mysqlRecreateConfig.sql temp file? (y or n)"
  read ans
  if [ $ans = "y" ]; then
    rm mysqlRecreateConfig.sql
  fi
fi

echo
echo "Database initialization complete!"
echo

exit 0
