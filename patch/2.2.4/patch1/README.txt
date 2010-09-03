        ********************************************************
        * Installation Instructions for Fedora v2.2.4 Patch #1 *
        ********************************************************

About this patch
-----------------
  This patch fixes a potentially damaging bug* in the Fedora server,
  version 2.2.4, that can result in errors if a FOXML file with an
  object label contains the \ escape character.

  * FCREPO-789: https://jira.duraspace.org/browse/FCREPO-789

Applying the patch
------------------
  1) Make sure you are running v2.2.4 of the Fedora server software.
     ** DO NOT APPLY THIS PATCH TO ANY OTHER VERSION OF FEDORA! **
     To check your repository's version, check the "describe" page.
     For instance, if you're running Fedora on localhost:8080,
     visit http://localhost:8080/fedora/describe

  2) Shut down your Fedora server

  3) Make a backup of the following files:
     %CATALINA_HOME%/webapps/fedora/WEB-INF/classes/fedora/server/storage/DefaultDOManager.class
     %CATALINA_HOME%/webapps/fedora/WEB-INF/classes/fedora/server/utilities/rebuild/SQLRebuilder.class

  4) Copy the SQLRebuilder.class from this patch into the directory
     %CATALINA_HOME%/webapps/fedora/WEB-INF/classes/fedora/server/storage/
     OVER THE EXISTING FILE

  5) Copy the DefaultDOManager.class from this patch into the directory
     %CATALINA_HOME%/webapps/fedora/WEB-INF/classes/fedora/server/utilities/rebuild/
     OVER THE EXISTING FILE

  6) Re-start your Fedora server.

  7) (OPTIONAL) If you installed the source code distribution of
     Fedora, you should also copy the included .java
     files into your copy of the source files:
     /src/java/fedora/server/storage/DefaultDOManager.java
     /src/java/fedora/server/storage/SQLRebuilder.java
     OVER THE EXISTING FILES
