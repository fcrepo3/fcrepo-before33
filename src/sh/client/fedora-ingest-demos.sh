#!/bin/sh
# ----------------------------------------------------------------------
# Fedora Server ingest-demos script
# ----------------------------------------------------------------------

# Cannot proceed if FEDORA_HOME is not set
if [ -z "$FEDORA_HOME" ]; then
	echo "ERROR: The FEDORA_HOME environment variable is not defined."
	exit 1
fi

if [ -r "$FEDORA_HOME"/client/bin/set-env.sh ]; then
  	. "$FEDORA_HOME"/client/bin/set-env.sh
else
	echo "ERROR: $FEDORA_HOME/client/bin/set-env.sh was not found."
	exit 1
fi

# Validate number of arguments
if [ $# -lt 4 ]; then
	echo "Usage: fedora-ingest-demos <hostname> <port> <username> <password>"
	echo "Use the values in fedora.fcfg, e.g.:"
	echo "    fedora-ingest-demos localhost 8080 fedoraAdmin fedoraAdmin"
	exit 1
fi

echo "Starting Fedora DemoIngester..."

echo "Ingesting Demonstration Objects..."

DO_FORMAT=foxml1.0

(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.utility.ingest.Ingest d $FEDORA_HOME/client/demo/foxml/local-server-demos $DO_FORMAT DMO $1:$2 $3 $4)
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.utility.ingest.Ingest d $FEDORA_HOME/client/demo/foxml/open-server-demos $DO_FORMAT DMO $1:$2 $3 $4)

echo "Finished."

JAVA_HOME=$OLD_JAVA_HOME
export JAVA_HOME

exit 0
