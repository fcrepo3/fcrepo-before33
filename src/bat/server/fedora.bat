@echo off
set CLASSPATH=%FEDORA_HOME%\tomcat41\webapps\fedora\WEB-INF\classes;%FEDORA_HOME%\tomcat41\common\lib\xerces2-2.0.2.jar
java -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Dfedora.home=%FEDORA_HOME% fedora.server.Server %1 %2 %3 %4 %5 %6 %7 %8 %9