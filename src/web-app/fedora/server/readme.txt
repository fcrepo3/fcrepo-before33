notes on tomcat web.xml security-constraints
this is hard-to-find into, either on web or in books

no <auth-constraint/> at all
	doesn't care about credentials if present
	no challenge if credentials absent
	no container authn or authz

<auth-constraint> (mere presence regardless of contents) 
	requires authn; i.e., server challenges client if http request is w/o credentials
	container authz:  subject matches if it matches -any- of the <auth-constraint>'s <role-name>s;
	otherwise subject doesn't match == failed container authz

<auth-constraint/>, i.e., it contains no <role-name>s
	challenged if http request omits credentials
	matches no subject, w/ or w/o roles
	i.e., this is always a failed container authz, regardless of subject attributes

<role-name></role-name>
	matches subject w/o any roles at all
	doesn't not match a subject with some role, whatever it is

<role-name>*</role-name>
	matches any subject whatever its role(s) or with no roles
	but does require authn, because it is inside <auth-constraint>

order of <role-name>s in <auth-constraint> doesn't matter
these are logically or-ed

so . . .

<auth-constraint><role-name></role-name></auth-constraint>
	challenged if http request omits credentials
	matches subjects w/o any roles at all	
	(so or's with other role-names)

<auth-constraint><role-name>*</role-name></auth-constraint>
	challenged if http request omits credentials
	matches any role or no role
	matches any subject, even those without roles
	but does require authn

presence of the 2nd and 3rd <role-name>s doesn't change effect; 2nd doesn't increase coverage, 3rd doesn't hurt
<auth-constraint>
	<role-name>*</role-name>
	<role-name>administrator</role-name>
	<role-name></role-name>
</auth-constraint>


