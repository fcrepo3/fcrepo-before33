Installing from source:
  Unzip in C:\mellon
    If you choose a different directory, be sure to change
    the FEDORA_DEV value in initenv.bat appropriately.
  Run C:\mellon\initenv
    This sets your environment up for running/compiling the server.

Compiling:
  Go to C:\mellon and type "ant serverdist"
    This builds everything in C:\mellon\dist\server
    For other targets, see build.xml

Initial setup:
  Install the database (mysql, see installer at C:\mellon\res\)
  Start the database
  Run mySQLConfig (with no params to see usage)
  Change fedora.fcfg if needed (db host, user, pass)

Starting the server:
  Type fedora-start
    This starts the server.
    If it fails, type "fedora-stop fedoraAdmin"
      * fedoraAdmin is the default shutdown password

Preparing the demo:
  Type fedora-demoprep
    This installs a couple demo objects; a Behavior Definition
    object and a Behavior Mechanism object.