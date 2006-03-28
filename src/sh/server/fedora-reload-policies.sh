#!/bin/sh
# ----------------------------------------------------------------------
# Fedora Reload Policies script
# ----------------------------------------------------------------------

# ----------------------------------------------------------------------
# Environment setup

# Cannot proceed if FEDORA_HOME is not set
if [ -z "$FEDORA_HOME" ]; then
  echo "ERROR: The FEDORA_HOME environment variable is not defined."
  exit 1
fi

if [ -r "$FEDORA_HOME"/server/bin/set-env.sh ]; then
  . "$FEDORA_HOME"/server/bin/set-env.sh
else
  echo "ERROR: $FEDORA_HOME/server/bin/set-env.sh was not found."
  exit 1
fi

TOMCAT_DIR=@tomcat.basename@
TC="$FEDORA_HOME"/server/"$TOMCAT_DIR"
TC_COMMON="$TC"/common/lib
SERVER_CONTROLLER_LIBS=@ServerController.unix.libs@

echo "Reloading Fedora policies..."

(exec "$JAVA" -cp "$TC"/webapps/fedora/WEB-INF/classes:"$TC"/webapps/fedora/WEB-INF/lib/commons-httpclient-2.0.1.jar:"$TC"/webapps/fedora/WEB-INF/lib/commons-logging.jar:"$SERVER_CONTROLLER_LIBS" -Dfedora.home="$FEDORA_HOME" fedora.server.utilities.ServerUtility reloadPolicies $1)
restoreJavaHome
