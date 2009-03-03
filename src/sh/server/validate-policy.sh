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

if [ -z "$1" ]; then
    echo "ERROR: Must supply filename."
    exit 1
fi

_CP=$CATALINA_HOME/webapps/fedora/WEB-INF/classes
_ED=$CATALINA_HOME/webapps/fedora/WEB-INF/lib:$CATALINA_HOME/common/endorsed:$CATALINA_HOME/common/lib

(exec "$JAVA_HOME/bin/java" -cp "$_CP" -Djava.endorsed.dirs="$_ED" -Dfedora.home="$FEDORA_HOME" \
    -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl \
    -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl \
    -Dcom.sun.xacml.PolicySchema="$FEDORA_HOME/server/xsd/cs-xacml-schema-policy-01.xsd" \
    fedora.server.security.PolicyParser "$1")

if [ $? -eq 0 ]; then
    echo "Validation successful"
    exit 0
else
    echo "Validation failed"
    exit 1
fi
