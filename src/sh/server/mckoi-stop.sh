#!/bin/sh

if [ "$FEDORA_HOME" = "" ]; then
  echo "ERROR: Environment variable, FEDORA_HOME must be set."
  exit 1
fi

if [ ! -f "$JAVA_HOME/bin/java" ]; then
  echo "ERROR: java was not found in $JAVA_HOME"
  echo "Make sure JAVA_HOME is set correctly."
  exit 1
fi

if [ ! -f "$FEDORA_HOME/mckoi094/mckoidb.jar" ]; then
  echo "ERROR: No mckoidb.jar found in $FEDORA_HOME/mckoi094/"
  exit 1
fi

if [ ! -f "$FEDORA_HOME/mckoi094/data/DefaultDatabase.sf" ]; then 
  echo "ERROR: McKoi database hasn't been initialized, run mckoi-init first."
  exit 1
fi

if [ $# -lt 2 ]; then
  echo "Usage: mckoi-stop adminUser adminPass"
  echo "Use the same user/pass values used when running mckoi-init"
  exit 1
fi

(exec $JAVA_HOME/bin/java -cp $FEDORA_HOME/mckoi094/gnu-regexp-1.1.4.jar -jar $FEDORA_HOME/mckoi094/mckoidb.jar -conf $FEDORA_HOME/mckoi094/db.conf -shutdown "$1" "$2")

echo "Finished."

echo "Stopping McKoi DB..."


exit 0
