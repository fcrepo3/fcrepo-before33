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
if [ "$1" != "ssl-authenticate-apim"   ] &&
   [ "$1" != "ssl-authenticate-all"    ] &&
   [ "$1" != "no-ssl-authenticate-apim" ] &&
   [ "$1" != "no-ssl-authenticate-all"  ]; then
       echo
       echo "Usage: fedora-setup [configuration-name]"
       echo
       echo "    where [configuration-name] must be one of the following:"
       echo
       echo "        ssl-authenticate-apim - API-M with basicAuth and SSL"
       echo "                              - API-A with no basicAuth and no SSL"
       echo
       echo "        ssl-authenticate-all  - API-M with basicAuth and SSL
       echo "                              - API-A with basicAuth and SSL"
       echo
       echo "        no-ssl-authenticate-apim - API-M with basicAuth but no SSL
       echo "                                 - API-A with no basicAuth and no SSL"
       echo
       echo "        no-ssl-authenticate-all  - API-M with basicAuth but no SSL
       echo "                                 - API-A with basicAuth but no SSL"
       echo
       exit 1
fi

if [ "$1" = "ssl-authenticate-apim" ]; then
   CONFIG_SUFFIX="secure-apim"
fi

if [ "$1" = "ssl-authenticate-all" ]; then
   CONFIG_SUFFIX="secure-all"
fi

if [ "$1" = "no-ssl-authenticate-apim" ]; then
   CONFIG_SUFFIX="unsecure-apim"
fi

if [ "$1" = "no-ssl-authenticate-all" ]; then
   CONFIG_SUFFIX="unsecure-all"
fi

echo
echo "Copying"
echo "   FROM: $FEDORA_HOME/server/config/fedora-$CONFIG_SUFFIX.fcfg"
echo "     TO: $FEDORA_HOME/server/config/fedora.fcfg"
cp $FEDORA_HOME/server/config/fedora-$CONFIG_SUFFIX.fcfg $FEDORA_HOME/server/config/fedora.fcfg
echo "Copying"
echo "   FROM: $FEDORA_HOME/server/config/beSecurity-$CONFIG_SUFFIX.xml"
echo "     TO: $FEDORA_HOME/server/config/beSecurity.xml"
cp $FEDORA_HOME/server/config/beSecurity-$CONFIG_SUFFIX.xml $FEDORA_HOME/server/config/beSecurity.xml
echo "Copying"
echo "   FROM: $WEBAPP_DIR/web-$CONFIG_SUFFIX.xml"
echo "     TO: $WEBAPP_DIR/web.xml"
cp $WEBAPP_DIR/web-$CONFIG_SUFFIX.xml $WEBAPP_DIR/web.xml

echo
echo "Fedora security setup complete!"
echo "Configuration files in play are:"
echo "   fedora-$CONFIG_SUFFIX.fcfg"
echo "   beSecurity-$CONFIG_SUFFIX.xml"
echo "   web-$CONFIG_SUFFIX.xml"

exit 0