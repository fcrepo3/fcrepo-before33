/*
 * Created on Jun 24, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.apache.catalina.realm;


//import fedora.server.security.GenericPrincipal;

/**
 * @author wdn5e
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class IdPasswordPrincipal extends IdPrincipal {
	
	private String password = null;
	
	public IdPasswordPrincipal(String namespace, String name, String password) {
		super(namespace, name);
		this.password = password;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String toString() {
		return super.toString() + ": password=" + password;  
	}

	public static void main(String[] args) {
	}
}
