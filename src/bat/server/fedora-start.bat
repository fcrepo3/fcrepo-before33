@echo off

goto checkEnv
:envOk

echo Starting Fedora server...

set TC=%FEDORA_HOME%\tomcat41
set OLD_JAVA_HOME=%JAVA_HOME%
set JAVA_HOME=%THIS_JAVA_HOME%
start /B %JAVA_HOME%\bin\java -Xms64m -Xmx96m -cp %TC%\bin\bootstrap.jar -Dfedora.home=%FEDORA_HOME% -Dclasspath=%TC%\bin\bootstrap.jar -Djava.endorsed.dirs=%TC%\bin -Djava.security.manager -Djava.security.policy=%TC%\conf\catalina.policy -Dcatalina.base=%TC% -Dcatalina.home=%TC% -Djava.io.tmpdir=%TC%\temp org.apache.catalina.startup.Bootstrap start
set JAVA_HOME=%OLD_JAVA_HOME%

			<arg path="inc/server/tomcat41/common/lib/axis.jar;inc/server/tomcat41/common/lib/commons-logging.jar;inc/server/tomcat41/common/lib/jaxrpc.jar;inc/server/tomcat41/common/lib/wsdl4j.jar;inc/server/tomcat41/common/lib/tt-bytecode.jar;inc/server/tomcat41/common/lib/saaj.jar;inc/server/tomcat41/common/lib/commons-discovery.jar;dist/server/tomcat41/webapps/fedora/WEB-INF/classes"/>
			<arg value="org.apache.axis.client.AdminClient"/>
			<arg value="-lhttp://localhost:8080/fedora/AdminService"/>
			<arg value="dist/server/config/deployAPI-A.wsdd"/>
   
echo FIXME: insert deployment here

echo Standard output is being directed to %FEDORA_HOME%\mckoi094\stdout.log
echo To stop the server, use fedora-stop.

goto end

:checkEnv
if "%FEDORA_HOME%" == "" goto noFedoraHome
if not exist %FEDORA_HOME%\config\fedora.fcfg goto configNotFound
if "%FEDORA_JAVA_HOME%" == "" goto tryJavaHome
set THIS_JAVA_HOME=%FEDORA_JAVA_HOME%
:checkJava
if not exist %THIS_JAVA_HOME%\bin\java.exe goto noJavaBin
if not exist %THIS_JAVA_HOME%\bin\orbd.exe goto badJavaVersion
goto envOk

:tryJavaHome
echo Warning: FEDORA_JAVA_HOME not set, falling back to JAVA_HOME
if "%JAVA_HOME%" == "" goto noJavaHome
set THIS_JAVA_HOME=%JAVA_HOME%
goto checkJava

:noFedoraHome
echo ERROR: Environment variable, FEDORA_HOME must be set.
goto end

:configNotFound
echo ERROR: FEDORA_HOME does not appear correctly set.
echo Configuration cannot be found at %FEDORA_HOME%\config\fedora.fcfg
goto end

:noJavaHome
echo ERROR: FEDORA_JAVA_HOME was not defined, nor was (the fallback) JAVA_HOME.
goto end

:noJavaBin
echo ERROR: java.exe was not found in %THIS_JAVA_HOME%
echo Make sure FEDORA_JAVA_HOME or JAVA_HOME is set correctly.
goto end

:badJavaVersion
echo ERROR: java was found in %THIS_JAVA_HOME%, but it was not version 1.4
echo Make sure FEDORA_JAVA_HOME or JAVA_HOME points to a 1.4JRE/JDK base.
goto end

:end

