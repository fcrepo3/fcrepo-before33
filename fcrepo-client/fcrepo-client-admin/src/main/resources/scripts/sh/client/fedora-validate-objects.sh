#!/bin/sh

scriptdir=`dirname "$0"`
. "$scriptdir"/env-client.sh

execWithCmdlineArgs fedora.client.utility.validate.process.ValidatorProcess

exit $?
