new JAASJNDILoginModule
1. extends JNDIRealm, locally altered as below 
	Tomcat does not know about the realm functionality subclassed here, 
	only about the JAASRealm which has JAASJNDILoginModule as one module
2. set superclass properties with these options with jaas.config:
	debug
	connectionURL
	userBase
	userSearch
	userSubtree
	userRoleName
	userPassword
3. userRoleName can have multiple (comma-separated) attribute names
4. interpret userPassword value
	a. "NO_AUTHENTICATION" == this login module provides a source for attributes only (UVa usage)
		its operation is conditional on success of an earlier module
		(earlier == order in jaas.config file)
		only contribute roles if an earlier module is successful 
		(earlier login module will return a subject)
		return only roles (not a subject)
	b. otherwise == this login module provides a source for both subject and additional attributes 
		its operation not conditional on success of an earlier module
		return subject and roles
5. JNDI not tested against another backend than LDAP
6. not tested in either authenticating scenario
	
	
JNDIRealm (changed from Tomcat source)
1. multiple attributes can now be specified
	separate attribute names with comma
2. roles returned are now attribute values prefixed with "name=", identifying the contributing attribute
3. authentication has been made optional
	userPassword == "NO_AUTHENTICATION" now interpreted as don't authenticate at all
	previous usage left intact
		which would allow a site to leverage benefits 1-2, yet with authentication by LDAP realm 
		userPassword not specified in jaas.config == use user credentials to authenticate to LDAP backend
		otherwise, userPassword taken as name of LDAP attribute which contains user password
			after fetch from backend, LDAP realm matches user credentials with that LDAP attribute
4. not tested as such, i.e., without subclassing to a login module
5. JNDI not tested against another backend than LDAP
6. not tested in either authenticating scenario


JAASRealm (changed from Tomcat source)
1. tomcat source passed along a GenericPrincipal found among subjects returned by login modules,
effectively ignoring any other principals found and so making login modules returning Generic Principals 
(those shipped with tomcat) -not- work cooperatively with other login modules.
2. (the only login module shipping with tomcat 5.0 is the JAASMemoryLoginModule.)
3. GenericPrincipal is so named because the same principal instance conveys subject data and roles
4. I changed JAASRealm code so that JAASMemoryLoginModule (or any login module returning
Generic Principal) plays correctly with other login modules
5. this change fixed problems which I had mistakenly thought were in JAASMemoryLoginModule, so I deleted 
that replacement class from Fedora distribution (tomcat version of JAASMemoryLoginModule works with 
the changed JAASRealm)
6. I also changed JAASRealm to include password in the single created principal passed along to tomcat.
The existing code nulled that out, whenever a principal is created from various login modules.  
(As opposed to when it passed along intact a GenericPrincipal -- this simple short-circuiting now never 
happens, GenericPrincipal is no longer handled deferentially by JAASRealm.)


		