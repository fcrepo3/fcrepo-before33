/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.security.servletfilters;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Hashtable;
*/


/** 
 *  @author Bill Niebel (niebel@virginia.edu)
 */
public class Principal implements java.security.Principal {
    private Log log = LogFactory.getLog(Principal.class);
    
    private final String name;
    
    public Principal(String name) {
    	//this.authority = null;
    	this.name = name;
    }

    public String getName() {
    	return name;
    }

    public String toString() {
    	//need to re-implement this    	
    	return "Principal[" + getName() + "]";
    }

    public int hashCode() {
    	//need to implement this    	
    	return 1;
    }

    public boolean equals(Object another) {
    	//need to implement this    	
    	return false;
    }
    
    public String[] getRoles() {
    	return new String[0];
    }
    
}
