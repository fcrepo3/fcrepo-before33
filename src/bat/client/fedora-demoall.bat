@echo off

goto checkEnv
:envOk

set OLD_JAVA_HOME=%JAVA_HOME%
set JAVA_HOME=%THIS_JAVA_HOME%

:runMinimized
rem bdefs
%JAVA_HOME%\bin\java -Xms64m -Xmx96m -cp %FEDORA_HOME%\client;%FEDORA_HOME%\client\client.jar -Dfedora.home=%FEDORA_HOME% fedora.client.ingest.AutoIngestor %1 %2 %3 %4 %FEDORA_HOME%\demo\bdef-mrsid-image-picksize.xml "first import"
%JAVA_HOME%\bin\java -Xms64m -Xmx96m -cp %FEDORA_HOME%\client;%FEDORA_HOME%\client\client.jar -Dfedora.home=%FEDORA_HOME% fedora.client.ingest.AutoIngestor %1 %2 %3 %4 %FEDORA_HOME%\demo\bdef-mrsid-image.xml "first import"
%JAVA_HOME%\bin\java -Xms64m -Xmx96m -cp %FEDORA_HOME%\client;%FEDORA_HOME%\client\client.jar -Dfedora.home=%FEDORA_HOME% fedora.client.ingest.AutoIngestor %1 %2 %3 %4 %FEDORA_HOME%\demo\bdef-simple-image.xml "first import"

rem bmechs
%JAVA_HOME%\bin\java -Xms64m -Xmx96m -cp %FEDORA_HOME%\client;%FEDORA_HOME%\client\client.jar -Dfedora.home=%FEDORA_HOME% fedora.client.ingest.AutoIngestor %1 %2 %3 %4 %FEDORA_HOME%\demo\bmech-mrsid-image-picksize.xml "first import"
%JAVA_HOME%\bin\java -Xms64m -Xmx96m -cp %FEDORA_HOME%\client;%FEDORA_HOME%\client\client.jar -Dfedora.home=%FEDORA_HOME% fedora.client.ingest.AutoIngestor %1 %2 %3 %4 %FEDORA_HOME%\demo\bmech-mrsid-image.xml "first import"
%JAVA_HOME%\bin\java -Xms64m -Xmx96m -cp %FEDORA_HOME%\client;%FEDORA_HOME%\client\client.jar -Dfedora.home=%FEDORA_HOME% fedora.client.ingest.AutoIngestor %1 %2 %3 %4 %FEDORA_HOME%\demo\bmech-simple-image.xml "first import"
%JAVA_HOME%\bin\java -Xms64m -Xmx96m -cp %FEDORA_HOME%\client;%FEDORA_HOME%\client\client.jar -Dfedora.home=%FEDORA_HOME% fedora.client.ingest.AutoIngestor %1 %2 %3 %4 %FEDORA_HOME%\demo\bmech-sizer-image.xml "first import"

rem regular objects (demonstrating different bdefs/mechs)
%JAVA_HOME%\bin\java -Xms64m -Xmx96m -cp %FEDORA_HOME%\client;%FEDORA_HOME%\client\client.jar -Dfedora.home=%FEDORA_HOME% fedora.client.ingest.AutoIngestor %1 %2 %3 %4 %FEDORA_HOME%\demo\obj-mrsid-image-picksize.xml "first import"
%JAVA_HOME%\bin\java -Xms64m -Xmx96m -cp %FEDORA_HOME%\client;%FEDORA_HOME%\client\client.jar -Dfedora.home=%FEDORA_HOME% fedora.client.ingest.AutoIngestor %1 %2 %3 %4 %FEDORA_HOME%\demo\obj-mrsid-image.xml "first import"
%JAVA_HOME%\bin\java -Xms64m -Xmx96m -cp %FEDORA_HOME%\client;%FEDORA_HOME%\client\client.jar -Dfedora.home=%FEDORA_HOME% fedora.client.ingest.AutoIngestor %1 %2 %3 %4 %FEDORA_HOME%\demo\obj-simple-image.xml "first import"
%JAVA_HOME%\bin\java -Xms64m -Xmx96m -cp %FEDORA_HOME%\client;%FEDORA_HOME%\client\client.jar -Dfedora.home=%FEDORA_HOME% fedora.client.ingest.AutoIngestor %1 %2 %3 %4 %FEDORA_HOME%\demo\obj-sizer-image.xml "first import"

rem regular objects (demonstrating different dc ingest scenarios)
%JAVA_HOME%\bin\java -Xms64m -Xmx96m -cp %FEDORA_HOME%\client;%FEDORA_HOME%\client\client.jar -Dfedora.home=%FEDORA_HOME% fedora.client.ingest.AutoIngestor %1 %2 %3 %4 %FEDORA_HOME%\demo\obj-nolabel-nodc.xml "first import"
%JAVA_HOME%\bin\java -Xms64m -Xmx96m -cp %FEDORA_HOME%\client;%FEDORA_HOME%\client\client.jar -Dfedora.home=%FEDORA_HOME% fedora.client.ingest.AutoIngestor %1 %2 %3 %4 %FEDORA_HOME%\demo\obj-nolabel-15dc.xml "first import"
%JAVA_HOME%\bin\java -Xms64m -Xmx96m -cp %FEDORA_HOME%\client;%FEDORA_HOME%\client\client.jar -Dfedora.home=%FEDORA_HOME% fedora.client.ingest.AutoIngestor %1 %2 %3 %4 %FEDORA_HOME%\demo\obj-nolabel-30dc.xml "first import"
%JAVA_HOME%\bin\java -Xms64m -Xmx96m -cp %FEDORA_HOME%\client;%FEDORA_HOME%\client\client.jar -Dfedora.home=%FEDORA_HOME% fedora.client.ingest.AutoIngestor %1 %2 %3 %4 %FEDORA_HOME%\demo\obj-label-nodc.xml "first import"
%JAVA_HOME%\bin\java -Xms64m -Xmx96m -cp %FEDORA_HOME%\client;%FEDORA_HOME%\client\client.jar -Dfedora.home=%FEDORA_HOME% fedora.client.ingest.AutoIngestor %1 %2 %3 %4 %FEDORA_HOME%\demo\obj-label-15dc.xml "first import"
%JAVA_HOME%\bin\java -Xms64m -Xmx96m -cp %FEDORA_HOME%\client;%FEDORA_HOME%\client\client.jar -Dfedora.home=%FEDORA_HOME% fedora.client.ingest.AutoIngestor %1 %2 %3 %4 %FEDORA_HOME%\demo\obj-label-30dc.xml "first import"

set JAVA_HOME=%OLD_JAVA_HOME%

goto end

:checkEnv
if "%FEDORA_HOME%" == "" goto noFedoraHome
if not exist %FEDORA_HOME%\client\client.jar goto clientNotFound
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

:clientNotFound
echo ERROR: FEDORA_HOME does not appear correctly set.
echo Client cannot be found at %FEDORA_HOME%\client\client.jar
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

