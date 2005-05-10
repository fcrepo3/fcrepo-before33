package org.apache.catalina.realm;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.catalina.Container;

public final class JAASNullLoginModule implements LoginModule {
	
	private CallbackHandler callbackHandler = null;
	
	protected CallbackHandler getCallbackHandler() {
		return callbackHandler;
	}
	
	private boolean loginModuleIsContingent = true; // safest default

	protected boolean debug = false;

	protected Map options = null;

	protected Map sharedState = null;

	private Subject subject = null;
	
	//private Realm realm = null;
	private String namespace = null;
	
	private GenericPrincipal principal = null;
	
	protected static final int UNREADY = 0;
	protected static final int READY = 1;
	protected static final int ACK = 2;
	protected static final int NACK = 3;
	protected static final int IN_SUBJ = 4;
	
	protected int state = UNREADY;


	// --------------------------------------------------------- Public Methods


	/**
	 * Initialize this <code>LoginModule</code> with the specified
	 * configuration information.
	 *
	 * @param subject The <code>Subject</code> to be authenticated
	 * @param callbackHandler A <code>CallbackHandler</code> for communicating
	 *  with the end user as necessary
	 * @param sharedState State information shared with other
	 *  <code>LoginModule</code> instances
	 * @param options Configuration information for this specific
	 *  <code>LoginModule</code> instance
	 */
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
		log("JAASMLM init");
		log("initialize()");
		principal = null;
		this.state = UNREADY;		
		if ((subject != null) && (callbackHandler != null)) {
			this.subject = subject;
			this.callbackHandler = callbackHandler;
			this.sharedState = sharedState;
			if (this.sharedState == null) {
				this.sharedState = new Hashtable();
			}
			this.options = options;
			if (this.options == null) {
				this.options = new Hashtable();
			}
			this.debug = "true".equalsIgnoreCase((String) options.get("debug"));
			//String realmClassName = "";
			namespace = this.getClass().getName();
			try {
				//realmClassName = (String) options.get("realm");
				//Class[] formalArgs = new Class[] {};
				//Object[] actualArgs = new Object[] {};
				//Constructor constructor = Class.forName(realmClassName).getConstructor(formalArgs);
				//realm = (Realm) constructor.newInstance(actualArgs);
				//namespace =  realm.getClass().getName();
				//if ("org.apache.catalina.realm.MemoryRealm".equals(realmClassName)) {
					//((MemoryRealm)realm).start();					
				//}
				this.state = READY;				
			} catch (Throwable e) {
				//log("couldn't call constructor " + realmClassName + "():  " + e.getClass().getName(), e);
			}				
		}
		log("/initialize()");
	}

	private void rollback() {
		log("JAASMLM rollback");
    	Iterator principals = subject.getPrincipals().iterator();
    	while (principals.hasNext()) {
    		AbstractPrincipal testPrincipal = ((AbstractPrincipal)principals.next());
    		if (namespace.equals(testPrincipal.getNamespace())) {
            	subject.getPrincipals().remove(testPrincipal);
    		}
    	}	
		principal = null;
		state = READY;		
	}

	private String password = "";

	
	/**
	 * Phase 1 of authenticating a <code>Subject</code>.
	 *
	 * @return <code>true</code> if the authentication succeeded, or
	 *  <code>false</code> if this <code>LoginModule</code> should be
	 *  ignored
	 *
	 * @exception LoginException if the authentication fails
	 */
	public boolean login() throws LoginException {
		log("JAASMLM login");
		log("login()");
		state = NACK;
		principal = null;
		String username = null;
		//String password = null;
        if (callbackHandler == null)
            throw new LoginException("No CallbackHandler specified");
        Callback callbacks[] = new Callback[2];
        callbacks[0] = new NameCallback("Username: ");
        callbacks[1] = new PasswordCallback("Password: ", false);
        try {
            callbackHandler.handle(callbacks);
            username = ((NameCallback) callbacks[0]).getName();
            password = new String(((PasswordCallback) callbacks[1]).getPassword());
        } catch (IOException e) {
            throw new LoginException(e.toString());
        } catch (UnsupportedCallbackException e) {
            throw new LoginException(e.toString());
        }
		log("login(): username=" + username);
		log("login(): password=" + password);
		//log("login(): realm=" + realm);
		//log("login(): getPath()=" + getPathname());
		//log("login(): getPath()=" + ((MemoryRealm)realm).getPathname());
		if (JAASRealm.DONTCHECK.equals(password)) {
	        principal = new GenericPrincipal(null, username, password);			
		}
		//GenericPrincipal principal = (GenericPrincipal) realm.authenticate(username, password);
		log("login(): principal=" + principal);		
		if (principal != null) {
			state = ACK;
		}
		boolean rc = (state == ACK);
		log("/login(): " + rc);
		return rc;
	}
	
	public boolean commit() throws LoginException {
		log("JAASMLM commit");
		log("commit()");
		boolean rc = (state == ACK);
		if (rc) {
			log("commit(): subject=" + subject);
			log("commit(): subject.getPrincipals()=" + subject.getPrincipals());
			log("commit(): namespace=" + namespace);
			log("commit(): principal=" + principal);
			if (! loginModuleIsContingent) {
	        	subject.getPrincipals().add(new IdPasswordPrincipal(namespace, principal.getName(), password));				
			}
			if (loginModuleIsContingent && subject.getPrincipals().isEmpty()) {
				log("login():  prev. login modules were not successful; do -not- return roles");
			} else {
	           	String[] roles = principal.getRoles();
	           	for (int i = 0; i < roles.length; i++) {
	            	subject.getPrincipals().add(new RolePrincipal(namespace, roles[i]));
	           	}
			}
			state = IN_SUBJ;
		}
		log("/commit(): true");
		return true;
	}

	/**
	 * Phase 2 of authenticating a <code>Subject</code> when Phase 1
	 * fails.  This method is called if the <code>LoginContext</code>
	 * failed somewhere in the overall authentication chain.
	 *
	 * @return <code>true</code> if this method succeeded, or
	 *  <code>false</code> if this <code>LoginModule</code> should be
	 *  ignored
	 *
	 * @exception LoginException if the abort fails
	 */
	public boolean abort() throws LoginException {
		log("JAASMLM abort");
		log("abort()");
		boolean rc = (state == ACK);
		//clear(); ?
		//state == ???
		rollback();
		log("/abort(): " + rc);
		return rc;
	}
		
	/**
	 * Log out this user.
	 *
	 * @return <code>true</code> in all cases because the
	 *  <code>LoginModule</code> should not be ignored
	 *
	 * @exception LoginException if logging out failed
	 */
	public boolean logout() throws LoginException {
		log("JAASMLM logout");
		log("logout()");		
		rollback();
		//clear(); ?
		//state == ???
		boolean rc = true;
		log("/logout(): " + rc);
		return rc;
	}


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
	
	//fixup
	private static Container staticContainer = null;
	public static void setStaticContainer(Container container) {
		staticContainer = container;
	}

}


