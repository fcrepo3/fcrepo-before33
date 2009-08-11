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

echo "================================================"
echo "Compiling distribution and running unit tests..."
echo "================================================"
echo ""
cd $BUILD_HOME
$M2_HOME/bin/mvn clean install

if [ $? -ne 0 ]; then
  echo ""
  echo "ERROR: Failed to compile and unit-test distribution; see above"
  exit 1
fi

echo ""
echo "============================"
echo "Building Fedora Installer..."
echo "============================"
echo ""
$M2_HOME/bin/mvn install -P fedora-installer -Dmaven.test.skip=true -Dintegration.test.skip=true

if [ $? -ne 0 ]; then
  echo ""
  echo "ERROR: Failed to build installer; see above"
  exit 1
fi

echo ""
echo "==========================="
echo "Running sanity system tests"
echo "==========================="
echo ""

# Where to put server log artifacts after each sys test
mkdir -p $BUILD_HOME/build/server-logs

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

cd $BUILD_HOME/integrationtest
$M2_HOME/bin/mvn integration-test -P configB -Dfedora.baseURL=http://fedcommdevsrv1.nsdlib.org:9080/fedora
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
# Config A Tests
#

$SCRIPTDIR/install-fedora.sh $1 ConfigA.properties

if [ $? -ne 0 ]; then
  echo ""
  echo "ERROR: Failed while installing Fedora for ConfigA tests; see above"
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

cd $BUILD_HOME/integrationtest
$M2_HOME/bin/mvn integration-test -P configA -Dfedora.baseURL=http://fedcommdevsrv1.nsdlib.org:9080/fedora
#$SCRIPTDIR/systest.sh $1 -Dtest=fedora.test.AllSystemTestsConfigA -Dfedora.port=9080 -Dfedora.hostname=fedcommdevsrv1.nsdlib.org
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

#
# End of Config A Tests
#

echo ""
echo "===================================="
echo "Completed sanity tests successfully!"
echo "===================================="

