some help with xacml


OASIS
http://docs.oasis-open.org/xacml/access_control-xacml-2_0-core-spec-cd-04.pdf 
this is a spec and a good reference for identifiers.  

Other documents general to XACML are available at the homepage:
http://www.oasis-open.org/committees/tc_home.php?wg_abbrev=xacml


SUN
Specific to the Sun Java reference implementation is the programmer's guide at
http://sunxacml.sourceforge.net/guide.html 
and the Javadocs at 
http://sunxacml.sourceforge.net/javadoc/index.html  
The project's homepage is at http://sunxacml.sourceforge.net/


XACML GOTCHAS
xacml provides attribute values for target evaluation as single values, 
but provides them for condition evaluation as "bags" (sets), even so 
for either singleton or empty bags.  Code policies accordingly.

In Targets, multiple <Subject> elements are logically or-ed.  
Multiple <SubjectMatch> are logically and-ed.  MatchId functions which 
can be used in Targets are much restricted compared to those available
for use in Condition FunctionIds. 

<Subjects>, <Actions>, <Resources>, and <Environments> in <Target>s behave
similarly, in regard to the previous paragraph.  Except . . .
despite statements that <Environments> was added to <Target> generally, 
it doesn't seem to work under sunxacml.


FEDORA

For now, the Fedora-specific identifiers to use in policies can be found 
in fedora.server.security.Authorization.java 
or derived from fedora.common.Constants and the classes under fedora.common.rdf

to-do:  provide this list

To activate policies, 
copy them from example-repository-policies into repository-policies
or from example-object-policies into object-policies, renaming appropriately.

An object policy named demo-5.xml in that directory will be included in 
evaluating authz for Fedora object demo:5  Or put the object policy in 
the object's datastream named "POLICY".  It is good practice with object
policy's to include a check of the pid in the policy:  if the policy mistakenly
gets put into repository-policies, it has the same effect.

Duplicate and edit as needed to create your own policy mix.
Changes to repository-policies requires a server restart.
Use MSIE or an XML editor to check well-formedness after editing.
XML which violates the xacml schema might not show up until Fedora attempts to 
load the policy.

The example policies are crafted to be used together, and are of two broad types:
1. "positive" policies can only permit authz
2. "negative" policies can only deny authz

For a request to succeed authz, 
	1. at least one positive policy must match the request
	2. no negative can match the request
	3. no policy evaluations can return indeterminate 
		(an attribute was missing or an error prevented the evaluation)
	4. no policy evaluations can return an unanticipated result 
	5. some policy evaluations can return a notApplicable result
		(all needed attributes were present, but the policy's target wasn't matched)
	
	Because 3. severely cramps policy writing, Fedora xacml code always supplies an 
	attribute value (e.g., "" if otherwise absent).

Otherwise, the request fails authz.
