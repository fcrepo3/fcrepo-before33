/*
 * Created on Jun 29, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.apache.catalina.realm;

import java.security.Principal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.CommunicationException;
import javax.naming.InvalidNameException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NameParser;
import javax.naming.Name;
import javax.naming.AuthenticationException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.util.Base64;
import org.apache.catalina.realm.RealmBase;
import org.apache.catalina.realm.GenericPrincipal;


/**
 * @author wdn5e
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class JNDIRealm extends RealmBase {
	
	

	/**
	 * This JAAS LoginModule does -not- perform authentication, 
	 * but -does- return as user Principals those roles found in the 
	 * directory in fields configured for role discovery.
	 *
	 * Though comparable to the JAAS JNDILoginModule, it is dissimilar
	 * in not performing authentication and in returning user roles
	 * as additional Principals.  It is not based on JNDILoginModule code.
	 * 
	 * Instead it is based on and includes code from Tomcat's JNDIRealm,
	 * to return user roles.  Unlike JNDIRealm, it wraps roles
	 * as Principals, as required by a JAAS LoginModule.  It also allows
	 * multiple role fields; JNDIRealm allows only one such field.
	 * Roles are namespaced to indicate the field of origin.
	 * It omits JNDIRealm's authenticatation function. 
	 * 
	 * @author wdn5e
	 * 
	 * This product includes software developed by the
	 * Apache Software Foundation (http://www.apache.org/).
	 * Copyright (c) 1999-2003 The Apache Software Foundation.
	 * All rights reserved.
	 */

		//	BEGIN LDAP CODE

		/*
		* $Header$
		* $Revision$addAttributeValues
		* $Date$
		*
		* ====================================================================
		* The Apache Software License, Version 1.1
		*
		* Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
		* reserved.
		*
		* Redistribution and use in source and binary forms, with or without
		* modification, are permitted provided that the following conditions
		* are met:
		*
		* 1. Redistributions of source code must retain the above copyright
		*    notice, this list of conditions and the following disclaimer.
		*
		* 2. Redistributions in binary form must reproduce the above copyright
		*    notice, this list of conditions and the following disclaimer in
		*    the documentation and/or other materials provided with the
		*    distribution.
		*
		* 3. The end-user documentation included with the redistribution, if
		*    any, must include the following acknowlegement:
		*       "This product includes software developed by the
		*        Apache Software Foundation (http://www.apache.org/)."
		*    Alternately, this acknowlegement may appear in the software itself,
		*    if and wherever such third-party acknowlegements normally appear.
		*
		* 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
		*    Foundation" must not be used to endorse or promote products derived
		*    from this software without prior written permission. For written
		*    permission, please contact apache@apache.org.
		*
		* 5. Products derived from this software may not be called "Apache"
		*    nor may "Apache" appear in their names without prior written
		*    permission of the Apache Group.
		*
		* THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
		* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
		* OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
		* DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
		* ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
		* SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
		* LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
		* USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
		* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
		* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
		* OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
		* SUCH DAMAGE.
		* ====================================================================
		*
		* This software consists of voluntary contributions made by many
		* individuals on behalf of the Apache Software Foundation.  For more
		* information on the Apache Software Foundation, please see
		* <http://www.apache.org/>.
		*
		* [Additional notices, if required by prior licensing conditions]
		*
		*/

		/**
		 * <p>Implementation of <strong>Realm</strong> that works with a directory
		 * server accessed via the Java Naming and Directory Interface (JNDI) APIs.
		 * The following constraints are imposed on the data structure in the
		 * underlying directory server:</p>
		 * <ul>
		 *
		 * <li>Each user that can be authenticated is represented by an individual
		 *     element in the top level <code>DirContext</code> that is accessed
		 *     via the <code>connectionURL</code> property.</li>
		 *
		 * <li>If a socket connection can not be made to the <code>connectURL</code>
		 *     an attempt will be made to use the <code>alternateURL</code> if it
		 *     exists.</li>
		 *
		 * <li>Each user element has a distinguished name that can be formed by
		 *     substituting the presented username into a pattern configured by the
		 *     <code>userPattern</code> property.</li>
		 *
		 * <li>Alternatively, if the <code>userPattern</code> property is not
		 *     specified, a unique element can be located by searching the directory
		 *     context. In this case:
		 *     <ul>
		 *     <li>The <code>userSearch</code> pattern specifies the search filter
		 *         after substitution of the username.</li>
		 *     <li>The <code>userBase</code> property can be set to the element that
		 *         is the base of the subtree containing users.  If not specified,
		 *         the search base is the top-level context.</li>
		 *     <li>The <code>userSubtree</code> property can be set to
		 *         <code>true</code> if you wish to search the entire subtree of the
		 *         directory context.  The default value of <code>false</code>
		 *         requests a search of only the current level.</li>
		 *    </ul>
		 * </li>
		 *
		 * <li>The user may be authenticated by binding to the directory with the
		 *      username and password presented. This method is used when the
		 *      <code>userPassword</code> property is not specified.</li>
		 *
		 * <li>The user may be authenticated by retrieving the value of an attribute
		 *     from the directory and comparing it explicitly with the value presented
		 *     by the user. This method is used when the <code>userPassword</code>
		 *     property is specified, in which case:
		 *     <ul>
		 *     <li>The element for this user must contain an attribute named by the
		 *         <code>userPassword</code> property.
		 *     <li>The value of the user password attribute is either a cleartext
		 *         String, or the result of passing a cleartext String through the
		 *         <code>RealmBase.digest()</code> method (using the standard digest
		 *         support included in <code>RealmBase</code>).
		 *     <li>The user is considered to be authenticated if the presented
		 *         credentials (after being passed through
		 *         <code>RealmBase.digest()</code>) are equal to the retrieved value
		 *         for the user password attribute.</li>
		 *     </ul></li>
		 *
		 * <li>Each group of users that has been assigned a particular role may be
		 *     represented by an individual element in the top level
		 *     <code>DirContext</code> that is accessed via the
		 *     <code>connectionURL</code> property.  This element has the following
		 *     characteristics:
		 *     <ul>
		 *     <li>The set of all possible groups of interest can be selected by a
		 *         search pattern configured by the <code>roleSearch</code>
		 *         property.</li>
		 *     <li>The <code>roleSearch</code> pattern optionally includes pattern
		 *         replacements "{0}" for the distinguished name, and/or "{1}" for
		 *         the username, of the authenticated user for which roles will be
		 *         retrieved.</li>
		 *     <li>The <code>roleBase</code> property can be set to the element that
		 *         is the base of the search for matching roles.  If not specified,
		 *         the entire context will be searched.</li>
		 *     <li>The <code>roleSubtree</code> property can be set to
		 *         <code>true</code> if you wish to search the entire subtree of the
		 *         directory context.  The default value of <code>false</code>
		 *         requests a search of only the current level.</li>
		 *     <li>The element includes an attribute (whose name is configured by
		 *         the <code>roleName</code> property) containing the name of the
		 *         role represented by this element.</li>
		 *     </ul></li>
		 *
		 * <li>In addition, roles may be represented by the values of an attribute
		 * in the user's element whose name is configured by the
		 * <code>userRoleName</code> property.</li>
		 *
		 * <li>Note that the standard <code>&lt;security-role-ref&gt;</code> element in
		 *     the web application deployment descriptor allows applications to refer
		 *     to roles programmatically by names other than those used in the
		 *     directory server itself.</li>
		 * </ul>
		 *
		 * <p><strong>TODO</strong> - Support connection pooling (including message
		 * format objects) so that <code>authenticate()</code> does not have to be
		 * synchronized.</p>
		 *
		 * <p><strong>WARNING</strong> - There is a reported bug against the Netscape
		 * provider code (com.netscape.jndi.ldap.LdapContextFactory) with respect to
		 * successfully authenticated a non-existing user. The
		 * report is here: http://nagoya.apache.org/bugzilla/show_bug.cgi?id=11210 .
		 * With luck, Netscape has updated their provider code and this is not an
		 * issue. </p>
		 *
		 * @author John Holman
		 * @author Craig R. McClanahan
		 * @version $Revision$ $Date$
		 */

		// ----------------------------------------------------- Instance Variables

		/**
		 *  The type of authentication to use
		 */
		protected String authentication = null;

		/**
		 * The connection username for the server we will contact.
		 */
		protected String connectionName = null;

		/**
		 * The connection password for the server we will contact.
		 */
		protected String connectionPassword = null;

		/**
		 * The connection URL for the server we will contact.
		 */
		protected String connectionURL = null;

		/**
		 * The directory context linking us to our directory server.
		 */
		protected DirContext context = null;

		/**
		 * The JNDI context factory used to acquire our InitialContext.  By
		 * default, assumes use of an LDAP server using the standard JNDI LDAP
		 * provider.
		 */
		protected String contextFactory = "com.sun.jndi.ldap.LdapCtxFactory";

		/**
		 * Descriptive information about this Realm implementation.
		 */
		protected static final String info = "org.apache.catalina.realm.JndiRealm/1.0";

		/**
		 * Descriptive information about this Realm implementation.
		 */
		protected static final String name = "JndiRealm";

		/**
		 * The protocol that will be used in the communication with the
		 * directory server.
		 */
		protected String protocol = null;

		/**
		 * How should we handle referrals?  Microsoft Active Directory can't handle
		 * the default case, so an application authenticating against AD must
		 * set referrals to "follow".
		 */
		protected String referrals = null;

		/**
		 * The base element for user searches.
		 */
		protected String userBase = "";

		/**
		 * The message format used to search for a user, with "{0}" marking
		 * the spot where the username goes.
		 */
		protected String userSearch = null;

		/**
		 * The MessageFormat object associated with the current
		 * <code>userSearch</code>.
		 */
		protected MessageFormat userSearchFormat = null;

		/**
		 * Should we search the entire subtree for matching users?
		 */
		protected boolean userSubtree = false;

		/**
		 * The attribute name used to retrieve the user password.
		 */
		protected String userPassword = null;
		public static final String NO_AUTHENTICATION = "NO_AUTHENTICATION";

		/**
		 * A string of LDAP user patterns or paths, ":"-separated
		 * These will be used to form the distinguished name of a
		 * user, with "{0}" marking the spot where the specified username
		 * goes.
		 * This is similar to userPattern, but allows for multiple searches
		 * for a user.
		 */
		protected String[] userPatternArray = null;

		/**
		 * The message format used to form the distinguished name of a
		 * user, with "{0}" marking the spot where the specified username
		 * goes.
		 */
		protected String userPattern = null;

		/**
		 * An array of MessageFormat objects associated with the current
		 * <code>userPatternArray</code>.
		 */
		protected MessageFormat[] userPatternFormatArray = null;

		/**
		 * The base element for role searches.
		 */
		protected String roleBase = "";

		/**
		 * The MessageFormat object associated with the current
		 * <code>roleSearch</code>.
		 */
		protected MessageFormat roleFormat = null;

		/**
		 * The name of an attribute in the user's entry containing
		 * roles for that user
		 */
		protected String userRoleName = null;

		/**
		 * The name of the attribute containing roles held elsewhere
		 */
		protected String roleName = null;

		/**
		 * The message format used to select roles for a user, with "{0}" marking
		 * the spot where the distinguished name of the user goes.
		 */
		protected String roleSearch = null;

		/**
		 * Should we search the entire subtree for matching memberships?
		 */
		protected boolean roleSubtree = false;

		/**
		 * An alternate URL, to which, we should connect if connectionURL fails.
		 */
		protected String alternateURL;

		/**
		 * The number of connection attempts.  If greater than zero we use the
		 * alternate url.
		 */
		protected int connectionAttempt = 0;

		/**
		 * The current user pattern to be used for lookup and binding of a user.
		 */
		protected int curUserPattern = 0;

		// ------------------------------------------------------------- Properties

		/**
		 * Return the type of authentication to use.
		 */
		public String getAuthentication() {

			return authentication;

		}

		/**
		 * Set the type of authentication to use.
		 *
		 * @param authentication The authentication
		 */
		public void setAuthentication(String authentication) {

			this.authentication = authentication;

		}

		/**
		 * Return the connection username for this Realm.
		 */
		public String getConnectionName() {
			return (this.connectionName);

		}

		/**
		 * Set the connection username for this Realm.
		 *
		 * @param connectionName The new connection username
		 */
		public void setConnectionName(String connectionName) {

			this.connectionName = connectionName;

		}

		/**
		 * Return the connection password for this Realm.
		 */
		public String getConnectionPassword() {

			return (this.connectionPassword);

		}

		/**
		 * Set the connection password for this Realm.
		 *
		 * @param connectionPassword The new connection password
		 */
		public void setConnectionPassword(String connectionPassword) {

			this.connectionPassword = connectionPassword;

		}

		/**
		 * Return the connection URL for this Realm.
		 */
		public String getConnectionURL() {

			return (this.connectionURL);

		}

		/**
		 * Set the connection URL for this Realm.
		 *
		 * @param connectionURL The new connection URL
		 */
		public void setConnectionURL(String connectionURL) {
			log("setConnectionURL=" + connectionURL);
			this.connectionURL = connectionURL;

		}

		/**
		 * Return the JNDI context factory for this Realm.
		 */
		public String getContextFactory() {

			return (this.contextFactory);

		}

		/**
		 * Set the JNDI context factory for this Realm.
		 *
		 * @param contextFactory The new context factory
		 */
		public void setContextFactory(String contextFactory) {

			this.contextFactory = contextFactory;

		}

		/**
		 * Return the protocol to be used.
		 */
		public String getProtocol() {

			return protocol;

		}

		/**
		 * Set the protocol for this Realm.
		 *
		 * @param protocol The new protocol.
		 */
		public void setProtocol(String protocol) {

			this.protocol = protocol;

		}

		/**
		 * Returns the current settings for handling JNDI referrals.
		 */
		public String getReferrals() {
			return referrals;
		}

		/**
		 * How do we handle JNDI referrals? ignore, follow, or throw
		 * (see javax.naming.Context.REFERRAL for more information).
		 */
		public void setReferrals(String referrals) {
			this.referrals = referrals;
		}

		/**
		 * Return the base element for user searches.
		 */
		public String getUserBase() {

			return (this.userBase);

		}

		/**
		 * Set the base element for user searches.
		 *
		 * @param userBase The new base element
		 */
		public void setUserBase(String userBase) {
			log("setUserBase=" + userBase);
			this.userBase = userBase;

		}

		/**
		 * Return the message format pattern for selecting users in this Realm.
		 */
		public String getUserSearch() {

			return (this.userSearch);

		}

		/**
		 * Set the message format pattern for selecting users in this Realm.
		 *
		 * @param userSearch The new user search pattern
		 */
		public void setUserSearch(String userSearch) {
			log("setUserSearch=" + userSearch);
			this.userSearch = userSearch;
			if (userSearch == null)
				userSearchFormat = null;
			else
				userSearchFormat = new MessageFormat(userSearch);

		}

		/**
		 * Return the "search subtree for users" flag.
		 */
		public boolean getUserSubtree() {

			return (this.userSubtree);

		}

		/**
		 * Set the "search subtree for users" flag.
		 *
		 * @param userSubtree The new search flag
		 */
		public void setUserSubtree(boolean userSubtree) {
			log("userSubtree=" + userSubtree);
			this.userSubtree = userSubtree;

		}

		/**
		 * Return the user role name attribute name for this Realm.
		 */
		public String getUserRoleName() {

			return userRoleName;
		}

		/**
		 * Set the user role name attribute name for this Realm.
		 *
		 * @param userRoleName The new userRole name attribute name
		 */
		public void setUserRoleName(String userRoleName) {
			log("setUserRoleName=" + userRoleName);
			this.userRoleName = userRoleName;

		}

		/**
		 * Return the base element for role searches.
		 */
		public String getRoleBase() {

			return (this.roleBase);

		}

		/**
		 * Set the base element for role searches.
		 *
		 * @param roleBase The new base element
		 */
		public void setRoleBase(String roleBase) {

			this.roleBase = roleBase;

		}

		/**
		 * Return the role name attribute name for this Realm.
		 */
		public String getRoleName() {

			return (this.roleName);

		}

		/**
		 * Set the role name attribute name for this Realm.
		 *
		 * @param roleName The new role name attribute name
		 */
		public void setRoleName(String roleName) {

			this.roleName = roleName;

		}

		/**
		 * Return the message format pattern for selecting roles in this Realm.
		 */
		public String getRoleSearch() {

			return (this.roleSearch);

		}

		/**
		 * Set the message format pattern for selecting roles in this Realm.
		 *
		 * @param roleSearch The new role search pattern
		 */
		public void setRoleSearch(String roleSearch) {

			this.roleSearch = roleSearch;
			if (roleSearch == null)
				roleFormat = null;
			else
				roleFormat = new MessageFormat(roleSearch);

		}

		/**
		 * Return the "search subtree for roles" flag.
		 */
		public boolean getRoleSubtree() {

			return (this.roleSubtree);

		}

		/**
		 * Set the "search subtree for roles" flag.
		 *
		 * @param roleSubtree The new search flag
		 */
		public void setRoleSubtree(boolean roleSubtree) {

			this.roleSubtree = roleSubtree;

		}

		/**
		 * Return the password attribute used to retrieve the user password.
		 */
		public String getUserPassword() {

			return (this.userPassword);

		}

		private boolean authenticate = false;
		
		/**
		 * Set the password attribute used to retrieve the user password.
		 *
		 * @param userPassword The new password attribute
		 */
		public void setUserPassword(String userPassword) {
			this.userPassword = userPassword;
			authenticate = ! NO_AUTHENTICATION.equalsIgnoreCase(userPassword);
		}

		/**
		 * Return the message format pattern for selecting users in this Realm.
		 */
		public String getUserPattern() {

			return (this.userPattern);

		}

		/**
		 * Set the message format pattern for selecting users in this Realm.
		 * This may be one simple pattern, or multiple patterns to be tried,
		 * separated by parentheses. (for example, either "cn={0}", or
		 * "(cn={0})(cn={0},o=myorg)" Full LDAP search strings are also supported,
		 * but only the "OR", "|" syntax, so "(|(cn={0})(cn={0},o=myorg))" is
		 * also valid. Complex search strings with &, etc are NOT supported.
		 *
		 * @param userPattern The new user pattern
		 */
		public void setUserPattern(String userPattern) {

			this.userPattern = userPattern;
			if (userPattern == null)
				userPatternArray = null;
			else {
				userPatternArray = parseUserPatternString(userPattern);
				int len = this.userPatternArray.length;
				userPatternFormatArray = new MessageFormat[len];
				for (int i = 0; i < len; i++) {
					userPatternFormatArray[i] = new MessageFormat(userPatternArray[i]);
				}
			}
		}

		/**
		 * Getter for property alternateURL.
		 *
		 * @return Value of property alternateURL.
		 */
		public String getAlternateURL() {

			return this.alternateURL;

		}

		/**
		 * Setter for property alternateURL.
		 *
		 * @param alternateURL New value of property alternateURL.
		 */
		public void setAlternateURL(String alternateURL) {

			this.alternateURL = alternateURL;

		}

		// ---------------------------------------------------------- Realm Methods

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
			log("authenticate() 1:  ");

			DirContext context = null;
			Principal principal = null;

			try {
				log("authenticate() 2:  ");
				// Ensure that we have a directory context available
				context = open();
				log("authenticate() 3:  ");
				// Occassionally the directory context will timeout.  Try one more
				// time before giving up.
				try {
					log("authenticate() 4:  ");
					// Authenticate the specified username if possible
					principal = authenticate(context, username, credentials);
					log("authenticate() 5:  ");
				} catch (CommunicationException e) {

					// If contains the work closed. Then assume socket is closed.
					// If message is null, assume the worst and allow the
					// connection to be closed.
					if (e.getMessage() != null && e.getMessage().indexOf("closed") < 0)
						throw (e);

					// log the exception so we know it's there.
					log(sm.getString("jndiRealm.exception"), e);

					// close the connection so we know it will be reopened.
					if (context != null)
						close(context);

					// open a new directory context.
					context = open();

					// Try the authentication again.
					principal = authenticate(context, username, credentials);

				}
				log("authenticate() 6:  ");
				// Release this context
				release(context);
				log("authenticate() 7:  ");
				// Return the authenticated Principal (if any)
				return (principal);

			} catch (NamingException e) {

				// Log the problem for posterity
				log(sm.getString("jndiRealm.exception"), e);

				// Close the connection so that it gets reopened next time
				if (context != null)
					close(context);

				// Return "not authenticated" for this request
				return (null);

			}

		}

		// -------------------------------------------------------- Package Methods

		// ------------------------------------------------------ Protected Methods

		/**
		 * Return the Principal associated with the specified username and
		 * credentials, if there is one; otherwise return <code>null</code>.
		 *
		 * @param context The directory context
		 * @param username Username of the Principal to look up
		 * @param credentials Password or other credentials to use in
		 *  authenticating this username
		 *
		 * @exception NamingException if a directory server error occurs
		 */
		public synchronized Principal authenticate(DirContext context, String username, String credentials)
			throws NamingException {

			log("authenticate*() 1:  [" + username + "] [" + credentials + "]");
				
			if (username == null || username.equals("") || credentials == null || credentials.equals(""))
				return (null);
			log("authenticate*() 2:  ");

			if (userPatternArray != null) {
				for (curUserPattern = 0; curUserPattern < userPatternFormatArray.length; curUserPattern++) {
					// Retrieve user information
					User user = getUser(context, username);
					if (user != null) {
						try {
							// Check the user's credentials
							if (checkCredentials(context, user, credentials)) {
								// Search for additional roles
								List roles = getRoles(context, user);
								return (new GenericPrincipal(this, username, credentials, roles));
							}
						} catch (InvalidNameException ine) {
							// Log the problem for posterity
							log(sm.getString("jndiRealm.exception"), ine);
							// ignore; this is probably due to a name not fitting
							// the search path format exactly, as in a fully-
							// qualified name being munged into a search path
							// that already contains cn= or vice-versa
						}
					}
				}
				return null;
			} else {
				log("authenticate*() 3:  ");
				// Retrieve user information
				User user = getUser(context, username);
				if (user == null)
					return (null);
				log("authenticate*() 4:  ");

				// Check the user's credentials
				if (!checkCredentials(context, user, credentials))
					return (null);
				log("authenticate*() 5:  ");

				// Search for additional roles
				List roles = getRoles(context, user);
				log("authenticate*() 6:  " + username + credentials + roles);

				// Create and return a suitable Principal for this user
				return (new GenericPrincipal(this, username, credentials, roles));
			}
		}

		/**
		 * Return a User object containing information about the user
		 * with the specified username, if found in the directory;
		 * otherwise return <code>null</code>.
		 *
		 * If the <code>userPassword</code> configuration attribute is
		 * specified, the value of that attribute is retrieved from the
		 * user's directory entry. If the <code>userRoleName</code>
		 * configuration attribute is specified, all values of that
		 * attribute are retrieved from the directory entry.
		 *
		 * @param context The directory context
		 * @param username Username to be looked up
		 *
		 * @exception NamingException if a directory server error occurs
		 */
		protected User getUser(DirContext context, String username) throws NamingException {

			User user = null;

			// Get attributes to retrieve from user entry
			ArrayList list = new ArrayList();
			//2004.06.16 wdn5e vvvv replace	if (userPassword != null) with ...
			if ((userPassword != null) && authenticate)
				list.add(userPassword);
			if (userRoleName != null) {
				//2004.05.27 wdn5e vvvvvvvvvvvvvvvvvvvvvvvvvvvvv
				//replace list.add(userRoleName); with ...
				String[] userRoleNames = userRoleName.split(",");
				for (int i = 0; i < userRoleNames.length; i++) {
					if (debug >= 2)
						log("userRoleName[" + i + "]=" + userRoleNames[i]);
					list.add(userRoleNames[i]);
				}
				//to allow multiple role fields
				//2004.05.27 wdn5e ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
			}
			String[] attrIds = new String[list.size()];
			list.toArray(attrIds);

			// Use pattern or search for user entry
			if (userPatternFormatArray != null) {
				user = getUserByPattern(context, username, attrIds);
			} else {
				user = getUserBySearch(context, username, attrIds);
			}

			return user;
		}

		/**
		 * Use the <code>UserPattern</code> configuration attribute to
		 * locate the directory entry for the user with the specified
		 * username and return a User object; otherwise return
		 * <code>null</code>.
		 *
		 * @param context The directory context
		 * @param username The username
		 * @param attrIds String[]containing names of attributes to
		 * retrieve.
		 *
		 * @exception NamingException if a directory server error occurs
		 */
		protected User getUserByPattern(DirContext context, String username, String[] attrIds) throws NamingException {

			if (debug >= 2)
				log("lookupUser(" + username + ")");

			if (username == null || userPatternFormatArray[curUserPattern] == null)
				return (null);

			// Form the dn from the user pattern
			String dn = userPatternFormatArray[curUserPattern].format(new String[] { username });
			if (debug >= 3) {
				log("  dn=" + dn);
			}

			// Return if no attributes to retrieve
			if (attrIds == null || attrIds.length == 0)
				return new User(username, dn, null, null);

			// Get required attributes from user entry
			Attributes attrs = null;
			try {
				attrs = context.getAttributes(dn, attrIds);
			} catch (NameNotFoundException e) {
				return (null);
			}
			if (attrs == null)
				return (null);

			// Retrieve value of userPassword
			String password = null;
			if (userPassword != null)
				password = getAttributeValue(userPassword, attrs);

			// Retrieve values of userRoleName attribute
			ArrayList roles = null;
			if (userRoleName != null)
				roles = addAttributeValues(userRoleName, attrs, roles);

			return new User(username, dn, password, roles);
		}

		/**
		 * Search the directory to return a User object containing
		 * information about the user with the specified username, if
		 * found in the directory; otherwise return <code>null</code>.
		 *
		 * @param context The directory context
		 * @param username The username
		 * @param attrIds String[]containing names of attributes to retrieve.
		 *
		 * @exception NamingException if a directory server error occurs
		 */
		protected User getUserBySearch(DirContext context, String username, String[] attrIds) throws NamingException {

			if (username == null || userSearchFormat == null)
				return (null);

			// Form the search filter
			String filter = userSearchFormat.format(new String[] { username });

			// Set up the search controls
			SearchControls constraints = new SearchControls();

			if (userSubtree) {
				constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
			} else {
				constraints.setSearchScope(SearchControls.ONELEVEL_SCOPE);
			}

			// Specify the attributes to be retrieved
			if (attrIds == null)
				attrIds = new String[0];
			constraints.setReturningAttributes(attrIds);

			if (debug > 3) {
				log("  Searching for " + username);
				log("  base: " + userBase + "  filter: " + filter);
			}

			NamingEnumeration results = context.search(userBase, filter, constraints);

			// Fail if no entries found
			if (results == null || !results.hasMore()) {
				if (debug > 2) {
					log("  username not found");
				}
				return (null);
			}

			// Get result for the first entry found
			SearchResult result = (SearchResult) results.next();

			// Check no further entries were found
			if (results.hasMore()) {
				log("username " + username + " has multiple entries");
				return (null);
			}

			// Get the entry's distinguished name
			NameParser parser = context.getNameParser("");
			Name contextName = parser.parse(context.getNameInNamespace());
			Name baseName = parser.parse(userBase);
			Name entryName = parser.parse(result.getName());
			Name name = contextName.addAll(baseName);
			name = name.addAll(entryName);
			String dn = name.toString();

			if (debug > 2)
				log("  entry found for " + username + " with dn " + dn);

			// Get the entry's attributes
			Attributes attrs = result.getAttributes();
			if (attrs == null)
				return null;

			// Retrieve value of userPassword
			String password = null;
			if (userPassword != null)
				password = getAttributeValue(userPassword, attrs);

			// Retrieve values of userRoleName attribute
			ArrayList roles = null;
			if (userRoleName != null) {
				//2004.05.27 wdn5e **************************
				//roles = addAttributeValues(userRoleName, attrs, roles);
				/* first try
					  String[] userRoleNames = userRoleName.split(",");
				for (int i = 0; i < userRoleNames.length; i++) {
					  roles = addAttributeValues(userRoleNames[i], attrs, roles);
				}
				first try */
				roles = addAttributeValues(attrs, roles);
				//2004.05.27 wdn5e **************************

			}

			return new User(username, dn, password, roles);
		}

		/**
		 * Check whether the given User can be authenticated with the
		 * given credentials. If the <code>userPassword</code>
		 * configuration attribute is specified, the credentials
		 * previously retrieved from the directory are compared explicitly
		 * with those presented by the user. Otherwise the presented
		 * credentials are checked by binding to the directory as the
		 * user.
		 *
		 * @param context The directory context
		 * @param user The User to be authenticated
		 * @param credentials The credentials presented by the user
		 *
		 * @exception NamingException if a directory server error occurs
		 */
		protected boolean checkCredentials(DirContext context, User user, String credentials) throws NamingException {

			boolean validated = false;

			//2004.06.16 wdn5e vvvv
			if (! authenticate)
				validated = true;
			else
			//2004.06.16 wdn5e ^^^^		
			if (userPassword == null) {
				validated = bindAsUser(context, user, credentials);
			} else {
				validated = compareCredentials(context, user, credentials);
			}

			if (debug >= 2) {
				if (validated) {
					log(sm.getString("jndiRealm.authenticateSuccess", user.username));
				} else {
					log(sm.getString("jndiRealm.authenticateFailure", user.username));
				}
			}
			return (validated);
		}

		/**
		 * Check whether the credentials presented by the user match those
		 * retrieved from the directory.
		 *
		 * @param context The directory context
		 * @param user The User to be authenticated
		 * @param credentials Authentication credentials
		 *
		 * @exception NamingException if a directory server error occurs
		 */
		protected boolean compareCredentials(DirContext context, User info, String credentials) throws NamingException {

			if (info == null || credentials == null)
				return (false);

			String password = info.password;
			if (password == null)
				return (false);

			// Validate the credentials specified by the user
			if (debug >= 3)
				log("  validating credentials");

			boolean validated = false;
			if (hasMessageDigest()) {
				// iPlanet support if the values starts with {SHA1}
				// The string is in a format compatible with Base64.encode not
				// the Hex encoding of the parent class.
				if (password.startsWith("{SHA}")) {
					/* sync since super.digest() does this same thing */
					synchronized (this) {
						password = password.substring(5);
						md.reset();
						md.update(credentials.getBytes());
						String digestedPassword = new String(Base64.encode(md.digest()));
						validated = password.equals(digestedPassword);
					}
				} else {
					// Hex hashes should be compared case-insensitive
					validated = (digest(credentials).equalsIgnoreCase(password));
				}
			} else
				validated = (digest(credentials).equals(password));
			return (validated);

		}

		/**
		 * Check credentials by binding to the directory as the user
		 *
		 * @param context The directory context
		 * @param user The User to be authenticated
		 * @param credentials Authentication credentials
		 *
		 * @exception NamingException if a directory server error occurs
		 */
		protected boolean bindAsUser(DirContext context, User user, String credentials) throws NamingException {
			Attributes attr;

			if (credentials == null || user == null)
				return (false);

			String dn = user.dn;
			if (dn == null)
				return (false);

			// Validate the credentials specified by the user
			if (debug >= 3) {
				log("  validating credentials by binding as the user");
			}

			// Set up security environment to bind as the user
			context.addToEnvironment(Context.SECURITY_PRINCIPAL, dn);
			context.addToEnvironment(Context.SECURITY_CREDENTIALS, credentials);

			// Elicit an LDAP bind operation
			boolean validated = false;
			try {
				if (debug > 2) {
					log("  binding as " + dn);
				}
				attr = context.getAttributes("", null);
				validated = true;
			} catch (AuthenticationException e) {
				if (debug > 2) {
					log("  bind attempt failed");
				}
			}

			// Restore the original security environment
			if (connectionName != null) {
				context.addToEnvironment(Context.SECURITY_PRINCIPAL, connectionName);
			} else {
				context.removeFromEnvironment(Context.SECURITY_PRINCIPAL);
			}

			if (connectionPassword != null) {
				context.addToEnvironment(Context.SECURITY_CREDENTIALS, connectionPassword);
			} else {
				context.removeFromEnvironment(Context.SECURITY_CREDENTIALS);
			}

			return (validated);
		}

		/**
		 * Return a List of roles associated with the given User.  Any
		 * roles present in the user's directory entry are supplemented by
		 * a directory search. If no roles are associated with this user,
		 * a zero-length List is returned.
		 *
		 * @param context The directory context we are searching
		 * @param user The User to be checked
		 *
		 * @exception NamingException if a directory server error occurs
		 */
		protected List getRoles(DirContext context, User user) throws NamingException {

			if (user == null)
				return (null);

			String dn = user.dn;
			String username = user.username;

			if (dn == null || username == null)
				return (null);

			if (debug >= 2)
				log("  getRoles(" + dn + ")");

			// Start with roles retrieved from the user entry
			ArrayList list = user.roles;
			if (list == null) {
				list = new ArrayList();
			}

			// Are we configured to do role searches?
			if ((roleFormat == null) || (roleName == null))
				return (list);

			// Set up parameters for an appropriate search
			String filter = roleFormat.format(new String[] { dn, username });
			filter = doRFC2254Encoding(filter);
			SearchControls controls = new SearchControls();
			if (roleSubtree)
				controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			else
				controls.setSearchScope(SearchControls.ONELEVEL_SCOPE);

			controls.setReturningAttributes(new String[] { roleName });

			// Perform the configured search and process the results
			if (debug >= 3) {
				log("  Searching role base '" + roleBase + "' for attribute '" + roleName + "'");
				log("  With filter expression '" + filter + "'");
			}
			NamingEnumeration results = context.search(roleBase, filter, controls);
			if (results == null)
				return (list); // Should never happen, but just in case ...
			while (results.hasMore()) {
				SearchResult result = (SearchResult) results.next();
				Attributes attrs = result.getAttributes();
				if (attrs == null)
					continue;
				list = addAttributeValues(roleName, attrs, list);
			}

			if (debug >= 2) {
				if (list != null) {
					log("  Returning " + list.size() + " roles");
					for (int i = 0; i < list.size(); i++)
						log("  Found role " + list.get(i));
				} else {
					log("  getRoles about to return null ");
				}
			}

			return (list);
		}

		/**
		 * Return a String representing the value of the specified attribute.
		 *
		 * @param attrId Attribute name
		 * @param attrs Attributes containing the required value
		 *
		 * @exception NamingException if a directory server error occurs
		 */
		private String getAttributeValue(String attrId, Attributes attrs) throws NamingException {

			if (debug >= 3)
				log("  retrieving attribute " + attrId);

			if (attrId == null || attrs == null)
				return null;

			Attribute attr = attrs.get(attrId);
			if (attr == null)
				return (null);
			Object value = attr.get();
			if (value == null)
				return (null);
			String valueString = null;
			if (value instanceof byte[])
				valueString = new String((byte[]) value);
			else
				valueString = value.toString();

			return valueString;
		}

		/**
		 * Add values of a specified attribute to a list
		 *
		 * @param attrId Attribute name
		 * @param attrs Attributes containing the new values
		 * @param values ArrayList containing values found so far
		 *
		 * @exception NamingException if a directory server error occurs
		 */
		private ArrayList addAttributeValues(String attrId, Attributes attrs, ArrayList values) throws NamingException {

			if (debug >= 3)
				log("  retrieving values for attribute " + attrId);
			if (attrId == null || attrs == null)
				return values;
			if (values == null)
				values = new ArrayList();
			Attribute attr = attrs.get(attrId);
			if (attr == null)
				return (values);
			NamingEnumeration e = attr.getAll();
			while (e.hasMore()) {
				String value = (String) e.next();
				values.add(value);
			}
			return values;
		}

		//	2004-05-28 wdn
		private ArrayList addAttributeValues(//String attrId,
		Attributes attrs, ArrayList values) throws NamingException {

			if (debug >= 3)
				log("  retrieving values for all attributes"); //
			/* if (attrId == null || attrs == null)
				  return values; */
			if (values == null)
				values = new ArrayList();
			//Attribute attr = attrs.get(attrId);
			NamingEnumeration ee = attrs.getAll(); //
			//if (attr == null)
			if (ee == null) //
				return (values);
			while (ee.hasMore()) { //
				Attribute attr = (Attribute) ee.next();
				NamingEnumeration e = attr.getAll();
				while (e.hasMore()) {
					String value = (String) e.next();
					//values.add(value);
					values.add(attr.getID() + "=" + value); //
				}
			} //
			return values;
		}
		//	2004-05-28 wdn

		/**
		 * Close any open connection to the directory server for this Realm.
		 *
		 * @param context The directory context to be closed
		 */
		protected void close(DirContext context) {

			// Do nothing if there is no opened connection
			if (context == null)
				return;

			// Close our opened connection
			try {
				if (debug >= 1)
					log("Closing directory context");
				context.close();
			} catch (NamingException e) {
				log(sm.getString("jndiRealm.close"), e);
			}
			this.context = null;

		}

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
		 * Open (if necessary) and return a connection to the configured
		 * directory server for this Realm.
		 *
		 * @exception NamingException if a directory server error occurs
		 */
		protected DirContext open() throws NamingException {

			// Do nothing if there is a directory server connection already open
			if (context != null)
				return (context);

			try {

				// Ensure that we have a directory context available
				context = new InitialDirContext(getDirectoryContextEnvironment());

			} catch (NamingException e) {

				connectionAttempt = 1;

				// log the first exception.
				log(sm.getString("jndiRealm.exception"), e);

				// Try connecting to the alternate url.
				context = new InitialDirContext(getDirectoryContextEnvironment());

			} finally {

				// reset it in case the connection times out.
				// the primary may come back.
				connectionAttempt = 0;

			}

			return (context);

		}

		/**
		 * Create our directory context configuration.
		 *
		 * @return java.util.Hashtable the configuration for the directory context.
		 */
		protected Hashtable getDirectoryContextEnvironment() {

			Hashtable env = new Hashtable();

			// Configure our directory context environment.
			if (debug >= 1 && connectionAttempt == 0)
				log("Connecting to URL " + connectionURL);
			else if (debug >= 1 && connectionAttempt > 0)
				log("Connecting to URL " + alternateURL);
			env.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory);
			if (connectionName != null)
				env.put(Context.SECURITY_PRINCIPAL, connectionName);
			if (connectionPassword != null)
				env.put(Context.SECURITY_CREDENTIALS, connectionPassword);
			if (connectionURL != null && connectionAttempt == 0)
				env.put(Context.PROVIDER_URL, connectionURL);
			else if (alternateURL != null && connectionAttempt > 0)
				env.put(Context.PROVIDER_URL, alternateURL);
			if (authentication != null)
				env.put(Context.SECURITY_AUTHENTICATION, authentication);
			if (protocol != null)
				env.put(Context.SECURITY_PROTOCOL, protocol);
			if (referrals != null)
				env.put(Context.REFERRAL, referrals);

			return env;

		}

		/**
		 * Release our use of this connection so that it can be recycled.
		 *
		 * @param context The directory context to release
		 */
		protected void release(DirContext context) {

			; // NO-OP since we are not pooling anything

		}

		// ------------------------------------------------------ Lifecycle Methods

		/**
		 * Prepare for active use of the public methods of this Component.
		 *
		 * @exception LifecycleException if this component detects a fatal error
		 *  that prevents it from being started
		 */
		public void start() throws LifecycleException {

			// Validate that we can open our connection
			try {
				open();
			} catch (NamingException e) {
				throw new LifecycleException(sm.getString("jndiRealm.open"), e);
			}

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

			// Close any open directory server connection
			close(this.context);

		}

		/**
		 * Given a string containing LDAP patterns for user locations (separated by
		 * parentheses in a pseudo-LDAP search string format -
		 * "(location1)(location2)", returns an array of those paths.  Real LDAP
		 * search strings are supported as well (though only the "|" "OR" type).
		 *
		 * @param userPatternString - a string LDAP search paths surrounded by
		 * parentheses
		 */
		protected String[] parseUserPatternString(String userPatternString) {

			if (userPatternString != null) {
				ArrayList pathList = new ArrayList();
				int startParenLoc = userPatternString.indexOf('(');
				if (startParenLoc == -1) {
					// no parens here; return whole thing
					return new String[] { userPatternString };
				}
				int startingPoint = 0;
				while (startParenLoc > -1) {
					int endParenLoc = 0;
					// weed out escaped open parens and parens enclosing the
					// whole statement (in the case of valid LDAP search
					// strings: (|(something)(somethingelse))
					while ((userPatternString.charAt(startParenLoc + 1) == '|')
						|| (startParenLoc != 0 && userPatternString.charAt(startParenLoc - 1) == '\\')) {
						startParenLoc = userPatternString.indexOf("(", startParenLoc + 1);
					}
					endParenLoc = userPatternString.indexOf(")", startParenLoc + 1);
					// weed out escaped end-parens
					while (userPatternString.charAt(endParenLoc - 1) == '\\') {
						endParenLoc = userPatternString.indexOf(")", endParenLoc + 1);
					}
					String nextPathPart = userPatternString.substring(startParenLoc + 1, endParenLoc);
					pathList.add(nextPathPart);
					startingPoint = endParenLoc + 1;
					startParenLoc = userPatternString.indexOf('(', startingPoint);
				}
				return (String[]) pathList.toArray(new String[] {
				});
			}
			return null;

		}

		/**
		 * Given an LDAP search string, returns the string with certain characters
		 * escaped according to RFC 2254 guidelines.
		 * The character mapping is as follows:
		 *     char ->  Replacement
		 *    ---------------------------
		 *     *  -> \2a
		 *     (  -> \28
		 *     )  -> \29
		 *     \  -> \5c
		 *     \0 -> \00
		 * @param inString string to escape according to RFC 2254 guidelines
		 * @return
		 */
		protected String doRFC2254Encoding(String inString) {
			StringBuffer buf = new StringBuffer(inString.length());
			for (int i = 0; i < inString.length(); i++) {
				char c = inString.charAt(i);
				switch (c) {
					case '\\' :
						buf.append("\\5c");
						break;
					case '*' :
						buf.append("\\2a");
						break;
					case '(' :
						buf.append("\\28");
						break;
					case ')' :
						buf.append("\\29");
						break;
					case '\0' :
						buf.append("\\00");
						break;
					default :
						buf.append(c);
						break;
				}
			}
			return buf.toString();
		}
		//	END LDAP CODE

	public static void main(String[] args) {
	}

	}

//	   ------------------------------------------------------ Private Classes

	/**
	 * A private class representing a User
	 */
	class User {
		String username = null;
		String dn = null;
		String password = null;
		ArrayList roles = null;

		User(String username, String dn, String password, ArrayList roles) {
			this.username = username;
			this.dn = dn;
			this.password = password;
			this.roles = roles;
		}

	}

