@echo off
set CLASSPATH=%FEDORA_HOME%\tomcat41\webapps\fedora\WEB-INF\classes
java -Dfedora.home=%FEDORA_HOME% fedora.server.Server %1 %2 %3 %4 %5 %6 %7 %8 %9