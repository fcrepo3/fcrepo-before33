/*
 * Created on Jun 24, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.apache.catalina.realm;
import java.security.Principal;
//import fedora.server.security.GenericPrincipal;

/**
 * @author wdn5e
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RolePrincipal extends AbstractPrincipal {
	
	String name = null;
	
	public RolePrincipal(String namespace, String name) {
		super(namespace, name);
	}
	


	public static void main(String[] args) {
	}
}
