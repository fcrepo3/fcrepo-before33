#!/bin/sh

KOWARI_HOSTNAME=localhost
KOWARI_HTTP_PORT=8081
KOWARI_RMI_PORT=1099
KOWARI_HOME=$FEDORA_HOME/server/kowari
KOWARI_JAR=kowari-1.0.4.jar

if [ "$FEDORA_HOME" = "" ]; then
  echo "ERROR: Environment variable, FEDORA_HOME must be set."
  exit 1
fi

if [ "$FEDORA_JAVA_HOME" = "" ]; then
  if [ "$JAVA_HOME" = "" ]; then
    echo "ERROR: FEDORA_JAVA_HOME was not defined, nor was (the fallback) JAVA_HOME."
    exit 1
  fi 
  THIS_JAVA_HOME=$JAVA_HOME
else
  THIS_JAVA_HOME="$FEDORA_JAVA_HOME"
fi

if [ ! -f "$THIS_JAVA_HOME/bin/java" ]; then
  echo "ERROR: java was not found in $THIS_JAVA_HOME"
  echo "Make sure FEDORA_JAVA_HOME or JAVA_HOME is set correctly."
  exit 1
fi

if [ ! -f "$KOWARI_HOME/$KOWARI_JAR" ]; then
  echo "ERROR: No $KOWARI_JAR found in $KOWARI_HOME"
  exit 1
fi

echo "Starting Kowari server..."

OLD_JAVA_HOME=$JAVA_HOME
JAVA_HOME=$THIS_JAVA_HOME
export JAVA_HOME

(exec $JAVA_HOME/bin/java -jar $KOWARI_HOME/$KOWARI_JAR --serverhost $KOWARI_HOSTNAME --path $KOWARI_HOME --servername fedoraResourceIndex --port $KOWARI_HTTP_PORT --rmiport $KOWARI_RMI_PORT )

JAVA_HOME=$OLD_JAVA_HOME
export JAVA_HOME

exit 0
