# REQUIRED ENVIRONMENT VARIABLES
# 	FEDORA_HOME
#	FEDORA_JAVA_HOME or JAVA_HOME

# Make sure requisite environment variables are set

# FEDORA_HOME
if [ -z "$FEDORA_HOME" ]; then
	echo "ERROR: The FEDORA_HOME environment variable is not defined."
	exit 1
fi

if [ ! -f "$FEDORA_HOME/client/client.jar" ]; then
	echo "ERROR: FEDORA_HOME does not appear to be set correctly."
	echo "Client directory not found at $FEDORA_HOME/client"
	exit 1
fi

# FEDORA_JAVA_HOME or JAVA_HOME
if [ -z "${FEDORA_JAVA_HOME:=$JAVA_HOME}" ]; then
	echo "ERROR: neither FEDORA_JAVA_HOME nor JAVA_HOME is defined."
    exit 1
fi

if [ ! -x "$FEDORA_JAVA_HOME"/bin/java -o ! -x "$FEDORA_JAVA_HOME"/bin/orbd ]; then
    echo "Neither FEDORA_JAVA_HOME nor JAVA_HOME appears to be set correcty."
    echo "Make sure FEDORA_JAVA_HOME or JAVA_HOME points to a 1.4JRE/JDK base."
	exit 1
fi

# If set, JAVA_HOME will be restored at the end of the script
JAVA_HOME_BACKUP="$JAVA_HOME"
JAVA_HOME="$FEDORA_JAVA_HOME"
export JAVA_HOME
JAVA="$JAVA_HOME"/bin/java

# Restore JAVA_HOME to its original prior, if any
restoreJavaHome() {
	if [ -z "$JAVA_HOME_BACKUP" ]; then
		export JAVA_HOME="$JAVA_HOME_BACKUP"
	fi
}
