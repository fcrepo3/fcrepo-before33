#!/bin/sh

if [ "$FEDORA_HOME" = "" ]; then
  echo "ERROR: Environment variable, FEDORA_HOME must be set."
  exit 1
fi

if [ ! -f "$FEDORA_HOME/client/client.jar" ]; then
  echo "ERROR: FEDORA_HOME does not appear correctly set."
  echo "Client cannot be found at $FEDORA_HOME/client/client.jar"
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

echo "Starting Fedora DemoPurger..."

OLD_JAVA_HOME=$JAVA_HOME
JAVA_HOME=$THIS_JAVA_HOME
export JAVA_HOME

echo "Purging Demonstration Objects (27 total)... "

echo "Purging data objects (11 data objects)
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client;$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME fedora.client.purge.AutoPurger %1 %2 %3 %4 demo:5  "Deleted by fedora-purge-demos script"
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client;$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME fedora.client.purge.AutoPurger %1 %2 %3 %4 demo:6  "Deleted by fedora-purge-demos script"
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client;$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME fedora.client.purge.AutoPurger %1 %2 %3 %4 demo:7  "Deleted by fedora-purge-demos script"
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client;$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME fedora.client.purge.AutoPurger %1 %2 %3 %4 demo:10 "Deleted by fedora-purge-demos script"
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client;$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME fedora.client.purge.AutoPurger %1 %2 %3 %4 demo:11 "Deleted by fedora-purge-demos script"
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client;$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME fedora.client.purge.AutoPurger %1 %2 %3 %4 demo:14 "Deleted by fedora-purge-demos script"
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client;$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME fedora.client.purge.AutoPurger %1 %2 %3 %4 demo:17 "Deleted by fedora-purge-demos script"
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client;$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME fedora.client.purge.AutoPurger %1 %2 %3 %4 demo:18 "Deleted by fedora-purge-demos script"
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client;$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME fedora.client.purge.AutoPurger %1 %2 %3 %4 demo:21 "Deleted by fedora-purge-demos script"
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client;$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME fedora.client.purge.AutoPurger %1 %2 %3 %4 demo:26 "Deleted by fedora-purge-demos script"
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client;$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME fedora.client.purge.AutoPurger %1 %2 %3 %4 demo:29 "Deleted by fedora-purge-demos script"

echo "Purging bMechs (9 bmech objects)"
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client;$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME fedora.client.purge.AutoPurger %1 %2 %3 %4 demo:2  "Deleted by fedora-purge-demos script"
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client;$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME fedora.client.purge.AutoPurger %1 %2 %3 %4 demo:3  "Deleted by fedora-purge-demos script"
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client;$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME fedora.client.purge.AutoPurger %1 %2 %3 %4 demo:4  "Deleted by fedora-purge-demos script"
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client;$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME fedora.client.purge.AutoPurger %1 %2 %3 %4 demo:9  "Deleted by fedora-purge-demos script"
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client;$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME fedora.client.purge.AutoPurger %1 %2 %3 %4 demo:13 "Deleted by fedora-purge-demos script"
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client;$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME fedora.client.purge.AutoPurger %1 %2 %3 %4 demo:16 "Deleted by fedora-purge-demos script"
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client;$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME fedora.client.purge.AutoPurger %1 %2 %3 %4 demo:20 "Deleted by fedora-purge-demos script"
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client;$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME fedora.client.purge.AutoPurger %1 %2 %3 %4 demo:25 "Deleted by fedora-purge-demos script"
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client;$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME fedora.client.purge.AutoPurger %1 %2 %3 %4 demo:28 "Deleted by fedora-purge-demos script"

echo "Purging bDefs (7 bdef objects)"
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client;$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME fedora.client.purge.AutoPurger %1 %2 %3 %4 demo:1  "Deleted by fedora-purge-demos script"
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client;$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME fedora.client.purge.AutoPurger %1 %2 %3 %4 demo:8  "Deleted by fedora-purge-demos script"
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client;$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME fedora.client.purge.AutoPurger %1 %2 %3 %4 demo:12 "Deleted by fedora-purge-demos script"
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client;$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME fedora.client.purge.AutoPurger %1 %2 %3 %4 demo:15 "Deleted by fedora-purge-demos script"
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client;$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME fedora.client.purge.AutoPurger %1 %2 %3 %4 demo:19 "Deleted by fedora-purge-demos script"
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client;$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME fedora.client.purge.AutoPurger %1 %2 %3 %4 demo:22 "Deleted by fedora-purge-demos script"
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client;$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME fedora.client.purge.AutoPurger %1 %2 %3 %4 demo:27 "Deleted by fedora-purge-demos script"

echo Finished Purging Demonstration objects.

JAVA_HOME=$OLD_JAVA_HOME
export JAVA_HOME

exit 0
