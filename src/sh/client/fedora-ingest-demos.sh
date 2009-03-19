#!/bin/sh

scriptdir=`dirname "$0"`
. "$scriptdir"/env-client.sh

if [ $# -lt 5 ]; then
	echo "ERROR: Not enough arguments."
	echo "Usage:"
	echo "  fedora-ingest-demos HOST PORT USERNAME PASSWORD http[s]"
	echo "Example:"
	echo "  fedora-ingest-demos localhost 8080 fedoraAdmin fedoraAdmin http"
	exit 1
fi

demo_path="$FEDORA_HOME"/client/demo/foxml/local-server-demos
demo_format=info:fedora/fedora-system:FOXML-1.1

args="\"d\" \"$demo_path\" \"$demo_format\" \"$1:$2\" \"$3\" \"$4\" \"$5\"

execWithTheseArgs fedora.client.utility.ingest.Ingest "$args"

exit $?
