#!/bin/sh

if [ "$FEDORA_HOME" = "" ]; then
  echo "ERROR: Environment variable FEDORA_HOME must be set."
  exit 1
fi

(exec java -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME fedora.client.purge.AutoPurger $1 $2 $3 $4 $5 $6)

exit 0
