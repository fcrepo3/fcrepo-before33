/*
 * Copyright 2001-2002,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 5.025
 */


package org.apache.catalina.realm;


import java.io.File;
import java.security.Principal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import javax.security.auth.Subject;
import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.CredentialExpiredException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.HttpRequest;
import org.apache.catalina.HttpResponse;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.deploy.SecurityConstraint;
import org.apache.catalina.util.StringManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fedora.server.MultiValueMap;
import fedora.server.security.ReducedPolicyEnforcementPoint;
import fedora.server.security.Transom;


/**
 * <p>Implmentation of <b>Realm</b> that authenticates users via the <em>Java
 * Authentication and Authorization Service</em> (JAAS).  JAAS support requires
 * either JDK 1.4 (which includes it as part of the standard platform) or
 * JDK 1.3 (with the plug-in <code>jaas.jar</code> file).</p>
 *
 * <p>The value configured for the <code>appName</code> property is passed to
 * the <code>javax.security.auth.login.LoginContext</code> constructor, to
 * specify the <em>application name</em> used to select the set of relevant
 * <code>LoginModules</code> required.</p>
 *
 * <p>The JAAS Specification describes the result of a successful login as a
 * <code>javax.security.auth.Subject</code> instance, which can contain zero
 * or more <code>java.security.Principal</code> objects in the return value
 * of the <code>Subject.getPrincipals()</code> method.  However, it provides
 * no guidance on how to distinguish Principals that describe the individual
 * user (and are thus appropriate to return as the value of
 * request.getUserPrincipal() in a web application) from the Principal(s)
 * that describe the authorized roles for this user.  To maintain as much
 * independence as possible from the underlying <code>LoginMethod</code>
 * implementation executed by JAAS, the following policy is implemented by
 * this Realm:</p>
 * <ul>
 * <li>The JAAS <code>LoginModule</code> is assumed to return a
 *     <code>Subject with at least one <code>Principal</code> instance
 *     representing the user himself or herself, and zero or more separate
 *     <code>Principals</code> representing the security roles authorized
 *     for this user.</li>
 * <li>On the <code>Principal</code> representing the user, the Principal
 *     name is an appropriate value to return via the Servlet API method
 *     <code>HttpServletRequest.getRemoteUser()</code>.</li>
 * <li>On the <code>Principals</code> representing the security roles, the
 *     name is the name of the authorized security role.</li>
 * <li>This Realm will be configured with two lists of fully qualified Java
 *     class names of classes that implement
 *     <code>java.security.Principal</code> - one that identifies class(es)
 *     representing a user, and one that identifies class(es) representing
 *     a security role.</li>
 * <li>As this Realm iterates over the <code>Principals</code> returned by
 *     <code>Subject.getPrincipals()</code>, it will identify the first
 *     <code>Principal</code> that matches the "user classes" list as the
 *     <code>Principal</code> for this user.</li>
 * <li>As this Realm iterates over the <code>Princpals</code> returned by
 *     <code>Subject.getPrincipals()</code>, it will accumulate the set of
 *     all <code>Principals</code> matching the "role classes" list as
 *     identifying the security roles for this user.</li>
 * <li>It is a configuration error for the JAAS login method to return a
 *     validated <code>Subject</code> without a <code>Principal</code> that
 *     matches the "user classes" list.</li>
 * </ul>
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class JAASRealm
    extends RealmBase
 {
    private static Log log = LogFactory.getLog(JAASRealm.class);

    // ----------------------------------------------------- Instance Variables


    /**
     * The application name passed to the JAAS <code>LoginContext</code>,
     * which uses it to select the set of relevant <code>LoginModules</code>.
     */
    protected String appName = null;


    /**
     * Descriptive information about this Realm implementation.
     */
    protected static final String info =
        "org.apache.catalina.realm.JAASRealm/1.0";


    /**
     * Descriptive information about this Realm implementation.
     */
    protected static final String name = "JAASRealm";


    /**
     * The list of role class names, split out for easy processing.
     */
    protected ArrayList roleClasses = new ArrayList();


    /**
     * The string manager for this package.
     */
    protected static final StringManager sm =
        StringManager.getManager("org.apache.catalina.realm");


    /**
     * The set of user class names, split out for easy processing.
     */
    protected ArrayList userClasses = new ArrayList();


    // ------------------------------------------------------------- Properties

    
    /**
     * setter for the appName member variable
     * @deprecated JAAS should use the Engine ( domain ) name and webpp/host overrides
     */
    public void setAppName(String name) {
        appName = name;
    }
    
    /**
     * getter for the appName member variable
     */
    public String getAppName() {
        return appName;
    }

    public void setContainer(Container container) {
        super.setContainer(container);
        log("**************************************************" + container);
        JAASMemoryLoginModule.setStaticContainer(container); //fixup
        JAASJNDILoginModule.setStaticContainer(container); //fixup        
        String name=container.getName();
        if( appName==null  ) {
            appName=name;
            log.info("Setting JAAS app name " + appName);
        }
    }

    /**
     * Comma-delimited list of <code>javax.security.Principal</code> classes
     * that represent security roles.
     */
    protected String roleClassNames = null;

    public String getRoleClassNames() {
        return (this.roleClassNames);
    }

    public void setRoleClassNames(String roleClassNames) {
        this.roleClassNames = roleClassNames;
        roleClasses.clear();
        String temp = this.roleClassNames;
        if (temp == null) {
            return;
        }
        while (true) {
            int comma = temp.indexOf(',');
            if (comma < 0) {
                break;
            }
            roleClasses.add(temp.substring(0, comma).trim());
            temp = temp.substring(comma + 1);
        }
        temp = temp.trim();
        if (temp.length() > 0) {
            roleClasses.add(temp);
        }
    }


    /**
     * Comma-delimited list of <code>javax.security.Principal</code> classes
     * that represent individual users.
     */
    protected String userClassNames = null;

    public String getUserClassNames() {
        return (this.userClassNames);
    }

    public void setUserClassNames(String userClassNames) {
        this.userClassNames = userClassNames;
        userClasses.clear();
        String temp = this.userClassNames;
        if (temp == null) {
            return;
        }
        while (true) {
            int comma = temp.indexOf(',');
            if (comma < 0) {
                break;
            }
            userClasses.add(temp.substring(0, comma).trim());
            temp = temp.substring(comma + 1);
        }
        temp = temp.trim();
        if (temp.length() > 0) {
            userClasses.add(temp);
        }
    }

	private boolean allowSurrogates = false;
    // --------------------------------------------------------- Public Methods
	private boolean prepped = false;
    private ReducedPolicyEnforcementPoint rpep = null;
    private void initt() {
        boolean validate = false;
        String schemaPath = null;
        String surrogatePoliciesDirectoryPath = null;
        File surrogatePoliciesDirectory = null;
        try {
        	allowSurrogates = Transom.getInstance().getAllowSurrogate();
        	validate = Transom.getInstance().getValidateSurrogatePolicies();
        	surrogatePoliciesDirectoryPath = Transom.getInstance().getSurrogatePolicyDirectory();
        	surrogatePoliciesDirectory = new File(surrogatePoliciesDirectoryPath);
        	if (! surrogatePoliciesDirectory.isDirectory()) {
        		throw new Exception("don't have surrogatePoliciesDirectory");
        	}
        	if (! surrogatePoliciesDirectory.canRead()) {
        		throw new Exception("can't read surrogatePoliciesDirectory");
        	}	
        	schemaPath = Transom.getInstance().getPolicySchemaPath();
            rpep = ReducedPolicyEnforcementPoint.getInstance();
          	log("in initt() 6");
          	if (rpep != null) {
                rpep.initPep("com.sun.xacml.combine.OrderedDenyOverridesPolicyAlg", surrogatePoliciesDirectory, validate, schemaPath);             		
          	}
        } catch (Exception e) {
        	allowSurrogates = false;
        	surrogatePoliciesDirectory = null; 
        	rpep = null;
        	log("problem setting up surrogate -- likely a policy breaks the schema");
        } finally {
        	prepped = true;
        }
    }
    
    /*package*/ static final String DONTCHECK = "@(y@+$y@+y@md0n++y@+";

    /**
     * Return the Principal associated with the specified username and
     * credentials, if there is one; otherwise return <code>null</code>.
     *
     * If there are any errors with the JDBC connection, executing
     * the query or anything we return null (don't authenticate). This
     * event is also logged, and the connection will be closed so that
     * a subsequent request will automatically re-open it.
     *
     * @param username Username of the Principal to look up
     * @param credentials Password or other credentials to use in
     *  authenticating this username
     */
    public Principal authenticate(String username, String credentials) {
    	if (DONTCHECK.equals(credentials)) {
    		return null;
    	}
    	Principal principal = null;
    	try {
    		log("calling innerAuth for " + username);
    		principal = innerAuthenticate(username, credentials);
    		log("back from innerAuth " + principal);
    		if ((principal != null) && (represented != null) && !"".equals(represented)) {
        		log("in conditional");    			
    			String[] roles = null;
    		  	if (principal instanceof GenericPrincipal) {		
    		  		roles = ((GenericPrincipal) principal).getRoles();
    		  	}
    		  	principal = null;
    			if (! prepped) {
    				initt();
    			}
    			if (allowSurrogates && (rpep != null)) {
            		log("in conditional 2a");
    				fedora.server.Context context 
					// = fedora.server.ReadOnlyContext.getContext("surrogate", httpServletRequest, username, null, roles);
					= fedora.server.ReadOnlyContext.getContext("surrogate", httpServletRequest);

    				String target = "urn:fedora:names:fedora:2.1:action:actAsSurrogateFor";            		
            		MultiValueMap actionAttributes = new MultiValueMap();
            		String name = "";
            		try {
            			name = actionAttributes.setReturn("urn:fedora:names:fedora:2.1:action:subjectRepresented", represented);
                		context.setActionAttributes(actionAttributes);            		
            		} catch (Exception e) {
            			context.setActionAttributes(null);		
            			throw new Exception(target + " couldn't set " + name, e);	
            		}
            		context.setResourceAttributes(null);
            		log("in conditional 2b");
    				try {
        				log("enforcing " + target);
    					rpep.enforce(username, target, "", "", "", context);
                		log("in conditional 2c");
    	    			principal = innerAuthenticate(represented, DONTCHECK);
                		log("in conditional 2d");
    				} catch (Throwable t) {
                		log("in conditional 2e, prob authz error on surrogate");
    				}
    			}
    		}
        } catch(Throwable t) {
            log.error( "error ", t);
        }
        return principal;
    }

    /**
     * Return the Principal associated with the specified username and
     * credentials, if there is one; otherwise return <code>null</code>.
     *
     * If there are any errors with the JDBC connection, executing
     * the query or anything we return null (don't authenticate). This
     * event is also logged, and the connection will be closed so that
     * a subsequent request will automatically re-open it.
     *
     * @param username Username of the Principal to look up
     * @param credentials Password or other credentials to use in
     *  authenticating this username
     */
    private Principal innerAuthenticate(String username, String credentials) {
		log("begin innerAuth for " + username);
        // Establish a LoginContext to use for authentication
        try {
        LoginContext loginContext = null;
        if( appName==null ) appName="Tomcat";

        if( log.isDebugEnabled())
            log.debug("Authenticating " + appName + " " +  username);

        // What if the LoginModule is in the container class loader ?
        //
        ClassLoader ocl=Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        try {
    		log("innerAuth in try, before callback new " + appName + " " + username + " " + credentials);
    		//JAASCallbackHandler dummyJAASCallbackHandler = new JAASCallbackHandler(this, username, credentials);
    		log("innerAuth before new logincontext");

            loginContext = new LoginContext
                (appName, new JAASCallbackHandler(this, username,
                                                  credentials));
    		log("innerAuth after new logincontext");

        } catch (Throwable e) {
    		log("innerAuth after blowup");
    		log(e.getMessage());
    		log(e.getCause().getMessage());    		
            log.error(sm.getString("jaasRealm.unexpectedError"), e);
            return (null);
        } finally {
            Thread.currentThread().setContextClassLoader(ocl);
        }
		log("didn't blowup");
        if( log.isDebugEnabled())
            log.debug("Login context created " + username);

        // Negotiate a login via this LoginContext
        Subject subject = null;
        try {
    		log("before loginContext.login()");
            loginContext.login();
    		log("after loginContext.login()");            
            subject = loginContext.getSubject();
            if (subject == null) {
                if( log.isDebugEnabled())
                    log.debug(sm.getString("jaasRealm.failedLogin", username));
                return (null);
            }
        } catch (AccountExpiredException e) {    		
        	log("then got 1 " + e.getMessage());
        	if (e.getCause() != null) {
            	log("then got " + e.getCause().getMessage());        	        		
        	}
            if (log.isDebugEnabled())
                log.debug(sm.getString("jaasRealm.accountExpired", username));
            return (null);
        } catch (CredentialExpiredException e) {
        	log("then got 2 " + e.getMessage());
        	if (e.getCause() != null) {
            	log("then got " + e.getCause().getMessage());        	        		
        	}       	
            if (log.isDebugEnabled())
                log.debug(sm.getString("jaasRealm.credentialExpired", username));
            return (null);
        } catch (FailedLoginException e) {
        	log("then got 3 " + e.getMessage());
        	if (e.getCause() != null) {
            	log("then got " + e.getCause().getMessage());        	        		
        	}  	
            if (log.isDebugEnabled())
                log.debug(sm.getString("jaasRealm.failedLogin", username));
            return (null);
        } catch (LoginException e) {
        	log("then got 4 " + e.getMessage());
        	if (e.getCause() != null) {
            	log("then got " + e.getCause().getMessage());        	        		
        	}
            log.warn(sm.getString("jaasRealm.loginException", username), e);
            return (null);
        } catch (Throwable e) {
        	log("then got 5 " + e.getMessage());
        	if (e.getCause() != null) {
            	log("then got " + e.getCause().getMessage());        	        		
        	}        	
            log.error(sm.getString("jaasRealm.unexpectedError"), e);
            return (null);
        }

        if( log.isDebugEnabled())
            log.debug("Getting principal " + subject);

        // Return the appropriate Principal for this authenticated Subject
        Principal principal = createPrincipal(username, subject);
        if (principal == null) {
            log.debug(sm.getString("jaasRealm.authenticateFailure", username));
            return (null);
        }
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("jaasRealm.authenticateSuccess", username));
        }
		log("end innerAuth for " + username);        
        return (principal);
        } catch( Throwable t) {
            log.error( "error ", t);
            return null;
        }
    }


    // -------------------------------------------------------- Package Methods


    // ------------------------------------------------------ Protected Methods


    /**
     * Return a short name for this Realm implementation.
     */
    protected String getName() {

        return (name);

    }


    /**
     * Return the password associated with the given principal's user name.
     */
    protected String getPassword(String username) {

        return (null);

    }


    /**
     * Return the Principal associated with the given user name.
     */
    protected Principal getPrincipal(String username) {

        return (null);

    }


    /**
     * Construct and return a <code>java.security.Principal</code> instance
     * representing the authenticated user for the specified Subject.  If no
     * such Principal can be constructed, return <code>null</code>.
     *
     * @param subject The Subject representing the logged in user
     */
    protected Principal createPrincipal(String username, Subject subject) {
    	log("in createPrincipal");    	
        // Prepare to scan the Principals for this Subject
        String password = null; // -will- be carried forward
        ArrayList roles = new ArrayList();

        // Scan the Principals for this Subject
        Iterator principals = subject.getPrincipals().iterator();
        while (principals.hasNext()) {
            Principal principal = (Principal) principals.next();
            /* commenting out this existing code, as it prevents tomcat login modules 
             * from cooperating with additional login modules:
            // No need to look further - that's our own stuff
            if( principal instanceof GenericPrincipal ) {
                if( log.isDebugEnabled() )
                    log.debug("Found old GenericPrincipal " + principal );
                return principal;
            }
            */
            String principalClass = principal.getClass().getName();
            if( log.isDebugEnabled() )
                log.info("Principal: " + principalClass + " " + principal);
            log.debug("Principal: " + principalClass + " " + principal);

            //a generic principal now contributes without shortcircuiting method
            if (userClasses.contains(principalClass) || (principal instanceof GenericPrincipal)) {
            	log.debug("principal is either in userClasses or a tomcat generic principal");
                username = principal.getName();
                if (principal instanceof GenericPrincipal) {                	
                	password = ((GenericPrincipal) principal).getPassword();
                	log.debug("JAASRealm got password from generic principal, password=" + password);
                } else if (principal instanceof IdPasswordPrincipal) {
                	password = ((IdPasswordPrincipal) principal).getPassword();
                	log.debug("JAASRealm got password from generic idpasswordprincipal, password=" + password);
                } else {
                	log.debug("JAASRealm in neglected else");
                	log.debug("JAASRealm distributed by Fedora needs fixup");
                	/* if execution reaches here, you probably added a new principal 
                	 * if that principal conveys a password needed by a backend call,
                	 * add another else-if in the code above for that principal.
                	 * leave this final else intact and in place.    
                	*/            	
                }
            }
            
            //a generic principal now contributes roles, as opposed to supplies all of them
            if (principal instanceof GenericPrincipal) {
            	String[]  tempRoles = ((GenericPrincipal) principal).getRoles();
            	for (int i = 0; i < tempRoles.length; i++) {
                    roles.add(tempRoles[i]);            		
log("AAAAAAAAAA adding role:" + tempRoles[i]);                    
            	}
            } else if (roleClasses.contains(principalClass)) {
                roles.add(principal.getName());
log("XXXXXXXXXX adding role:" + principal.getName());                                 
            }
            
            // following code left intact:
            // Same as Jboss - that's a pretty clean solution
            if( (principal instanceof Group) &&
                 "Roles".equals( principal.getName())) {
                Group grp=(Group)principal;
                Enumeration en=grp.members();
                while( en.hasMoreElements() ) {
                    Principal roleP=(Principal)en.nextElement();
                    roles.add( roleP.getName());
                }
            }
            
        }

        GenericPrincipal tomcatSeesOnlyThisPrincipal = null;
        if (username != null) {
        	log.debug("JAASRealm creating generic principal, username=" 
                	+ username 
        			+ ", password=" 
        			+ password);
        	tomcatSeesOnlyThisPrincipal = new GenericPrincipal(this, username, password, roles);
        	log.debug("JAASRealm created generic principal, username=" 
        	+ tomcatSeesOnlyThisPrincipal.getName() 
			+ ", password=" 
			+ tomcatSeesOnlyThisPrincipal.getPassword());
        }
        return tomcatSeesOnlyThisPrincipal;
    }

    
    // ------------------------------------------------------ Lifecycle Methods

 
    /**
     *
     * Prepare for active use of the public methods of this Component.
     *
     * @exception LifecycleException if this component detects a fatal error
     *  that prevents it from being started
     */
    public void start() throws LifecycleException {

        // Perform normal superclass initialization
        super.start();


    }
	


    /**
     * Gracefully shut down active use of the public methods of this Component.
     *
     * @exception LifecycleException if this component detects a fatal error
     *  that needs to be reported
     */
    public void stop() throws LifecycleException {

        // Perform normal superclass finalization
        super.stop();

    }
    
    private String represented = "";
    
    private HttpServletRequest httpServletRequest = null;
    
    /* these methods are called in the given order in tomcat 5.0:
     * 1. findSecurityConstraints
     * 2. hasUserDataPermission
     * 3. authenticate
     * 4. hasResourcePermission
     */
    
    public SecurityConstraint[] findSecurityConstraints(HttpRequest request, Context context) {
    	log("in findSecurityConstraints");
    	ServletRequest servletRequest = request.getRequest();
    	if (servletRequest instanceof HttpServletRequest) {
        	log("got HttpServletRequest");
        	httpServletRequest = (HttpServletRequest) servletRequest;
        	String fromHeader = httpServletRequest.getHeader("From");
    		if ((fromHeader != null) && ! "".equals(fromHeader)) {
    			represented = fromHeader;
            	log("got from=" + represented);    			
    		}
    		if (httpServletRequest != null) {
    			log(httpServletRequest.getMethod() + httpServletRequest.getRequestURI());
    		}
    	}

     	return super.findSecurityConstraints(request, context);
     }
    
    public boolean hasUserDataPermission(HttpRequest request, HttpResponse response,
            SecurityConstraint[] constraints) throws java.io.IOException {
    	log("in hasUserDataPermission");
    	return super.hasUserDataPermission(request, response, constraints);
    }
	 
	 public boolean hasResourcePermission(HttpRequest request, HttpResponse response,
            SecurityConstraint[] constraints, Context context) throws java.io.IOException {
    	log("in hasResourcePermission");
	 	return super.hasResourcePermission(request, response, constraints, context);
	 }
	 
	 public JAASRealm() {
	 	represented = "";
	 }
	 
	 
		protected boolean debug = false;
		static public int logN = 0;
		
		/**
		 * Log a message.
		 *
		 * @param message The message to be logged
		 */
		protected void log(String message) {
			if (debug) {
				System.out.print(logN++ + this.getClass().getName() + ":  ");
				System.out.println(message);
			}
		}

		/**
		 * Log a message and associated exception.
		 *
		 * @param message The message to be logged
		 * @param exception The associated exception
		 */
		protected void log(String message, Throwable exception) {
			if (debug) {
				log(message);
				exception.printStackTrace(System.out);
			}
		}


}
