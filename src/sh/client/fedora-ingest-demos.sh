#!/bin/sh

if [ "$FEDORA_HOME" = "" ]; then
  echo "ERROR: Environment variable, FEDORA_HOME must be set."
  exit 1
fi

if [ ! -f "$FEDORA_HOME/client/client.jar" ]; then
  echo "ERROR: FEDORA_HOME does not appear correctly set."
  echo "Client cannot be found at $FEDORA_HOME/client/client.jar"
  exit 1
fi

if [ "$FEDORA_JAVA_HOME" = "" ]; then


  if [ "$JAVA_HOME" = "" ]; then
    echo "ERROR: FEDORA_JAVA_HOME was not defined, nor was (the fallback) JAVA_HOME."
    exit 1
  else
    THIS_JAVA_HOME=$JAVA_HOME
  fi
else
  THIS_JAVA_HOME=$FEDORA_JAVA_HOME
fi

if [ ! -f "$THIS_JAVA_HOME/bin/java" ]; then
  echo "ERROR: java was not found in $THIS_JAVA_HOME"
  echo "Make sure FEDORA_JAVA_HOME or JAVA_HOME is set correctly."
  exit 1
fi

if [ ! -f "$THIS_JAVA_HOME/bin/orbd" ]; then 
  echo "ERROR: java was found in $THIS_JAVA_HOME, but it was not version 1.4"
  echo "Make sure FEDORA_JAVA_HOME or JAVA_HOME points to a 1.4JRE/JDK base."
  exit 1
fi

echo "Starting Fedora DemoIngester..."

OLD_JAVA_HOME=$JAVA_HOME
JAVA_HOME=$THIS_JAVA_HOME
export JAVA_HOME

echo "Ingesting Demonstration Objects..."

echo "Ingesting local-server simple image demo (1 bdef, 1 bmech, 1 object)..."
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.ingest.AutoIngestor $1 $2 $3 $4 $FEDORA_HOME/client/demo/local-server-demos/simple-image-demo/bdef-simple-image.xml "Created by fedora-ingest-demos script")
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.ingest.AutoIngestor $1 $2 $3 $4 $FEDORA_HOME/client/demo/local-server-demos/simple-image-demo/bmech-simple-image-4res.xml "Created by fedora-ingest-demos script")
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.ingest.AutoIngestor $1 $2 $3 $4 $FEDORA_HOME/client/demo/local-server-demos/simple-image-demo/obj-image-4res-colliseum.xml "Created by fedora-ingest-demos script")

echo "Ingesting local-server simple document demos (2 objects)..."
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.ingest.AutoIngestor $1 $2 $3 $4 $FEDORA_HOME/client/demo/local-server-demos/simple-document-demo/obj-document-ECDLpaper.xml "Created by fedora-ingest-demos script")
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.ingest.AutoIngestor $1 $2 $3 $4 $FEDORA_HOME/client/demo/local-server-demos/simple-document-demo/obj-document-batik-demos.xml "Created by fedora-ingest-demos script")

echo "Ingesting local-server image manipulation demo (1 bdef, 1 bmech, 1 object)..."
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.ingest.AutoIngestor $1 $2 $3 $4 $FEDORA_HOME/client/demo/local-server-demos/image-manip-demo/bdef-image-manip.xml "Created by fedora-ingest-demos script")
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.ingest.AutoIngestor $1 $2 $3 $4 $FEDORA_HOME/client/demo/local-server-demos/image-manip-demo/bmech-image-manip.xml "Created by fedora-ingest-demos script")
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.ingest.AutoIngestor $1 $2 $3 $4 $FEDORA_HOME/client/demo/local-server-demos/image-manip-demo/obj-image-manip-colliseum.xml "Created by fedora-ingest-demos script")

echo "Ingesting local-server document transform demo (1 bdef, 1 bmech, 1 object)..."
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.ingest.AutoIngestor $1 $2 $3 $4 $FEDORA_HOME/client/demo/local-server-demos/document-transform-demo/bdef-document-trans.xml "Created by fedora-ingest-demos script")
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.ingest.AutoIngestor $1 $2 $3 $4 $FEDORA_HOME/client/demo/local-server-demos/document-transform-demo/bmech-document-trans-saxon.xml "Created by fedora-ingest-demos script")
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.ingest.AutoIngestor $1 $2 $3 $4 $FEDORA_HOME/client/demo/local-server-demos/document-transform-demo/obj-document-fedoraAPIA.xml "Created by fedora-ingest-demos script")

echo "Ingesting local-server formatting objects demo (2 bdefs, 2 bmechs, 2 objects)..."
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.ingest.AutoIngestor $1 $2 $3 $4 $FEDORA_HOME/client/demo/local-server-demos/formatting-objects-demo/bdef-fo.xml "Created by fedora-ingest-demos script")
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.ingest.AutoIngestor $1 $2 $3 $4 $FEDORA_HOME/client/demo/local-server-demos/formatting-objects-demo/bdef-pdf.xml "Created by fedora-ingest-demos script")
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.ingest.AutoIngestor $1 $2 $3 $4 $FEDORA_HOME/client/demo/local-server-demos/formatting-objects-demo/bmech-fop.xml "Created by fedora-ingest-demos script")
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.ingest.AutoIngestor $1 $2 $3 $4 $FEDORA_HOME/client/demo/local-server-demos/formatting-objects-demo/bmech-tei-to-fo.xml "Created by fedora-ingest-demos script")
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.ingest.AutoIngestor $1 $2 $3 $4 $FEDORA_HOME/client/demo/local-server-demos/formatting-objects-demo/obj-fop-to-pdf.xml "Created by fedora-ingest-demos script")
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.ingest.AutoIngestor $1 $2 $3 $4 $FEDORA_HOME/client/demo/local-server-demos/formatting-objects-demo/obj-tei-to-pdf.xml "Created by fedora-ingest-demos script")

echo "Ingesting open-server simple image demos (2 bmechs, 3 objects)..."
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.ingest.AutoIngestor $1 $2 $3 $4 $FEDORA_HOME/client/demo/open-server-demos/simple-image-demos/bmech-simple-image-4res-zoom.xml "Created by fedora-ingest-demos script")
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.ingest.AutoIngestor $1 $2 $3 $4 $FEDORA_HOME/client/demo/open-server-demos/simple-image-demos/bmech-simple-image-mrsid.xml "Created by fedora-ingest-demos script")
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.ingest.AutoIngestor $1 $2 $3 $4 $FEDORA_HOME/client/demo/open-server-demos/simple-image-demos/obj-image-4res-pavilliondraw.xml "Created by fedora-ingest-demos script")
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.ingest.AutoIngestor $1 $2 $3 $4 $FEDORA_HOME/client/demo/open-server-demos/simple-image-demos/obj-image-4res-pavilliondraw2.xml "Created by fedora-ingest-demos script")
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.ingest.AutoIngestor $1 $2 $3 $4 $FEDORA_HOME/client/demo/open-server-demos/simple-image-demos/obj-image-mrsid-pavillion.xml "Created by fedora-ingest-demos script")

echo "Ingesting open-server user param image demo (1 bdef, 1 bmech, 2 objects)..."
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.ingest.AutoIngestor $1 $2 $3 $4 $FEDORA_HOME/client/demo/open-server-demos/userinput-image-demo/bdef-image-userinput.xml "Created by fedora-ingest-demos script")
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.ingest.AutoIngestor $1 $2 $3 $4 $FEDORA_HOME/client/demo/open-server-demos/userinput-image-demo/bmech-image-userinput-mrsid.xml "Created by fedora-ingest-demos script")
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.ingest.AutoIngestor $1 $2 $3 $4 $FEDORA_HOME/client/demo/open-server-demos/userinput-image-demo/obj-image-userinput-archdraw.xml "Created by fedora-ingest-demos script")
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.ingest.AutoIngestor $1 $2 $3 $4 $FEDORA_HOME/client/demo/open-server-demos/userinput-image-demo/obj-image-userinput-column.xml "Created by fedora-ingest-demos script")

echo "Ingesting open-server EAD finding aid demo (1 bdef, 1 bmech, 1 object)..."
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.ingest.AutoIngestor $1 $2 $3 $4 $FEDORA_HOME/client/demo/open-server-demos/EAD-finding-aid-demo/bdef-ead-finding-aid.xml "Created by fedora-ingest-demos script")
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.ingest.AutoIngestor $1 $2 $3 $4 $FEDORA_HOME/client/demo/open-server-demos/EAD-finding-aid-demo/bmech-ead-finding-aid.xml "Created by fedora-ingest-demos script")
(exec $JAVA_HOME/bin/java -Xms64m -Xmx96m -cp $FEDORA_HOME/client:$FEDORA_HOME/client/client.jar -Dfedora.home=$FEDORA_HOME -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl fedora.client.ingest.AutoIngestor $1 $2 $3 $4 $FEDORA_HOME/client/demo/open-server-demos/EAD-finding-aid-demo/obj-ead-finding-aid.xml "Created by fedora-ingest-demos script")

echo "Finished."

JAVA_HOME=$OLD_JAVA_HOME
export JAVA_HOME

exit 0
