#!/bin/sh
# ----------------------------------------------------------------------
# Fedora Rebuild script
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

TC_BASENAME="@tomcat.basename@"
TC="$FEDORA_HOME"/server/"$TC_BASENAME"
TC_COMMON="$TC"/common/lib
TC_ENDORSED="$TC"/common/endorsed
AXIS_UTILITY_LIBS=@AxisUtility.unix.libs@
SERVER_CONTROLLER_LIBS=@ServerController.unix.libs@

SERVER_PROFILE="$1"
(exec "$JAVA" -server -Xmn64m -Xms256m -Xmx256m \
	    	  -cp "$TC/webapps/fedora/WEB-INF/classes" \
  			  -Dfedora.home="$FEDORA_HOME" \
          -Dfedora.serverProfile=$SERVER_PROFILE \
  			  -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl \
  			  -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl \
			  -Djava.endorsed.dirs="$TC_COMMON:$TC/webapps/fedora/WEB-INF/lib" \
			  -Djava.io.tmpdir="$TC/temp" \
              fedora.server.utilities.rebuild.Rebuild $SERVER_PROFILE)
restoreJavaHome
