-------------------------------------------------------------------
             Fedora Release 3.2.1 - Summer 2009
-------------------------------------------------------------------
This is a full source code release of Fedora.  Before using this
software, you must read and agree to the license, found under
license/license.html.  Documentation can be found for online
browsing or download at http://fedora-commons.org/go/fcr30

Building Fedora
===============
To build the executable installer, make sure you have maven2
installed and enter the following:

   1. Until dependencies are found in a public repository, 
      the following script needs to be run
          * ./resources/scripts/installLibs.sh
   2. mvn install -P fedora-installer
          * generates fedora-installer.jar
          * found in /installer/target
  

Running Unit Tests
=====================

  mvn install -Dintegration.test.skip=true

Running System Tests
====================
The system tests consist of functional "black box" tests to be
executed against a running Fedora server.  These tests are divided
into top-level suites, where each suite is intended to be run with
the Fedora server configured in a specific way.

  [fedora.test.AllSystemTestsConfigA]
    When running this suite, the server should be configured
    with API-A authentication turned OFF.

  [fedora.test.AllSystemTestsConfigB]
    When running this suite, the server should be configured
    with API-A authentication turned ON, with the
    Resource Index, REST api, and Messaging modules enabled.
    
  [fedora.test.AllSystemTestsConfigQ]
    When running this suite, the server should be configured
    with the default options provided by 'quick install'.
    It can be used to verify the successful installation of 'quick install'.
    
These tests do not depend on external hosts and can therefore be
run without external network access.

To execute a test suite, make sure the server has been started[*] and 
that $FEDORA_HOME points to the correct directory. Then enter:

  mvn integration-test -P config[A|B|Q]

By default, each test will run using the demo objects in
FOXML format.  To run the same tests using the demo objects
in METS, Atom, or Atom Zip format, add one of the following to 
the line above:

  -Ddemo.format=mets
  -Ddemo.format=atom
  -Ddemo.format=atom-zip

There are some system tests that are not included in the system
test suites due to the time required to execute the test,
the following tests fall into that category:

  [fedora.test.integration.TestLargeDatastreams]
    This test adds a 5GB datastream through API-M, then 
    retrieves it via API-A and API-A-Lite. When running 
    this test, the server should be configured to allow
    non-SSL access to API-M and API-A. This test has no
    dependencies on external hosts and can therefore be 
    run without external internet access.
  
  To run this test, make sure the server has been started[*] 
  Then enter:
    
    ant junit -Dtest=fedora.test.integration.TestLargeDatastreams 

Running Performance Tests
=========================
The performance tests provide information about the time required to
perform a variety of repository functions. This information can be used 
to determine the performance impact of various configuration choices.
To run these tests, make sure the server has been started[*]
Then enter: 

    ant performance-tests -Dhost=[HOST] -Dport=[PORT] 
                          -Dusername=[USERNAME] -Dpassword=[PASSWORD] 
                          -Diterations=[NUM-ITERATIONS] -Dthreads=[NUM-THREADS] 
                          -Dfile=[OUTPUT-FILE] -Dname=[TEST-NAME]  
    where

    [HOST] = Host on which the Fedora server is running
    [PORT] = Port on which the Fedora server APIs can be accessed
    [USERNAME] = A fedora user with administrative privileges
    [PASSWORD] = The fedora user's password
    [NUM-ITERATIONS] = The number of times to perform each operation
    [NUM-THREADS] = The number of threads to use in the thread pool
    [OUTPUT-FILE] = The full path to the file where test results will be written
    [TEST-NAME] = A name for this test run

[*] Normally, no additional setup is required when testing a
    Fedora server instance at http://localhost:8080/fedora/.  
    A different server port may be chosen with no consequence.    
    However, if the fedora server uses an alternate app
    server context (i.e. not /fedora), you must set the environment
    variable WEBAPP_NAME to the alternate context name.  This variable
    is used by command-line utilities.  System tests involving these 
    utiliteis may fail if WEBAPP_NAME is not set properly.
    Additionally, your test host is on a public IP and (not localhost), 
    you *may* need to manually edit the deny-apim-if-not-localhost.xml 
    policy before testing.
