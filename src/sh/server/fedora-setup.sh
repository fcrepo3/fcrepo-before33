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

if [ "$1" = "ssl-authenticate-apim" ]; then
   CONFIG_SUFFIX="secure-apim"
elif [ "$1" = "ssl-authenticate-all" ]; then
   CONFIG_SUFFIX="secure-all"
elif [ "$1" = "no-ssl-authenticate-apim" ]; then
   CONFIG_SUFFIX="unsecure-apim"
elif [ "$1" = "no-ssl-authenticate-all" ]; then
   CONFIG_SUFFIX="unsecure-all"
fi

# Apply properties to fedora-base.fcfg to create fedora.fcfg
FCFG_HOME="$FEDORA_HOME"/server/config
FEDORA_INTERNAL="$FEDORA_HOME"/server/fedora-internal-use
MY_PROPS="$FCFG_HOME"/my.properties

FCFG_BASE="$FEDORA_INTERNAL"/config/fedora-base.fcfg
PROPS="$FEDORA_INTERNAL"/config/fedora-"${CONFIG_SUFFIX}".properties
OUT="$FCFG_HOME"/fedora.fcfg

if [ -r "$MY_PROPS" ]; then
	echo -e "\nApplying \n\t$MY_PROPS"
	(exec "$JAVA" -classpath "$TC"/webapps/fedora/WEB-INF/classes \
		  fedora.server.config.ServerConfiguration $FCFG_BASE $MY_PROPS > $OUT)
		  
	echo -e "\t$PROPS"
	(exec mv $OUT ${OUT}.tmp)
	(exec "$JAVA" -classpath "$TC"/webapps/fedora/WEB-INF/classes \
		  fedora.server.config.ServerConfiguration ${OUT}.tmp $PROPS > $OUT)
	(exec rm ${OUT}.tmp)
else
	echo -e "\nApplying \n\t$PROPS"
	(exec "$JAVA" -classpath "$TC"/webapps/fedora/WEB-INF/classes \
		  fedora.server.config.ServerConfiguration $FCFG_BASE $PROPS > $OUT)
fi

echo "Wrote"
echo -e "\t$OUT\n"
echo "Copying"
echo "   FROM: $FEDORA_INTERNAL/config/beSecurity-$CONFIG_SUFFIX.xml"
echo "     TO: $FEDORA_HOME/server/config/beSecurity.xml"
cp $FEDORA_INTERNAL/config/beSecurity-$CONFIG_SUFFIX.xml $FEDORA_HOME/server/config/beSecurity.xml
echo "Copying"
echo "   FROM: $WEBAPP_DIR/web-$CONFIG_SUFFIX.xml"
echo "     TO: $WEBAPP_DIR/web.xml"
cp $WEBAPP_DIR/web-$CONFIG_SUFFIX.xml $WEBAPP_DIR/web.xml

echo
echo "Fedora security setup complete!"
echo "Configuration files in play are:"
echo "   fedora-$CONFIG_SUFFIX.properties"
echo "   beSecurity-$CONFIG_SUFFIX.xml"
echo "   web-$CONFIG_SUFFIX.xml"

exit 0
