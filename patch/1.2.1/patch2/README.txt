        ********************************************************
        * Installation Instructions for Fedora v1.2.1 Patch #2 *
        ********************************************************

About this patch
-----------------
  This patch fixes a bug in the Fedora server, version 1.2.1 that 
  can affect migration of objects from the 1.2.1 repository.  In
  cases where objects contain URLs that are referential to the
  local host:port of the repository (i.e., repository self-
  referential URLs), these URLs should be modified on export to
  a placeholder string ("fedora.local.server") that replaces the 
  actual host:port of URLs relative to the repository.  This is
  so that the exported object can later be ingested into another
  Fedora repository in a manner that Fedora can recognize these
  URLs as being local to whatever repository the object resides.
  A bug in version 1.2.1 exported such URLs with the actual 
  host:port instead of the placeholder string.  This patch
  fixes this, so that when objects are ingested into another
  repository, relative URLs are properly maintained in the new
  repository.


Applying the patch
------------------
  1) Make sure you are running v1.2.1 of the Fedora server software.
     ** DO NOT APPLY THIS PATCH TO ANY OTHER VERSION OF FEDORA! **
     To check your repository's version, check the "describe" page.
     For instance, if you're running Fedora on localhost:8080,
     visit http://localhost:8080/fedora/describe

  2) Shut down your Fedora server (using fedora-stop)

  3) Make a backup of the following file:
     %FEDORA_HOME%\server\tomcat41\webapps\fedora\WEB-INF\classes\fedora\server\storage\translation\METSLikeExportDOSerializer.class

  4) Copy the METSLikeExportDOSerializer.class from this patch into the
     above directory, OVER THE EXISTING FILE.

  5) Re-start your Fedora server.

  6) (OPTIONAL) If you installed the source code distribution of
     Fedora, you should also copy the included METSLikeExportDOSerializer.java
     file into your src\java\fedora\server\storage\translation
     directory, OVER THE EXISTING FILE.
