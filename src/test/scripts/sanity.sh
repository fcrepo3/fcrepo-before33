#!/bin/bash

echo "========================="
echo "Starting sanity tests...."
echo "========================="
echo ""

SCRIPTPATH=$(cd ${0%/*} && echo $PWD/${0##*/})
SCRIPTDIR=`dirname "$SCRIPTPATH"`
. $SCRIPTDIR/common.sh

echo ""
echo "Removing $FEDORA_HOME"                                                                                        
rm -rf $FEDORA_HOME

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

# Where to put server log artifacts after each sys test
mkdir $BUILD_HOME/build/server-logs

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
echo "Waiting 20 seconds for Fedora to start..."
sleep 20
echo ""
echo "[Running ConfigB Tests...]"
$SCRIPTDIR/systest.sh $1 -Dtest=fedora.test.AllSystemTestsConfigB
if [ $? -ne 0 ]; then
  echo ""
  echo "ERROR: Failed ConfigB tests; see above"
  echo "Shutting down Tomcat..."
  $CATALINA_HOME/bin/shutdown.sh
  sleep 5
  mv $FEDORA_HOME/server/logs $BUILD_HOME/build/server-logs/fedora.test.AllSystemTestsConfigB
  exit 1
fi
echo "Shutting down tomcat..."
$CATALINA_HOME/bin/shutdown.sh
sleep 5
mv $FEDORA_HOME/server/logs $BUILD_HOME/build/server-logs/fedora.test.AllSystemTestsConfigB

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
  echo "ERROR: Failed while starting Fedora for ConfigA tests; see above"
  exit 1
fi
echo "Waiting 20 seconds for Fedora to start..."
sleep 20
echo ""
echo "[Running ConfigA Tests...]"
$SCRIPTDIR/systest.sh $1 -Dtest=fedora.test.AllSystemTestsConfigA
if [ $? -ne 0 ]; then
  echo ""
  echo "ERROR: Failed ConfigA tests; see above"
  echo "Shutting down Tomcat..."
  $CATALINA_HOME/bin/shutdown.sh
  sleep 5
  mv $FEDORA_HOME/server/logs $BUILD_HOME/build/server-logs/fedora.test.AllSystemTestsConfigA
  exit 1
fi
echo "Shutting down tomcat..."
$CATALINA_HOME/bin/shutdown.sh
sleep 5
mv $FEDORA_HOME/server/logs $BUILD_HOME/build/server-logs/fedora.test.AllSystemTestsConfigA

$CATALINA_HOME/bin/startup.sh
if [ $? -ne 0 ]; then
  echo ""
  echo "ERROR: Failed while starting Fedora for ConfigA tests; see above"
  exit 1
fi
echo "Waiting 20 seconds for Fedora to start..."
sleep 20
echo ""
echo "[Running ConfigC Tests...]"
$SCRIPTDIR/systest.sh $1 -Dtest=fedora.test.AllSystemTestsConfigC
if [ $? -ne 0 ]; then
  echo ""
  echo "ERROR: Failed ConfigC tests; see above"
  echo "Shutting down Tomcat..."
  $CATALINA_HOME/bin/shutdown.sh
  sleep 5
  mv $FEDORA_HOME/server/logs $BUILD_HOME/build/server-logs/fedora.test.AllSystemTestsConfigC
  exit 1
fi
echo "Shutting down tomcat..."
$CATALINA_HOME/bin/shutdown.sh
sleep 5
mv $FEDORA_HOME/server/logs $BUILD_HOME/build/server-logs/fedora.test.AllSystemTestsConfigC

#
# End of Config A and C Tests
#

echo ""
echo "===================================="
echo "Completed sanity tests successfully!"
echo "===================================="

