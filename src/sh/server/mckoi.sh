#!/bin/sh
# ----------------------------------------------------------------------
# McKoi start/stop script
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

# McKoi env
MCKOI_BASENAME=@mckoi.basename@
MCKOI_HOME="$FEDORA_HOME"/server/"$MCKOI_BASENAME"
MCKOI_CLASSPATH="$MCKOI_HOME"/gnu-regexp-1.1.4.jar
MCKOIDB_JAR="$MCKOI_HOME"/mckoidb.jar
MCKOI_CONF="$MCKOI_HOME"/db.conf
MCKOI_DB="$MCKOI_HOME"/data/DefaultDatabase_sf.koi
MCKOI_PORT=9157

if [ ! -f "$MCKOIDB_JAR" ]; then
  echo "ERROR: No mckoidb.jar found in $MCKOIDB_JAR"
  exit 1
fi

# ----------------------------------------------------------------------
# Functions

admin() {
	if [ $# -lt 2 ]; then
		echo "Usage: mckoi-admin adminUser adminPass"
		echo "Use the values you gave when running mckoi-init."
		exit 1
	fi

	echo "Launching McKoi SQL Interface... "

	(exec "$JAVA" -cp "$MCKOIDB_JAR" \
				  com.mckoi.tools.JDBCQueryTool \
				  -u "$1" -p "$2" -url "jdbc:mckoi://127.0.0.1:${MCKOI_PORT}/")
}

init() {
	if [ -f "$MCKOI_DB" ]; then
		echo "ERROR: McKoi database already initialized.  "
		echo "Remove $MCKOI_HOME/data to delete."
		exit 1
	fi
	
	if [ $# -lt 2 ]; then
		echo "Usage: mckoi-init adminUser adminPass"
		echo "Use your own user and password values, and remember them for later."
		exit 1
	fi

	echo "Initializing McKoi DB... "
	(exec $JAVA -cp "$MCKOI_CLASSPATH" \
	            -jar "$MCKOIDB_JAR" \
	            -conf "$MCKOI_CONF" \
	            -create "$1" "$2")
	restoreJavaHome
}

start() {
	if [ ! -f "$MCKOI_DB" ]; then
		echo "ERROR: McKoi database hasn't been initialized, run mckoi-init first."
		exit 1
	fi
	
	echo "Starting McKoi DB..."
	(exec $JAVA -Xms64m -Xmx96m \
	            -cp "$MCKOI_CLASSPATH" \
	            -jar "$MCKOIDB_JAR" \
	            -conf "$MCKOI_CONF" &)
	
	restoreJavaHome
}

stop() {
	if [ ! -f "$MCKOI_DB" ]; then
		echo "ERROR: McKoi database hasn't been initialized, run mckoi-init first."
		exit 1
	fi
	
	if [ $# -lt 2 ]; then
		echo "Usage: mckoi-stop adminUser adminPass"
		echo "Use the same user/pass values used when running mckoi-init"
		exit 1
	fi

	echo "Stopping McKoi..."
	(exec $JAVA -Xms64m -Xmx96m \
	            -cp "$MCKOI_CLASSPATH" \
	            -jar "$MCKOIDB_JAR" \
	            -conf "$MCKOI_CONF" \
	            -shutdown localhost ${MCKOI_PORT} "$1" "$2")
	restoreJavaHome
}

restart() {
	stop "$@"
	start "$@"
	restoreJavaHome
}

status() {
	echo "Sorry, this method is not yet supported."
}

# ----------------------------------------------------------------------
# 

case "$1" in
	admin)
		shift
		admin "$@"
		;;
	init)
		shift
		init "$@"
		;;
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
		echo "Usage: $0 {init|start|stop|restart|status}"
		;;
esac