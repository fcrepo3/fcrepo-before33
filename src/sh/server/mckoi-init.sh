#!/bin/sh

if [ "$FEDORA_HOME" = "" ]; then
  echo "ERROR: Environment variable, FEDORA_HOME must be set."
  exit 1
fi

if [ ! -f "$FEDORA_HOME/server/mckoi094/mckoidb.jar" ]; then
  echo "ERROR: No mckoidb.jar found in $FEDORA_HOME/server/mckoi094/"
  echo "Make sure FEDORA_HOME is set correctly."
  exit 1
fi

if [ -f "$FEDORA_HOME/server/mckoi094/data/DefaultDatabase.sf" ]; then
  echo "ERROR: McKoi database already initialized.  "
  echo "Remove $FEDORA_HOME/server/mckoi094/data to delete."
  exit 1
fi

if [ $# -lt 2 ]; then
  echo "Usage: mckoi-init adminUser adminPass"
  echo "Use your own user and password values, and remember them for later."
  exit 1
fi

echo "Initializing McKoi DB... "

(exec java -cp $FEDORA_HOME/server/mckoi094/gnu-regexp-1.1.4.jar -jar $FEDORA_HOME/server/mckoi094/mckoidb.jar -conf $FEDORA_HOME/server/mckoi094/db.conf -create "$1" "$2")

echo "Finished."


exit 0
