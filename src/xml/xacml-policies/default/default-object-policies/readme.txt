1. What is OBJECT-POLICIES-DIRECTORY?
OBJECT-POLICIES-DIRECTORY refers to the file system directory used to hold per-object authorization policies.  
The directory which serves this function is configured in fedora.fcfg, using the Authorization module parameter of the same name.  
OBJECT-POLICIES-DIRECTORY is an arbitrary file system location, readable by Fedora server, and written to by Fedora server 
on first use of an installation.  (There are a few restrictions on OBJECT-POLICIES-DIRECTORY placement, as described below.)


2. How is OBJECT-POLICIES-DIRECTORY created and initially populated?
OBJECT-POLICIES-DIRECTORY is created if it doesn't already exist when Fedora server starts.    
Further, if a subdirectory OBJECT-POLICIES-DIRECTORY/default doesn't already exist when Fedora server starts, it is created and any 
default per-object policies which come shipped with Fedora are copied into that subdirectory.  (With Fedora 2.1, there are -no- default per-object policies.)
This subdirectory creation and optional policy copying usually occurs only the first time you run Fedora server after installing it.  
You can edit or delete these policies as explained below. 
OBJECT-POLICIES-DIRECTORY is not prepopulated by Fedora with policies directly under that directory itself.  This is because
this is where you add, and later edit or delete, per-object policies of your -own- choosing, as explained below.


3. Do I need to create OBJECT-POLICIES-DIRECTORY myself?
No.  You -could- create the OBJECT-POLICIES-DIRECTORY directory yourself before you first run Fedora server.  This 
would, e.g., facilitate your putting your own per-object policies in place immediately.  In practice, this is unnecessary, as 
you will likely go though a period of system tuning before production use.  Creating this directory yourself is -not- recommended.


4. Should I create OBJECT-POLICIES-DIRECTORY/default myself?
No.  If you yourself create the subdirectory OBJECT-POLICIES-DIRECTORY/default, this will prevent Fedora server from 
initializing properly with any default per-object policies.  It is best to let Fedora do this initialization, which you 
can then adjust by optionally editing or deleting default policies. 


5. I see directories with default policies in Fedora's src and dist trees.  Why can't I simply point OBJECT-POLICIES-DIRECTORY into 
one of those directories? 
Default per-object policies have backing copies in Fedora binary distributions in the Fedora dist tree, and have yet other copies 
in Fedora source distributions in the src tree, respectively in the two directories: 
.../dist/server/fedora-internal-use/fedora-internal-use-object-policies and
.../src/xml/xacml-policies/default/default-object-policies.
The contents of these two directories serve to build and initialize a Fedora installation, and are -not- intended
to be changed in any way at user installations (no file additions, nor edits, nor deletions).  

Pointing OBJECT-POLICIES-DIRECTORY into the src or dist trees could lead to editing the backing copies in order to configure 
policies actually in play.  To be clear, make changes -only- in the OBJECT-POLICIES-DIRECTORY directory as configured in fedora.fcfg:
the backing copies should be kept distinct and should -not- be changed.


6. Can I locate OBJECT-POLICIES-DIRECTORY somewhere else under FEDORA_HOME?
Placing OBJECT-POLICIES-DIRECTORY -anywhere- under FEDORA_HOME is probably unwise, as changes to the policies it holds could be 
easily lost on later upgrade to a new Fedora version.  We recommend placing OBJECT-POLICIES-DIRECTORY outside of the FEDORA_HOME tree.


7. How do I add a new (non-default) per-object policy?
If you want to -add- your own per-object policies, do so directly in the OBJECT-POLICIES-DIRECTORY directory,
i.e., -not- in the OBJECT-POLICIES-DIRECTORY/default directory.


8. Why can't I just add a new (non-default) per-object policy to the OBJECT-POLICIES-DIRECTORY/default directory?
Reserving OBJECT-POLICIES-DIRECTORY/default for -default- per-object policies will keep maintenance clean and tidy.


9. How do I change a default per-object policy?
[This entry is included in case Fedora releases after version 2.1 do come to include default per-object policies.]
If a default per-object policy needs adjustment for your local needs, you can edit any policy in OBJECT-POLICIES-DIRECTORY/default 
to achieve a similar but different effect.  Do this carefully only after cautious consideration and do this only in the 
directory OBJECT-POLICIES-DIRECTORY/default.


10. How do I remove a default per-object policy?
[This entry is included in case Fedora releases after version 2.1 do come to include default per-object policies.]
If a default per-object policy is simply inappropriate for your local needs, you can delete its policy file from 
OBJECT-POLICIES-DIRECTORY/default.    Do this carefully only after cautious consideration and do this only in the 
directory OBJECT-POLICIES-DIRECTORY/default.


11. Can I delete all of the default per-object policies?
[This entry is included in case Fedora releases after version 2.1 do come to include default per-object policies.]
If none of the default per-object policies make sense for your site, you can delete all of the default per-object policy files
from OBJECT-POLICIES-DIRECTORY/default.  Fedora will leave this as-is, as you maintained it.  However, do -not- delete
the OBJECT-POLICIES-DIRECTORY/default subdirectory itself, as this would cause Fedora to re-initialize default per-object policies 
on the next server (re-)start.


12. My changes have become confusing, and I want to start over.  How do I re-establish Fedora's per-object policy defaults?
[This entry is included in case Fedora releases after version 2.1 do come to include default per-object policies.]
If you delete OBJECT-POLICIES-DIRECTORY/default itself (contents -and- directory), Fedora will re-initialize default per-object 
policies on the next server (re-)start.

