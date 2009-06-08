#!/bin/bash

echo "-----------------------"
echo "Running System Test...."
echo "-----------------------"
echo ""

SCRIPTPATH=$(cd ${0%/*} && echo $PWD/${0##*/})
SCRIPTDIR=`dirname "$SCRIPTPATH"`
. $SCRIPTDIR/common.sh

# Basic check of arguments
if [ $# -lt 2 ]; then
  echo "ERROR: Expected at least 2 args; java5|java6 -Dtest=fedora.test.SomeSysTestSuite ..."
  exit 1
fi

# After this, $* contains all args after java5|java6
shift

echo ""

cd $BUILD_HOME
$ANT_HOME/bin/ant junit $*
if [ $? -ne 0 ]; then
  echo ""
  echo "ERROR: System test failed; see above"
  exit 1
fi

echo ""
echo "---------------------------------"
echo "Finished System Test Successfully"
echo "---------------------------------"

