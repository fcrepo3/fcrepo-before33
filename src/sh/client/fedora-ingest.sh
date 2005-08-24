#!/bin/sh
# ----------------------------------------------------------------------
# Fedora Server ingest script
# ----------------------------------------------------------------------

# ----------------------------------------------------------------------
# Environment setup

# Reset the input field separator to its default value
IFS=

# Reset the execution path 
PATH=/bin:/usr/bin:/usr/local/bin:/opt/bin

# Cannot proceed if FEDORA_HOME is not set
if [ -z "$FEDORA_HOME" ]; then
	echo "ERROR: The FEDORA_HOME environment variable is not defined."
	exit 1
fi

if [ -r "$FEDORA_HOME"/client/bin/set-env.sh ]; then
  	. "$FEDORA_HOME"/client/bin/set-env.sh
else
	echo "ERROR: $FEDORA_HOME/server/bin/set-env.sh was not found."
	exit 1
fi

echo "Starting Fedora Ingester..."

(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar \
              -Djavax.net.ssl.trustStore=$FEDORA_HOME/client/truststore \
              -Djavax.net.ssl.trustStorePassword=tomcat \
              -Dfedora.home=$FEDORA_HOME \
              -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl \
              -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl \
              fedora.client.utility.ingest.Ingest "$@")

restoreJavaHome

exit 0
