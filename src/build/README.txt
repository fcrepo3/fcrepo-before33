To import these settings into Eclipse (3.3):

1) Go to "Window" -> "Preferences" -> "Java" -> "Code Style"
2) Under the top "Code Style" section:
   - enter "m_" for the prefix list for Fields, 
     and leave others blank
   - Check "Add @Override ...", and leave other checkboxes
     unchecked
   - Make sure "Exception variable name" is e
3) Import fedora-cleanup.xml under "Clean Up"
4) Replace "Firstname Lastname" with your own name in fedora-codetemplates.xml
4) Import fedora-codetemplates.xml under "Code Templates"
5) Import fedora-formatter.xml under "Formatter"
6) Import fedora.importorder under "Organize Imports"