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
SERVER_LIBS=@Server.unix.libs@
SERVER_CONTROLLER_LIBS=@ServerController.unix.libs@

# Before starting server, check if configuration files exist.
# If not, lof which files are missing and inform user
# to run fedora-setup to initially configure the server.

notConfiguredFlag="false"
if [ ! -r "$FEDORA_HOME/server/config/fedora.fcfg" ]; then
    echo
    echo "ERROR: Unable to find file: $FEDORA_HOME/server/config/fedora.fcfg."
    echo
    notConfiguredFlag="true"
fi

if [ ! -r "$FEDORA_HOME/server/config/beSecurity.xml" ]; then
    echo
    echo "ERROR: Unable to find file: $FEDORA_HOME/server/config/beSecurity.xml."
    echo
    notConfiguredFlag="true"
fi

if [ ! -r "$TC/webapps/fedora/WEB-INF/web.xml" ]; then
    echo
    echo "ERROR: Unable to find file: $TC/webapps/fedora/WEB-INF/web.xml."
    echo
    notConfiguredFlag="true"
fi

if [ "$notConfiguredFlag" = "true" ]; then
    echo
    echo       "It appears that the server has not been initially configured."
    echo       "Run the fedora-setup.bat script to initially configure the server"
    echo       "with the desired configuration. e.g.,"
    echo       
    echo "    Usage: fedora-setup [configuration-name]"
    echo
    echo "    where [configuration-name] must be one of the following:"
    echo
    echo "        ssl-authenticate-apim - API-M with basicAuth and SSL"
    echo "                              - API-A with no basicAuth and no SSL"
    echo
    echo "        ssl-authenticate-all  - API-M with basicAuth and SSL"
    echo "                              - API-A with basicAuth and SSL"
    echo
    echo "        no-ssl-authenticate-apim - API-M with basicAuth but no SSL"
    echo "                                 - API-A with no basicAuth and no SSL"
    echo
    echo "        no-ssl-authenticate-all  - API-M with basicAuth but no SSL"
    echo "                                 - API-A with basicAuth but no SSL"
    echo	
    exit 1
fi



# ----------------------------------------------------------------------
# Functions

start() {
	SERVER_PROFILE="$1"

	echo "Starting the Fedora server..."
	if [ ! -d "$FEDORA_HOME/server/logs/" ]; then
		mkdir "$FEDORA_HOME"/server/logs
	fi
	
	(exec "$JAVA" -classpath "$TC"/webapps/fedora/WEB-INF/classes:"$SERVER_LIBS" \
		-Djavax.net.ssl.trustStore="$FEDORA_HOME"/server/truststore \
		-Djavax.net.ssl.trustStorePassword=tomcat \
		-Dfedora.home="$FEDORA_HOME" \
		-Dtomcat.dir="$TC_BASENAME" \
		fedora.server.BasicServer)

    if [ $? != 0 ]; then
    	restoreJavaHome
        exit 1
    fi

	(exec "$JAVA" -classpath "$TC"/webapps/fedora/WEB-INF/classes:"$SERVER_LIBS" \
		-Djavax.net.ssl.trustStore="$FEDORA_HOME"/server/truststore \
		-Djavax.net.ssl.trustStorePassword=tomcat \
		-Dfedora.home="$FEDORA_HOME" \
		-Dtomcat.dir="$TC_BASENAME" \
		fedora.server.utilities.status.ServerStatusTool init)

    if [ $? != 0 ]; then
    	restoreJavaHome
        exit 1
    fi

	# start Tomcat
	if [ -z "${SERVER_PROFILE}" ]; then
		echo "Using the default server profile"
	else
		echo "Using server profile: $SERVER_PROFILE"
	fi
	
  	(exec nohup "$JAVA" -server -Xmn64m -Xms256m -Xmx256m \
		-classpath "$TC"/bin/bootstrap.jar:"$JAVA_HOME"/lib/tools.jar \
		-Djavax.net.ssl.trustStore="$FEDORA_HOME"/server/truststore \
		-Djavax.net.ssl.trustStorePassword=tomcat \
		-Djava.awt.fonts="$JAVA_HOME"/jre/lib/fonts \
		-Djava2d.font.usePlatformFont=false \
		-Djava.awt.headless=true \
		-Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl \
		-Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl \
		-Dfedora.home="$FEDORA_HOME" \
		-Dfedora.serverProfile=$SERVER_PROFILE \
		-Dclasspath=$TC/bin/bootstrap.jar:"$JAVA_HOME"/lib/tools.jar \
		-Djava.endorsed.dirs="$TC_ENDORSED" \
		-Djava.security.manager \
		-Djava.security.policy="$TC"/conf/catalina.policy \
		-Dcatalina.base="$TC" \
		-Dcatalina.home="$TC" \
		-Djava.io.tmpdir="$TC"/temp \
		-Djava.security.auth.login.config="$TC"/conf/jaas.config \
		-Djava.util.logging.config.file="$FEDORA_HOME"/server/fedora-internal-use/config/logging.properties\
		org.apache.catalina.startup.Bootstrap start &)
	
	(exec "$JAVA" -classpath "$TC"/webapps/fedora/WEB-INF/classes:"$SERVER_LIBS" \
		-Djavax.net.ssl.trustStore="$FEDORA_HOME"/server/truststore \
		-Djavax.net.ssl.trustStorePassword=tomcat \
		-Dfedora.home="$FEDORA_HOME" \
		-Dtomcat.dir="$TC_BASENAME" \
		fedora.server.utilities.status.ServerStatusTool watch-startup)

    if [ $? != 0 ]; then
        echo "Stopping Tomcat due to startup failure..."
        sleep 5
        stopTomcat
    	restoreJavaHome
        exit 1
    fi

    echo "Finished.  To stop server, use fedora-stop."
	restoreJavaHome
}

debug() {
	SERVER_PROFILE="$3"

	echo "Starting the Fedora server..."
	if [ ! -d "$FEDORA_HOME/server/logs/" ]; then
		mkdir "$FEDORA_HOME"/server/logs
	fi
	
	(exec "$JAVA" -classpath "$TC"/webapps/fedora/WEB-INF/classes:"$SERVER_LIBS" \
		-Djavax.net.ssl.trustStore="$FEDORA_HOME"/server/truststore \
		-Djavax.net.ssl.trustStorePassword=tomcat \
		-Dfedora.home="$FEDORA_HOME" \
		-Dtomcat.dir="$TC_BASENAME" \
		fedora.server.BasicServer)

    if [ $? != 0 ]; then
    	restoreJavaHome
        exit 1
    fi

	(exec "$JAVA" -classpath "$TC"/webapps/fedora/WEB-INF/classes:"$SERVER_LIBS" \
		-Djavax.net.ssl.trustStore="$FEDORA_HOME"/server/truststore \
		-Djavax.net.ssl.trustStorePassword=tomcat \
		-Dfedora.home="$FEDORA_HOME" \
		-Dtomcat.dir="$TC_BASENAME" \
		fedora.server.utilities.status.ServerStatusTool init)

    if [ $? != 0 ]; then
    	restoreJavaHome
        exit 1
    fi

	# start Tomcat
	if [ -z "${SERVER_PROFILE}" ]; then
		echo "Using the default server profile"
	else
		echo "Using server profile: $SERVER_PROFILE"
	fi
	
  	(exec nohup "$JAVA" -server -Xmn64m -Xms256m -Xmx256m \
		-Xnoagent -Xdebug -Djava.compiler=none \
		-Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n \
		-classpath "$TC"/bin/bootstrap.jar:"$JAVA_HOME"/lib/tools.jar \
		-Djavax.net.ssl.trustStore="$FEDORA_HOME"/server/truststore \
		-Djavax.net.ssl.trustStorePassword=tomcat \
		-Djava.awt.fonts="$JAVA_HOME"/jre/lib/fonts \
		-Djava2d.font.usePlatformFont=false \
		-Djava.awt.headless=true \
		-Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl \
		-Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl \
		-Dfedora.home="$FEDORA_HOME" \
		-Dfedora.serverProfile=$SERVER_PROFILE \
		-Dclasspath=$TC/bin/bootstrap.jar:"$JAVA_HOME"/lib/tools.jar \
		-Djava.endorsed.dirs="$TC_ENDORSED" \
		-Djava.security.manager \
		-Djava.security.policy="$TC"/conf/catalina.policy \
		-Dcatalina.base="$TC" \
		-Dcatalina.home="$TC" \
		-Djava.io.tmpdir="$TC"/temp \
		-Djava.security.auth.login.config="$TC"/conf/jaas.config \
		-Djava.util.logging.config.file="$FEDORA_HOME"/server/fedora-internal-use/config/logging.properties\
		org.apache.catalina.startup.Bootstrap start &)
	
	(exec "$JAVA" -classpath "$TC"/webapps/fedora/WEB-INF/classes:"$SERVER_LIBS" \
		-Djavax.net.ssl.trustStore="$FEDORA_HOME"/server/truststore \
		-Djavax.net.ssl.trustStorePassword=tomcat \
		-Dfedora.home="$FEDORA_HOME" \
		-Dtomcat.dir="$TC_BASENAME" \
		fedora.server.utilities.status.ServerStatusTool watch-startup)

    if [ $? != 0 ]; then
        echo "Stopping Tomcat due to startup failure..."
        sleep 5
        stopTomcat
    	restoreJavaHome
        exit 1
    fi

    echo "Starting jdb..."
    (exec "$JAVA_HOME/bin/jdb" -connect com.sun.jdi.SocketAttach:hostname=localhost,port=8000)
	restoreJavaHome
}

stop() {
	echo "Stopping the Fedora Server..."
    stopTomcat

    if [ $? != 0 ]; then
    	restoreJavaHome
        exit 1
    fi

    (exec "$JAVA" -classpath "$TC"/webapps/fedora/WEB-INF/classes:"$SERVER_LIBS" \
        -Djavax.net.ssl.trustStore="$FEDORA_HOME"/server/truststore \
        -Djavax.net.ssl.trustStorePassword=tomcat \
        -Dfedora.home="$FEDORA_HOME" \
        -Dtomcat.dir="$TC_BASENAME" \
        fedora.server.utilities.status.ServerStatusTool watch-shutdown)

    if [ $? != 0 ]; then
    	restoreJavaHome
        exit 1
    fi

	restoreJavaHome
}

stopTomcat() {

	# Stop Tomcat
	(exec "$JAVA" -cp "$TC"/bin/bootstrap.jar:"$JAVA_HOME"/lib/tools.jar \
		-Djavax.net.ssl.trustStore="$FEDORA_HOME"/server/truststore \
		-Djavax.net.ssl.trustStorePassword=tomcat \
		-Dfedora.home="$FEDORA_HOME" \
		-Dclasspath="$TC"/bin/bootstrap.jar:"$JAVA_HOME"/lib/tools.jar \
		-Djava.endorsed.dirs="$TC_ENDORSED" \
		-Dcatalina.base="$TC" \
		-Dcatalina.home="$TC" \
		-Djava.io.tmpdir="$TC"/temp \
		org.apache.catalina.startup.Bootstrap stop)
}

restart() {
	stop
	start "$@"
	restoreJavaHome
}

status() {
    (exec "$JAVA" -classpath "$TC"/webapps/fedora/WEB-INF/classes:"$SERVER_LIBS" \
        -Djavax.net.ssl.trustStore="$FEDORA_HOME"/server/truststore \
        -Djavax.net.ssl.trustStorePassword=tomcat \
        -Dfedora.home="$FEDORA_HOME" \
        -Dtomcat.dir="$TC_BASENAME" \
        fedora.server.utilities.status.ServerStatusTool show-status)
    exit $?
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
		echo "Usage: $0 {start|stop|restart|debug|status}"
		;;
esac
