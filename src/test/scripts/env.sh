#!/bin/bash

#
# CHANGE THESE SETTINGS AS APPROPRIATE FOR YOUR TEST ENVIRONMENT
# NOTE: You should also change the environment-specific setttings
#       in the Config*.properties files
#

# Where is JDK 1.5 installed?
JAVA5_HOME=/usr/lib/jvm/java-1.5.0-sun

# Where is JDK 1.6 installed?
JAVA6_HOME=/usr/lib/jvm/java-6-sun

# Where is ant installed?
ANT_HOME=/usr/share/ant

# Where is the Fedora source distribution to be tested?
BUILD_HOME=$HOME/work/fedora/trunk

# Where should test instances of Fedora be installed?
# This will be created and cleared out by test scripts as necessary.
FEDORA_HOME=$HOME/fedora/home

# When installed, what port will non-secure http requests be on?
HTTP_PORT=9080

#
# DON'T CHANGE BELOW THIS LINE
#
CATALINA_HOME=$FEDORA_HOME/tomcat

export ANT_HOME
export FEDORA_HOME
export CATALINA_HOME
