/*
 * Created on Jun 24, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.apache.catalina.realm;


/**
 * @author wdn5e
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AbstractPrincipal implements java.security.Principal {

	private final String namespace;
	private final String name;
	
	AbstractPrincipal(String namespace, String name) {
		this.namespace = namespace;
		this.name = name;
	}
	
	public String getNamespace() {
		return namespace;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean equals(Object that) {
		boolean comparison = false;
		try {
			comparison = 
				this.getClass().getName().equals(that.getClass().getName()) 
				&& getNamespace().equals(((AbstractPrincipal)that).getNamespace())
				&& getName().equals(((AbstractPrincipal)that).getName());

		} catch (Exception e) {
		}
		return comparison;
	}
	
	public int hashCode() {
		return (namespace + "::" + name).hashCode();
	}

	public String toString() {
		return this.getClass().getName() + ": namespace=" + namespace + ": name=" + name;  
	}


	public static void main(String[] args) {
	}
}
