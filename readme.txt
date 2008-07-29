-------------------------------------------------------------------
              Fedora Release 3.0 - July 29, 2008
-------------------------------------------------------------------
This is a full source code release of Fedora.  Before using this
software, you must read and agree to the license, found under
license/license.html.  Documentation can be found under the 
src/doc/userdocs/ directory, or online at fedora-commons.org.

Building Fedora
===============
To build the executable installer, make sure you have ant 1.7
installed and enter the following:

  ant installer

Running Offline Tests
=====================
The offline tests consist of unit and integration tests,
and require no prior setup.  To execute the entire set of
offline tests, enter the following:

  ant junit

To execute only the unit tests:

  ant junit -Dtest=fedora.test.AllUnitTests

To execute only the integration tests:

  ant junit -Dtest=fedora.test.AllIntegrationTests

You can also execute all unit or integration tests on
a per-package level by running the "AllUnit/IntegrationTests"
suite residing in the package of interest.  For example, 
to run all unit tests in the fedora.server.journal package:

  ant junit -Dtest=fedora.server.journal.AllUnitTests

There are some offline tests that are not included in the 
offline test suites due to the time required to execute 
the test. These tests can be run by entering the following:

  ant junit -Dtest=fedora.client.messaging.TestMessagingClient

Running System Tests
====================
The system tests consist of functional "black box" tests to be
executed against a running Fedora server.  These tests are divided
into several top-level suites, where each suite is intended to
be run with the Fedora server configured in a specific way:

  [fedora.test.AllSystemTestsConfigA]
    When running this suite, the server should be configured
    with API-A authentication turned OFF.  This suite has no
    dependencies on external hosts and can therefore be run
    without external internet access.

  [fedora.test.AllSystemTestsConfigB]
    When running this suite, the server should be configured
    with API-A authentication turned ON, with the
    Resource Index, REST api, and Messaging modules enabled.  
    This suite can also be run without external internet access.

  [fedora.test.AllSystemTestsConfigC]
    When running this suite, the server should be installed
    on a host with external internet access and should also
    be network-reachable from outside your firewall, if any.
    Be sure to provide your specific hostname (and not just 
    "localhost") at install time.

To run any of these tests, make sure the server has been 
started[*] and that $FEDORA_HOME points to the correct directory.
Then enter:

  ant junit -Dtest=fedora.test.AllSystemTestsConfigB

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

[*] Normally, no additional setup is required when testing a
    Fedora server instance at localhost:8080.  However, if
    your test host is on a public IP, you *may* need to manually
    edit the deny-apim-if-not-localhost.xml policy before
    testing.  In addition, if you're not testing on
    localhost:8080, you should run the fedora-convert-demos 
    script before starting the tests.
