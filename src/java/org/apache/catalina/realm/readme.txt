realms 
    /* these realm methods are called in the given order in tomcat 5.0:
     * 1. findSecurityConstraints
     * 2. hasUserDataPermission
     * 3. authenticate
     * 4. hasResourcePermission
     */



JAASRealm

tomcat source passed along a GenericPrincipal found among subjects returned by login modules,
effectively ignoring any other principals found and so making login modules returning Generic Principals 
(those shipped with tomcat) -not- work cooperatively with other login modules.

GenericPrincipal is so named because the same principal instance conveys subject data and roles

JAASRealm code changed so that JAASMemoryLoginModule (or any login module returning
Generic Principal) plays correctly with other login modules

this change fixed problems which I had mistakenly thought were in JAASMemoryLoginModule, so I deleted 
that replacement class from Fedora distribution (tomcat version of JAASMemoryLoginModule works with 
the changed JAASRealm)
(This may have been fixed in a later version of JAASRealm)

also changed JAASRealm to include password in the single created principal passed along to tomcat.
The existing code nulled that out, whenever a principal is created from various login modules.  
(As opposed to when it passed along intact a GenericPrincipal -- this simple short-circuiting now never 
happens, GenericPrincipal is no longer handled deferentially by JAASRealm.)

instanceof tests
	GenericPrincipal, IdPasswordPrincipal:  how to extend
		



