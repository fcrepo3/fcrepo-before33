#!/bin/sh

if [ "$FEDORA_HOME" = "" ]; then
  echo "ERROR: Environment variable, FEDORA_HOME must be set."
  exit 1
fi

if [ ! -f "$FEDORA_HOME/client/client.jar" ]; then
  echo "ERROR: FEDORA_HOME does not appear correctly set."
  echo "Client directory cannot be found at $FEDORA_HOME/client"
  exit 1
fi

if [ "$FEDORA_JAVA_HOME" = "" ]; then

  if [ "$JAVA_HOME" = "" ]; then
    echo "ERROR: FEDORA_JAVA_HOME was not defined, nor was (the fallback) JAVA_HOME."
    exit 1
  else
    THIS_JAVA_HOME=$JAVA_HOME
  fi
else
  THIS_JAVA_HOME=$FEDORA_JAVA_HOME
fi

if [ ! -f "$THIS_JAVA_HOME/bin/java" ]; then
  echo "ERROR: java was not found in $THIS_JAVA_HOME"
  echo "Make sure FEDORA_JAVA_HOME or JAVA_HOME is set correctly."
  exit 1
fi

if [ ! -f "$THIS_JAVA_HOME/bin/orbd" ]; then 
  echo "ERROR: java was found in $THIS_JAVA_HOME, but it was not version 1.4"
  echo "Make sure FEDORA_JAVA_HOME or JAVA_HOME points to a 1.4JRE/JDK base."
  exit 1
fi

echo "Converting Demo Objects..."

OLD_JAVA_HOME=$JAVA_HOME
JAVA_HOME=$THIS_JAVA_HOME
export JAVA_HOME

(exec $JAVA_HOME/bin/java -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar fedora.client.demo.DemoObjectConverter $1 $2 $3 $4 $5)

echo ""
echo "Finished Converting Demo Objects"

JAVA_HOME=$OLD_JAVA_HOME
export JAVA_HOME

exit 0
