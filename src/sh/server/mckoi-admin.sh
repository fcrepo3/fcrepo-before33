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

if [ $# -lt 2 ]; then
  echo "Usage: mckoi-admin adminUser adminPass"
  echo "Use the values you gave when running mckoi-init."
  exit 1
fi

echo "Launching McKoi SQL Interface... "

(exec java -cp $FEDORA_HOME/server/mckoi094/mckoidb.jar com.mckoi.tools.JDBCQueryTool -u "$1" -p "$2")

echo "Done."


exit 0
