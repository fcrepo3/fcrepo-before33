        ********************************************************
        * Installation Instructions for Fedora v1.2.1 Patch #1 *
        ********************************************************

About this patch
-----------------
  This patch fixes a potentially damaging bug* in the Fedora server,
  version 1.2.1 that can cause the unintended loss of managed datastreams 
  while adding datastreams to unrelated digital objects.

  * Bug #47: http://www.fedora.info/bugzilla/show_bug.cgi?id=47

Applying the patch
------------------
  1) Make sure you are running v1.2.1 of the Fedora server software.
     ** DO NOT APPLY THIS PATCH TO ANY OTHER VERSION OF FEDORA! **
     To check your repository's version, check the "describe" page.
     For instance, if you're running Fedora on localhost:8080,
     visit http://localhost:8080/fedora/describe

  2) Shut down your Fedora server (using fedora-stop)

  3) Make a backup of the following file:
     %FEDORA_HOME%\server\tomcat41\webapps\fedora\WEB-INF\classes\fedora\server\storage\replication\DefaultDOReplicator.class

  4) Copy the DefaultDOReplicator.class from this patch into the
     above directory, OVER THE EXISTING FILE.

  5) Re-start your Fedora server.

  6) (OPTIONAL) If you installed the source code distribution of
     Fedora, you should also copy the included DefaultDOReplicator.java
     file into your src\java\fedora\server\storage\replication
     directory, OVER THE EXISTING FILE.
