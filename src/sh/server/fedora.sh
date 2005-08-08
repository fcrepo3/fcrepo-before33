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
	SERVER_PROFILE="$1"

	echo "Starting the Fedora server..."
	if [ ! -d "$FEDORA_HOME/server/logs/" ]; then
		mkdir "$FEDORA_HOME"/server/logs
	fi
	
	(exec "$JAVA" -classpath "$TC"/webapps/fedora/WEB-INF/classes \
	            -Dfedora.home="$FEDORA_HOME" \
	            -Dtomcat.dir="$TC_BASENAME" \
	            fedora.server.BasicServer)

	# start Tomcat
	if [ -z "${SERVER_PROFILE}" ]; then
		echo "Using the default server profile"
	else
		echo "Using server profile: $SERVER_PROFILE"
	fi
	
  	(exec nohup "$JAVA" -server -Xmn32m -Xms128m -Xmx128m \
  					  -classpath "$TC"/bin/bootstrap.jar \
  					  -Djava.awt.fonts="$JAVA_HOME"/jre/lib/fonts \
  					  -Djava2d.font.usePlatformFont=false \
  					  -Djava.awt.headless=true \
  					  -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl \
  					  -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl \
  					  -Dfedora.home="$FEDORA_HOME" \
  					  -Dfedora.serverProfile=$SERVER_PROFILE \
					  -Dclasspath=$TC/bin/bootstrap.jar \
					  -Djava.endorsed.dirs="$TC_ENDORSED" \
					  -Djava.security.manager \
					  -Djava.security.policy="$TC"/conf/catalina.policy \
					  -Dcatalina.base="$TC" \
					  -Dcatalina.home="$TC" \
					  -Djava.io.tmpdir="$TC"/temp \
                      -Djava.security.auth.login.config="$TC"/conf/jaas.config \
                      -Djava.util.logging.config.file="$FEDORA_HOME"/server/config/logging.properties\
					  org.apache.catalina.startup.Bootstrap start &)
	
	echo "Deploying API-M and API-A..."
	(exec "$JAVA" -cp "$AXIS_UTILITY_LIBS":"$TC"/webapps/fedora/WEB-INF/classes:"$TC"/webapps/fedora/WEB-INF/lib/commons-httpclient-2.0.1.jar:"$TC"/webapps/fedora/WEB-INF/lib/commons-logging.jar \
	            -Dfedora.home="$FEDORA_HOME" \
	            -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl \
	            -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl \
	            fedora.server.utilities.AxisUtility deploy "$FEDORA_HOME"/server/config/deployAPI-A.wsdd 15 "")
	trap "Error deploying (see above)... to stop the server, use fedora-stop." 1 2 15
	
	(exec "$JAVA" -cp "$AXIS_UTILITY_LIBS":"$TC"/webapps/fedora/WEB-INF/classes:"$TC"/webapps/fedora/WEB-INF/lib/commons-httpclient-2.0.1.jar:"$TC"/webapps/fedora/WEB-INF/lib/commons-logging.jar \
	            -Dfedora.home="$FEDORA_HOME" \
	            -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl \
	            fedora.server.utilities.AxisUtility deploy "$FEDORA_HOME"/server/config/deploy.wsdd 15 "")
	trap "Error deploying (see above)... to stop the server, use fedora-stop." 1 2 15
	
	echo "Initializing Fedora Server instance..."
	(exec "$JAVA" -cp "$TC"/webapps/fedora/WEB-INF/classes:"$TC"/webapps/fedora/WEB-INF/lib/commons-httpclient-2.0.1.jar:"$TC"/webapps/fedora/WEB-INF/lib/commons-logging.jar:"$SERVER_CONTROLLER_LIBS" \
				-Dfedora.home="$FEDORA_HOME" \
				fedora.server.utilities.ServerUtility startup)
	restoreJavaHome
}

debug() {
	SERVER_PROFILE="$3"

	echo "Starting the Fedora server..."
	if [ ! -d "$FEDORA_HOME/server/logs/" ]; then
		mkdir "$FEDORA_HOME"/server/logs
	fi
	
	(exec "$JAVA" -classpath "$TC"/webapps/fedora/WEB-INF/classes \
	            -Dfedora.home="$FEDORA_HOME" \
	            -Dtomcat.dir="$TC_BASENAME" \
	            fedora.server.BasicServer)

	# start Tomcat
	if [ -z "${SERVER_PROFILE}" ]; then
		echo "Using the default server profile"
	else
		echo "Using server profile: $SERVER_PROFILE"
	fi
	
  	(exec nohup "$JAVA" -server -Xmn32m -Xms128m -Xmx128m \
                      -Xnoagent -Xdebug -Djava.compiler=none \
                      -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n \
  					  -classpath "$TC"/bin/bootstrap.jar \
  					  -Djava.awt.fonts="$JAVA_HOME"/jre/lib/fonts \
  					  -Djava2d.font.usePlatformFont=false \
  					  -Djava.awt.headless=true \
  					  -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl \
  					  -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl \
  					  -Dfedora.home="$FEDORA_HOME" \
  					  -Dfedora.serverProfile=$SERVER_PROFILE \
					  -Dclasspath=$TC/bin/bootstrap.jar \
					  -Djava.endorsed.dirs="$TC_ENDORSED" \
					  -Djava.security.manager \
					  -Djava.security.policy="$TC"/conf/catalina.policy \
					  -Dcatalina.base="$TC" \
					  -Dcatalina.home="$TC" \
					  -Djava.io.tmpdir="$TC"/temp \
                      -Djava.security.auth.login.config="$TC"/conf/jaas.config \
                      -Djava.util.logging.config.file="$FEDORA_HOME"/server/config/logging.properties\
					  org.apache.catalina.startup.Bootstrap start &)
	
	echo "Deploying API-M and API-A..."
	(exec "$JAVA" -cp "$AXIS_UTILITY_LIBS":"$TC"/webapps/fedora/WEB-INF/classes \
	            -Dfedora.home="$FEDORA_HOME" \
	            -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl \
	            -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl \
	            fedora.server.utilities.AxisUtility deploy "$FEDORA_HOME"/server/config/deployAPI-A.wsdd 15 "")
	trap "Error deploying (see above)... to stop the server, use fedora-stop." 1 2 15
	
	(exec "$JAVA" -cp "$AXIS_UTILITY_LIBS":"$TC"/webapps/fedora/WEB-INF/classes \
	            -Dfedora.home="$FEDORA_HOME" \
	            -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl \
	            fedora.server.utilities.AxisUtility deploy "$FEDORA_HOME"/server/config/deploy.wsdd 15 "")
	trap "Error deploying (see above)... to stop the server, use fedora-stop." 1 2 15
	
	echo "Initializing Fedora Server instance..."
	(exec "$JAVA" -cp "$TC"/webapps/fedora/WEB-INF/classes:"$TC"/webapps/fedora/WEB-INF/lib/commons-httpclient-2.0.1.jar:"$TC"/webapps/fedora/WEB-INF/lib/commons-logging.jar:"$SERVER_CONTROLLER_LIBS" \
				-Dfedora.home="$FEDORA_HOME" \
				fedora.server.utilities.ServerUtility startup)
    echo "Starting jdb..."
    (exec "$JAVA_HOME/bin/jdb" -connect com.sun.jdi.SocketAttach:hostname=localhost,port=8000)
	restoreJavaHome
}

stop() {
	echo "Stopping the Fedora Server..."
	(exec "$JAVA" -cp "$TC"/webapps/fedora/WEB-INF/classes:"$TC"/webapps/fedora/WEB-INF/lib/commons-httpclient-2.0.1.jar:"$TC"/webapps/fedora/WEB-INF/lib/commons-logging.jar:"$SERVER_CONTROLLER_LIBS" \
	                          -Dfedora.home="$FEDORA_HOME" \
	                          fedora.server.utilities.ServerUtility shutdown)

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
	debug)
		shift
		debug "$@"
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
