        ********************************************************
        * Installation Instructions for Fedora v2.2.4 Patch #1 *
        ********************************************************

About this patch
-----------------
  This patch fixes potential Denial-of-Service vulnerabilites in
  the Fedora server, version 2.2.4.  It updates the Fedora server
  to version 2.2.5.

  * FCREPO-789: https://jira.duraspace.org/browse/FCREPO-789
  * FCREPO-798: https://jira.duraspace.org/browse/FCREPO-798

Applying the patch to your repository
-------------------------------------
  1) Make sure you are running v2.2.4 of the Fedora server software.
     ** DO NOT APPLY THIS PATCH TO ANY OTHER VERSION OF FEDORA! **
     To check your repository's version, check the "describe" page.
     For instance, if you're running Fedora on localhost:8080,
     visit http://localhost:8080/fedora/describe

  2) Shut down your Fedora server

  3) Make a backup of the following folder:
     %CATALINA_HOME%/webapps/fedora/WEB-INF/classes

  4) Copy the file classes.tar.gz to the folder
     %CATALINA_HOME%/webapps/fedora/WEB-INF/classes

  5) Extract the files in this archive
     tar -xzf classes.tar.gz
     
     This will replace existing files with the patched versions)

  6) Re-start your Fedora server.
     
Applying the patch to your source code
--------------------------------------
If you have installed the source code distribution of Fedora, you should also
update your source with the patched source files.  You can update your source
to the release-2.2.5 tag in the source repository.  Alternatively you can patch
your source locally as follows:

  1) Make a backup of your existing source code
  
  2) Copy the file src.tar.gz to the src directory of your source code
  
  2) Extract this file:
     tar -xzf src.tar.gz
     