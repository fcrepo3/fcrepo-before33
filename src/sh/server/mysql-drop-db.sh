#!/bin/sh
#
# Bourne shell script that may be useful for testing or when migrating from earlier
# releases and you need to wipe the Fedora relational database clean and start with an 
# empty Fedora database. The script is designed to remove the specified Fedora MySQL 
# database and then recreate a new database. 
#
# Usage: mysql-drop-db.sh mysql_home mysql_dba_user mysql_dba_pass fedora_db_name
#
#   mysql_home      - the path where MySQL is installed (e.g., /usr/local/mysql)
#   mysql_dba_user  - the name of the MySQL user with dba privileges (e.g.,  root)
#   mysql_dba_pass  - the password for the MySQL user with dba privileges
#   fedora_db_name  - the name of the Fedora database to be removed (e.g., FedoraObjects)
#

# check for four input arguments

if [ "$#" != 4 ]; then
 echo "Usage: mysqlConfig.sh mysql_home mysql_dba_user mysql_dba_pass fedora_dba_user fedora_dba_pass fedora_db_name"
  echo "mysql_home      - the path where MySQL is installed (e.g., /usr/local/mysql)"
  echo "mysql_dba_user  - the name of the MySQL user with dba privileges (e.g.,  root)"
  echo "mysql_dba_pass  - the password for the MySQL user with dba privileges"
  echo "fedora_dba_pass - the password of MySQL user admin for Fedora database (default: fedoraAdmin)"
  echo "fedora_db_name  - the name of the Fedora database (e.g., FedoraObjects)"
  exit 1
fi

echo
echo "MySQL install directory: $1"
echo "MySQL dba username: $2"
echo "Mysql dba password: $3"
echo "Fedora database name:$4"
echo

echo
echo "Dropping Fedora database: $4"
echo

# Drop Fedora database using specified database name

(exec $1/bin/mysqladmin -u $2 -p$3 -h localhost drop $4)

echo
echo "Recreating Fedora database: $4"
echo

# Create Fedora database using specified database name

(exec $1/bin/mysqladmin -u $2 -p$3 -h localhost create $4)

exit 0
