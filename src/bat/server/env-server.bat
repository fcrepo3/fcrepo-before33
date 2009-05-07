@echo off
setlocal

REM ---------------------------------------------------------------------------
REM Common environment checks and launcher for Fedora server scripts.
REM
REM Environment Variables:
REM   FEDORA_HOME  : Required.  Used to determine the location of misc
REM                  server resources required to run the utilities.
REM   CATALINA_HOME: Required.  Used to determine the location of server
REM                  classes required to run the utilities.
REM   JAVA_HOME    : Optional.  Used to determine the location of java.
REM                  If JAVA_HOME is unspecified, will use FEDORA_JAVA_HOME.
REM                  If FEDORA_JAVA_HOME is unspecified, will use java in PATH.
REM ---------------------------------------------------------------------------

if not "%WEBAPP_NAME%" == "" goto gotWebappName
set WEBAPP_NAME=fedora
:gotWebappName

if not "%FEDORA_HOME%" == "" goto gotFedoraHome
echo ERROR: The FEDORA_HOME environment variable is not defined.
exit /B 1
:gotFedoraHome

if not "%CATALINA_HOME%" == "" goto gotCatalinaHome
echo ERROR: The CATALINA_HOME environment variable is not defined.
exit /B 1
:gotCatalinaHome

if not "%JAVA_HOME%" == "" goto setJavaFromJavaHome
if not "%FEDORA_JAVA_HOME%" == "" goto setJavaFromFedoraJavaHome
set JAVA=java
goto gotJava
:setJavaFromFedoraJavaHome
set JAVA="%FEDORA_JAVA_HOME%\bin\java"
goto gotJava
:setJavaFromJavaHome
set JAVA="%JAVA_HOME%\bin\java"
:gotJava

set WEBINF="%CATALINA_HOME%\webapps\%WEBAPP_NAME%\WEB-INF"
set COMMON="%CATALINA_HOME%\common"
set CP="%WEBINF%\classes"
set OPTS=-Djava.endorsed.dirs="%WEBINF%\lib;%COMMON%\endorsed;%COMMON%\lib"
set OPTS=%OPTS% -Djavax.net.ssl.trustStore="%FEDORA_HOME%\server\truststore"
set OPTS=%OPTS% -Djavax.net.ssl.trustStorePassword=tomcat
set OPTS=%OPTS% -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl
set OPTS=%OPTS% -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl
set OPTS=%OPTS% -Dorg.apache.commons.logging.LogFactory=org.apache.commons.logging.impl.Log4jFactory
set OPTS=%OPTS% -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Log4jLogger
set OPTS=%OPTS% -Dcom.sun.xacml.PolicySchema="%FEDORA_HOME%\server\xsd\cs-xacml-schema-policy-01.xsd"
set OPTS=%OPTS% -Dfedora.home="%FEDORA_HOME%"

%JAVA% -server -Xmn64m -Xms256m -Xmx256m -cp %CP% %OPTS% %*