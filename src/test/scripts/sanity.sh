#!/bin/bash

echo "========================="
echo "Starting sanity tests...."
echo "========================="
echo ""

SCRIPTPATH=$(cd ${0%/*} && echo $PWD/${0##*/})
SCRIPTDIR=`dirname "$SCRIPTPATH"`
. $SCRIPTDIR/common.sh

echo ""

echo "========================="
echo "Compiling distribution..."
echo "========================="
echo ""
cd $BUILD_HOME
$ANT_HOME/bin/ant clean generatedCode release

if [ $? -ne 0 ]; then
  echo ""
  echo "ERROR: Failed to compile distribution; see above"
  exit 1
fi

echo ""
echo "========================"
echo "Running offline tests..."
echo "========================"
echo ""
$ANT_HOME/bin/ant junit

if [ $? -ne 0 ]; then
  echo ""
  echo "ERROR: Offline tests failed; see above"
  exit 1
fi

echo ""
echo "==========================="
echo "Running sanity system tests"
echo "==========================="
echo ""

#
# Config B Tests
#

$SCRIPTDIR/install-fedora.sh $1 ConfigB.properties

if [ $? -ne 0 ]; then
  echo ""
  echo "ERROR: Failed while installing Fedora for ConfigB tests; see above"
  exit 1
fi

$CATALINA_HOME/bin/startup.sh

if [ $? -ne 0 ]; then
  echo ""
  echo "ERROR: Failed while starting Fedora for ConfigB tests; see above"
  exit 1
fi

echo "Waiting 30 seconds for Fedora to start..."
sleep 30

echo ""
echo "[Running ConfigB Tests...]"

$SCRIPTDIR/systest.sh $1 -Dtest=fedora.test.AllSystemTestsConfigB

if [ $? -ne 0 ]; then
  echo ""
  echo "ERROR: Failed ConfigB tests; see above"
  echo "Shutting down Tomcat..."
  $CATALINA_HOME/bin/shutdown.sh
  sleep 5
  exit 1
fi

echo "Shutting down tomcat..."
$CATALINA_HOME/bin/shutdown.sh
sleep 5

#
# End of Config B Tests
#

#
# Config A and C Tests
#

$SCRIPTDIR/install-fedora.sh $1 ConfigAC.properties

if [ $? -ne 0 ]; then
  echo ""
  echo "ERROR: Failed while installing Fedora for ConfigA and ConfigC tests; see above"
  exit 1
fi

$CATALINA_HOME/bin/startup.sh

if [ $? -ne 0 ]; then
  echo ""
  echo "ERROR: Failed while starting Fedora for ConfigA and ConfigC tests; see above"
  exit 1
fi

echo "Waiting 30 seconds for Fedora to start..."
sleep 30

echo ""
echo "[Running ConfigA Tests...]"

$SCRIPTDIR/systest.sh $1 -Dtest=fedora.test.AllSystemTestsConfigA

if [ $? -ne 0 ]; then
  echo ""
  echo "ERROR: Failed ConfigA tests; see above"
  echo "Shutting down Tomcat..."
  $CATALINA_HOME/bin/shutdown.sh
  sleep 5
  exit 1
fi

echo ""
echo "[Running ConfigB Tests...]"

$SCRIPTDIR/systest.sh $1 -Dtest=fedora.test.AllSystemTestsConfigC

if [ $? -ne 0 ]; then
  echo ""
  echo "ERROR: Failed ConfigC tests; see above"
  echo "Shutting down Tomcat..."
  $CATALINA_HOME/bin/shutdown.sh
  sleep 5
  exit 1
fi

echo "Shutting down tomcat..."
$CATALINA_HOME/bin/shutdown.sh
sleep 5


#
# End of Config A and C Tests
#

echo ""
echo "===================================="
echo "Completed sanity tests successfully!"
echo "===================================="

