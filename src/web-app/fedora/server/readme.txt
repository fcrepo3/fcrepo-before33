notes on tomcat web.xml security-constraints
this is hard-to-find info, either on web or in books

no <auth-constraint/> at all
	doesn't care about credentials if they are present
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


The following was taken from
http://www.roguewave.com/support/docs/leif/leif/html/bobcatug/7-3.html
does bobcat handle url-patterns like tomcat?

url-patterns
1. URL patterns use an extremely simple syntax. 
2. Every character in a pattern must match the corresponding character in the URL path exactly, 
with two exceptions. 
3. At the end of a pattern, /* matches any sequence of characters from that point forward. 
4. The pattern *.extension matches any file name ending with extension. 
5. No other wildcards are supported.
6. An asterisk at any other position in the pattern is not a wildcard.

filters
1. Different filters in a single context often use the same url-pattern. 
2. In this case, each filter that matches the request may process the request.

servlet-mappings
1. No two servlet-mapping elements in the same application may use the same url-pattern. 
2. If the web.xml file contains two identical mappings to different servlets, the container 
makes no guarantees about which servlet the container calls for a given request. 
3. Two servlets may use overlapping url-pattern elements.  The matching procedure determines 
which servlet the container calls.

servlet matching procedure
1. A request may match more than one servlet-mapping in a given context. 
2. The container uses a straightforward matching procedure to determine the best match. 
3. The matching procedure has four simple rules. 

servlet matching rules
1. The container prefers an exact path match over a wildcard path match. 
2. The container prefers to match the longest pattern. 
3. The container prefers path matches over filetype matches.
4. The pattern <url-pattern>/</url-pattern> always matches any request that no other pattern matches.


http://e-docs.bea.com/wls/docs61/webapp/web_xml.html
