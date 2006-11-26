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

(exec "$JAVA_HOME/bin/java" -server -Xmn64m -Xms256m -Xmx256m \
    -cp "$_CP" -Djava.endorsed.dirs="$_ED" -Dfedora.home="$FEDORA_HOME" \
    -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl \
    -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl \
    -Dfedora.serverProfile=$1 fedora.server.utilities.rebuild.Rebuild $1)

exit $?
