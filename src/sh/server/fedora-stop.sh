#!/bin/sh

if [ "$FEDORA_HOME" = "" ]; then
  echo "ERROR: Environment variable, FEDORA_HOME must be set."
  exit 1
fi

if [ ! -f "$FEDORA_HOME/server/config/fedora.fcfg" ]; then
  echo "ERROR: FEDORA_HOME does not appear correctly set."
  echo "Configuration cannot be found at $FEDORA_HOME/server/config/fedora.fcfg"
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
  echo "ERROR: java.exe was not found in $THIS_JAVA_HOME"
  echo "Make sure FEDORA_JAVA_HOME or JAVA_HOME is set correctly."
  exit 1
fi

if [ ! -f "$THIS_JAVA_HOME/bin/orbd" ]; then
  echo "ERROR: java was found in $THIS_JAVA_HOME, but it was not version 1.4"
  echo "Make sure FEDORA_JAVA_HOME or JAVA_HOME points to a 1.4JRE/JDK base."
  exit 1
fi

TC=$FEDORA_HOME/server/tomcat41
export TC
OLD_JAVA_HOME=$JAVA_HOME
JAVA_HOME=$THIS_JAVA_HOME
export JAVA_HOME

C=$TC/common/lib
CP=$C/saaj.jar:$C/commons-discovery.jar:$C/axis.jar:$C/commons-logging.jar:$C/jaxrpc.jar:$C/wsdl4j.jar:$C/tt-bytecode.jar

echo "Shutting down Fedora Server and Modules..."
(exec $JAVA_HOME/bin/java -cp $TC/webapps/fedora/WEB-INF/classes:$TC/common/lib/servlet.jar -Dfedora.home=$FEDORA_HOME fedora.server.ServerController shutdown)

echo "Shutting down Fedora Service..."

(exec $JAVA_HOME/bin/java -cp $TC/bin/bootstrap.jar -Dfedora.home=$FEDORA_HOME -Dclasspath=$TC/bin/bootstrap.jar -Djava.endorsed.dirs=$TC/bin -Djava.security.manager -Djava.security.policy=$TC/conf/catalina.policy -Dcatalina.base=$TC -Dcatalina.home=$TC -Djava.io.tmpdir=$TC/temp org.apache.catalina.startup.Bootstrap stop)

JAVA_HOME=$OLD_JAVA_HOME
export JAVA_HOME

exit 0
