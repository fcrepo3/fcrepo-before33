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
5. not tested in either authenticating scenario


JAASMemoryLoginModule
	I couldn't make the same-named login module from Tomcat work as delivered.  So I wrote another, 
subclassing from MemoryRealm.  We should try the Tomcat version again, in case I was wrong or our Tomcat 
upgrade brought a fix. 
		