#!/bin/sh
# ----------------------------------------------------------------------
# XACML Policy Validation script
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

TOMCAT_DIR=@tomcat.basename@
TC="$FEDORA_HOME"/server/"$TOMCAT_DIR"
TC_COMMON="$TC/common"

echo "Starting XACML Policy Validation script..."
(exec "$JAVA" -cp "$TC"/webapps/fedora/WEB-INF/classes:"$TC_COMMON"/lib/sunxacml.jar:"$TC_COMMON"/lib/xercesImpl.jar:"$TC_COMMON"/lib/xml-apis.jar \
              -Dfedora.home="$FEDORA_HOME" \
              -Dtomcat.dir="$TOMCAT_DIR"  \
              -Dcom.sun.xacml.PolicySchema="$FEDORA_HOME"/server/xsd/cs-xacml-schema-policy-01.xsd \
              -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl \
              fedora.server.security.ValidatePolicy "$1")

restoreJavaHome
