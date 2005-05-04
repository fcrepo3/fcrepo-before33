@echo off

goto checkEnv
:envOk

echo checking xacml policy...

set TOMCAT_DIR=@tomcat.basename@
set TC=%FEDORA_HOME%\server\%TOMCAT_DIR%
set OLD_JAVA_HOME=%JAVA_HOME%
set JAVA_HOME=%THIS_JAVA_HOME%

if exist %FEDORA_HOME%\server\logs\startup.log goto logDirExists
mkdir %FEDORA_HOME%\server\logs > NUL

:logDirExists
if "%OS%" == "" goto runMinimized

:run
if "%1" == "" goto noFileError
"%JAVA_HOME%\bin\java" -cp %TC%\webapps\fedora\WEB-INF\classes;%TC%\common\lib\sunxacml.jar;\fedora\mellon\dist\server\jakarta-tomcat-5.0.28\common\lib\xercesImpl.jar;\fedora\mellon\dist\server\jakarta-tomcat-5.0.28\common\lib\xml-apis.jar -Dfedora.home=%FEDORA_HOME% -Dtomcat.dir=%TOMCAT_DIR%  -Dcom.sun.xacml.PolicySchema=%FEDORA_HOME%\server\xsd\cs-xacml-schema-policy-01.xsd  -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl  fedora.server.security.ValidatePolicy "%1"
goto finish

:noFileError
echo Error:  must supply filename.

:finish
set JAVA_HOME=%OLD_JAVA_HOME%

goto end

:checkEnv
if "%FEDORA_HOME%" == "" goto noFedoraHome
if not exist %FEDORA_HOME%\server\config\fedora.fcfg goto configNotFound
if "%FEDORA_JAVA_HOME%" == "" goto tryJavaHome
set THIS_JAVA_HOME=%FEDORA_JAVA_HOME%

:checkJava
if not exist "%THIS_JAVA_HOME%\bin\java.exe" goto noJavaBin
if not exist "%THIS_JAVA_HOME%\bin\orbd.exe" goto badJavaVersion
goto envOk

:tryJavaHome
if "%JAVA_HOME%" == "" goto noJavaHome
set THIS_JAVA_HOME=%JAVA_HOME%
goto checkJava

:noFedoraHome
echo ERROR: Environment variable, FEDORA_HOME must be set.
goto end

:configNotFound
echo ERROR: FEDORA_HOME does not appear correctly set.
echo Configuration cannot be found at %FEDORA_HOME%\server\config\fedora.fcfg
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

