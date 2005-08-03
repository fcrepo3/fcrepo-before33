#!/bin/sh
# ----------------------------------------------------------------------
# Fedora Policy Editor script
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

echo Starting XACML policy editor...
(exec "$JAVA" -cp "$FEDORA_HOME"/server/utilities/PolicyEditor.jar -Dfedora.home="$FEDORA_HOME" fedora.utilities.policyEditor.PolicyEditor)
restoreJavaHome
