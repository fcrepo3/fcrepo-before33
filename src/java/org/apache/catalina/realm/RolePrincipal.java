package org.apache.catalina.realm;

/**
 * @author wdn5e
 */
public class RolePrincipal extends AbstractPrincipal {
	
	String name = null;
	
	public RolePrincipal(String namespace, String name) {
		super(namespace, name);
	}

	public static void main(String[] args) {
	}
}
