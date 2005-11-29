
Installation Notes:

The fedora-service program is designed to install the Fedora server as
a Windows service.  This will make it so that when the machine on which
the server is to run is rebooted, the server will automatically be 
started in the background, without requiring a Fedora administrator to
log-in, and start the server.  Note that this program is only applicable
to installations of Fedora on machines running Windows 2000 or Windows XP.

To install the service un-zip this zip file into the FEDORA_HOME/server
directory.  There will then be four new batch files in the directory 
FEDORA_HOME/server/bin.   These batch files are as follows:

fedora-service-install    which will install Fedora so that it will
                          run at startup as a Windows service.  Note 
                          that when installing Fedora as a service
                          the environment variables JAVA_HOME and 
                          FEDORA_HOME must be set to the correct values.


fedora-service-start      which will manually start the Fedora server 
                          as a Windows service.  Note that you can also 
                          use the Windows services control panel to start
                          the Fedora server when it is first installed 
                          as a service.  Note also that subesequently
                          whenever the machine is rebooted, this service 
                          will start automatically.


fedora-service-stop       which will manually stop the Fedora server when
                          it is running as a Windows service.  Note that 
                          you can also use the Windows services control 
                          panel to stop the Fedora server when it is running
                          as a service.  Additionally the usual fedora-stop
                          command will stop the server as it normally does.


fedora-service-uninstall  which will uninstall Fedora from the Windows services
                          control panel, if you decide that you no longer want 
                          to run Fedora as a service on your machine.  Note that 
                          this will NOT stop the fedora server that is running,
                          and therefore you should first manually stop the service
                          before uninstalling it.


note that all four of these batch file simply check the environment settings, 
and execute the program    fedora-service.exe   passing one of several arguments.

