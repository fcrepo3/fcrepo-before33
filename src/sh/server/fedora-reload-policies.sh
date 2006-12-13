#!/bin/sh

if [ -z "$JAVA_HOME" ]; then
    echo "ERROR: Environment variable, JAVA_HOME must be set."
    exit 1
fi

if [ -z "$FEDORA_HOME" ]; then
    echo "ERROR: Environment variable, FEDORA_HOME must be set."
    exit 1
fi

if [ -z "$CATALINA_HOME" ]; then
    echo "ERROR: Environment variable, CATALINA_HOME must be set."
    exit 1
fi

_CP=$CATALINA_HOME/webapps/fedora/WEB-INF/classes
_ED=$CATALINA_HOME/webapps/fedora/WEB-INF/lib:$CATALINA_HOME/common/endorsed:$CATALINA_HOME/common/lib

(exec "$JAVA_HOME/bin/java" -cp "$_CP" \
    -Djavax.net.ssl.trustStore="$FEDORA_HOME/server/truststore" \
    -Djavax.net.ssl.trustStorePassword=tomcat \
    -Djava.endorsed.dirs="$_ED" -Dfedora.home="$FEDORA_HOME" \
    fedora.server.utilities.ServerUtility $1 $2 $3)

exit $?
