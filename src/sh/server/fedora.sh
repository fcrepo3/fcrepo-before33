#!/bin/sh
# ----------------------------------------------------------------------
# Fedora Server start/stop script
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

# ----------------------------------------------------------------------
# Functions

start() {
	echo "Starting the Fedora server..."
	if [ ! -d "$FEDORA_HOME/server/logs/" ]; then
		mkdir "$FEDORA_HOME"/server/logs
	fi
	
	(exec "$JAVA" -server \
	            -classpath "$TC"/webapps/fedora/WEB-INF/classes \
	            -Dfedora.home="$FEDORA_HOME" \
	            -Dtomcat.dir="$TC_BASENAME" \
	            fedora.server.BasicServer)

	SERVER_PROFILE=
	if [ ! -z "$1" ]; then
		SERVER_PROFILE="-Dfedora.serverProfile=$1"
	fi

	# start Tomcat
  	(exec nohup "$JAVA" -Xms64m -Xmx96m \
  					  -classpath "$TC"/bin/bootstrap.jar \
  					  -Djava.awt.fonts="$JAVA_HOME"/jre/lib/fonts \
  					  -Djava2d.font.usePlatformFont=false \
  					  -Djava.awt.headless=true \
  					  -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl \
  					  -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl \
  					  -Dfedora.home="$FEDORA_HOME" \
  					  "$SERVER_PROFILE" \
					  -Dclasspath=$TC/bin/bootstrap.jar \
					  -Djava.endorsed.dirs="$TC_ENDORSED" \
					  -Djava.security.manager \
					  -Djava.security.policy=="$TC"/conf/catalina.policy \
					  -Dcatalina.base="$TC" \
					  -Dcatalina.home="$TC" \
					  -Djava.io.tmpdir="$TC"/temp \
					  org.apache.catalina.startup.Bootstrap start &)
	
	echo "Deploying API-M and API-A..."
	(exec "$JAVA" -cp "$AXIS_UTILITY_LIBS":"$TC"/webapps/fedora/WEB-INF/classes \
	            -Dfedora.home="$FEDORA_HOME" \
	            -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl \
	            -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl \
	            fedora.server.utilities.AxisUtility deploy "$FEDORA_HOME"/server/config/deployAPI-A.wsdd 15)
	trap "Error deploying (see above)... to stop the server, use fedora-stop." 1 2 15
	
	(exec "$JAVA" -cp "$AXIS_UTILITY_LIBS":"$TC"/webapps/fedora/WEB-INF/classes \
	            -Dfedora.home="$FEDORA_HOME" \
	            -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl \
	            fedora.server.utilities.AxisUtility deploy "$FEDORA_HOME"/server/config/deploy.wsdd 15)
	trap "Error deploying (see above)... to stop the server, use fedora-stop." 1 2 15
	
	echo "Initializing Fedora Server instance..."
	(exec "$JAVA" -cp "$TC"/webapps/fedora/WEB-INF/classes:"$SERVER_CONTROLLER_LIBS" \
				-Dfedora.home="$FEDORA_HOME" \
				fedora.server.ServerController startup)
	restoreJavaHome
}

stop() {
	echo "Stopping the Fedora Server..."
	(exec "$JAVA" -cp "$TC"/webapps/fedora/WEB-INF/classes:"$SERVER_CONTROLLER_LIBS" \
	                          -Dfedora.home="$FEDORA_HOME" \
	                          fedora.server.ServerController shutdown)

	# Stop Tomcat
    (exec "$JAVA" -cp "$TC"/bin/bootstrap.jar \
                              -Dfedora.home="$FEDORA_HOME" \
                              -Dclasspath="$TC"/bin/bootstrap.jar \
                              -Djava.endorsed.dirs="$TC_ENDORSED" \
                              -Dcatalina.base="$TC" \
                              -Dcatalina.home="$TC" \
                              -Djava.io.tmpdir="$TC"/temp \
                              org.apache.catalina.startup.Bootstrap stop)
	restoreJavaHome
}

restart() {
	stop
	start "$@"
	restoreJavaHome
}

status() {
	echo "Sorry, this method is not yet supported."
}

# ----------------------------------------------------------------------
# 

case "$1" in
	start)
		shift
		start "$@"
		;;
	stop)
		shift
		stop "$@"
		;;
	restart)
		shift
		restart "$@"
		;;
	status)
		shift
		status "$@"
		;;
	*)
		echo "Usage: $0 {start|stop|restart|status}"
		;;
esac