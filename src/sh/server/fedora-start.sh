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
  echo "ERROR: java was not found in $THIS_JAVA_HOME"
  echo "Make sure FEDORA_JAVA_HOME or JAVA_HOME is set correctly."
  exit 1
fi

if [ ! -f "$THIS_JAVA_HOME/bin/orbd" ]; then 
  echo "ERROR: java was found in $THIS_JAVA_HOME, but it was not version 1.4"
  echo "Make sure FEDORA_JAVA_HOME or JAVA_HOME points to a 1.4JRE/JDK base."
  exit 1
fi

echo "Starting Fedora server..."

TC=$FEDORA_HOME/server/tomcat41
export TC
OLD_JAVA_HOME=$JAVA_HOME
JAVA_HOME=$THIS_JAVA_HOME
export JAVA_HOME

if [ -f "$FEDORA_HOME/server/logs/startup.log" ]; then
  mkdir $FEDORA_HOME/server/logs
fi

(exec $JAVA_HOME/bin/java -cp $TC/webapps/fedora/WEB-INF/classes -Dfedora.home=$FEDORA_HOME fedora.server.BasicServer)
if [ "$1" = "" ]; then
  (exec  nohup $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $TC/bin/bootstrap.jar -Djava.awt.fonts=$JAVA_HOME/jre/lib/fonts -Djava2d.font.usePlatformFont=false -Djava.awt.headless=true -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl -Dfedora.home=$FEDORA_HOME -Dclasspath=$TC/bin/bootstrap.jar -Djava.endorsed.dirs=$TC/bin -Dcatalina.base=$TC -Dcatalina.home=$TC -Djava.io.tmpdir=$TC/temp org.apache.catalina.startup.Bootstrap start &)
else
  (exec  nohup $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $TC/bin/bootstrap.jar -Djava.awt.fonts=$JAVA_HOME/jre/lib/fonts -Djava2d.font.usePlatformFont=false -Djava.awt.headless=true -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl -Dfedora.home=$FEDORA_HOME -Dfedora.serverProfile=$1 -Dclasspath=$TC/bin/bootstrap.jar -Djava.endorsed.dirs=$TC/bin -Dcatalina.base=$TC -Dcatalina.home=$TC -Djava.io.tmpdir=$TC/temp org.apache.catalina.startup.Bootstrap start &)
fi

C=$TC/common/lib
export C
CP=$C/xerces2-2.0.2.jar:$C/saaj.jar:$C/commons-discovery.jar:$C/axis.jar:$C/commons-logging.jar:$C/jaxrpc.jar:$C/wsdl4j.jar:$C/tt-bytecode.jar
export CP

echo "Deploying API-M and API-A..."

(exec $JAVA_HOME/bin/java -cp $CP:$TC/webapps/fedora/WEB-INF/classes -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.server.utilities.AxisUtility deploy $FEDORA_HOME/server/config/deployAPI-A.wsdd 15)

trap "Error deploying (see above)... to stop the server, use fedora-stop." 1 2 15

(exec $JAVA_HOME/bin/java -cp $CP:$TC/webapps/fedora/WEB-INF/classes -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.server.utilities.AxisUtility deploy $FEDORA_HOME/server/config/deploy.wsdd 15)

trap "Error deploying (see above)... to stop the server, use fedora-stop." 1 2 15

echo "Initializing Fedora Server instance..."
(exec $JAVA_HOME/bin/java -cp $TC/webapps/fedora/WEB-INF/classes:$TC/common/lib/servlet.jar -Dfedora.home=$FEDORA_HOME fedora.server.ServerController startup)

echo "Finished.  To stop the server, use fedora-stop."

JAVA_HOME=$OLD_JAVA_HOME
export JAVA_HOME

exit 0
