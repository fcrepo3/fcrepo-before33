@echo off

if "%FEDORA_HOME%" == "" goto envErr

if not exist %FEDORA_HOME%\config\fedora.fcfg goto configNotFound

echo Starting Fedora server...

set TC=%FEDORA_HOME%\tomcat41
java -cp %TC%\bin\bootstrap.jar -Dfedora.home=%FEDORA_HOME% -Dclasspath=%TC%\bin\bootstrap.jar -Djava.endorsed.dirs=%TC%\bin -Djava.security.manager -Djava.security.policy=%TC%\conf\catalina.policy -Dcatalina.base=%TC% -Dcatalina.home=%TC% -Djava.io.tmpdir=%TC%\temp org.apache.catalina.startup.Bootstrap start

goto end

:envErr
echo ERROR: Environment variable, FEDORA_HOME must be set.
goto end

:configNotFound
echo ERROR: FEDORA_HOME does not appear correctly set.
echo Configuration cannot be found at %FEDORA_HOME%\config\fedora.fcfg
goto end

:end