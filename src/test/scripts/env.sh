#!/bin/bash

#
# CHANGE THESE SETTINGS AS APPROPRIATE FOR YOUR TEST ENVIRONMENT
# NOTE: You should also change the environment-specific setttings
#       in the Config*.properties files
#

# Where is JDK 1.5 installed?
JAVA5_HOME=/usr/java/jdk1.5.0_16

# Where is JDK 1.6 installed?
JAVA6_HOME=/usr/local/jdk1.6.0_11

# Where is ant installed?
ANT_HOME=/usr/local/ant

# Where is the Fedora source distribution to be tested?
BUILD_HOME=$HOME/agent-home/xml-data/build-dir/FCREPO-LEGLINUXSAN

# Where should test instances of Fedora be installed?
# This will be created and cleared out by test scripts as necessary.
FEDORA_HOME=$HOME/fedora-home

# When installed, what port will non-secure http requests be on?
HTTP_PORT=9080

#
# DON'T CHANGE BELOW THIS LINE
#
CATALINA_HOME=$FEDORA_HOME/tomcat

export ANT_HOME
export FEDORA_HOME
export CATALINA_HOME
