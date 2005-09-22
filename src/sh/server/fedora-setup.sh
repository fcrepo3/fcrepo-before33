#!/bin/sh
# ----------------------------------------------------------------------
# Fedora Server security setup script
# ----------------------------------------------------------------------

# ----------------------------------------------------------------------
# Security setup


# Reset the input field separator to its default value
IFS=

# Reset the execution path 
PATH=/bin:/usr/bin:/usr/local/bin:/opt/bin

# Cannot proceed if FEDORA_HOME is not set
if [ -z "$FEDORA_HOME" ]; then
	echo "ERROR: The FEDORA_HOME environment variable is not defined."
	exit 1
fi

if [ -r "$FEDORA_HOME/server/bin/set-env.sh" ]; then
  	. "$FEDORA_HOME"/server/bin/set-env.sh
else
	echo "ERROR: $FEDORA_HOME/server/bin/set-env.sh was not found."
	exit 1
fi

TC_BASENAME="@tomcat.basename@"
TC="$FEDORA_HOME"/server/"$TC_BASENAME"
WEBAPP_DIR="$TC"/webapps/fedora/WEB-INF

# Check for valid configuration names
if [ "$1" != "secure-apim"   ] &&
   [ "$1" != "secure-all"    ] &&
   [ "$1" != "unsecure-apim" ] &&
   [ "$1" != "unsecure-all"  ]; then
       echo
       echo "Usage: fedora-setup configuration-name"
       echo
       echo "    where configuration-name must be one of the following:"
       echo "        secure-apim   - API-M with basicAuth and SSL; API-A with no basicAuth and no SSL"
       echo "        secure-all    - API-M with basicAuth and SSL; API-A with basicAuth and SSL"
       echo "        unsecure-apim - API-M with basicAuth but no SSL; API-A with no basicAuth and no SSL"
       echo "        unsecure-all  - API-M with basicAuth but no SSL; API-A with basicAuth but no SSL"
       echo
       exit 1
fi


echo
echo "Copying"
echo "   FROM: $FEDORA_HOME/server/config/fedora-$1.fcfg"
echo "     TO: $FEDORA_HOME/server/config/fedora.fcfg"
cp $FEDORA_HOME/server/config/fedora-$1.fcfg $FEDORA_HOME/server/config/fedora.fcfg
echo "Copying"
echo "   FROM: $FEDORA_HOME/server/config/beSecurity-$1.xml"
echo "     TO: $FEDORA_HOME/server/config/beSecurity.xml"
cp $FEDORA_HOME/server/config/beSecurity-$1.xml $FEDORA_HOME/server/config/beSecurity.xml
echo "Copying"
echo "   FROM: $WEBAPP_DIR/web-$1.xml"
echo "     TO: $WEBAPP_DIR/web.xml"
cp $WEBAPP_DIR/web-$1.xml $WEBAPP_DIR/web.xml

echo
echo "Fedora security setup complete!"
echo "Configuration files in play are:"
echo "   fedora-$1.fcfg"
echo "   beSecurity-$1.xml"
echo "   web-$1.xml"

exit 0