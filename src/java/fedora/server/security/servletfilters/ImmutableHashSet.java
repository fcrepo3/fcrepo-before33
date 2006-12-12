package fedora.server.security.servletfilters;
import java.util.HashSet;
import java.util.Collection;

/** 
 *  @author Bill Niebel (niebel@virginia.edu)
 */
public class ImmutableHashSet extends HashSet {
	private static final long serialVersionUID = 1L;
	
    public boolean add(Object o) {
    	return false;
    }
    
    public void clear() {
    }

    public Object clone() {
    	return null; 
    }

    public boolean remove(Object o) {
    	return false;
    }
    
    public boolean removeAll(Collection c) {
    	return false;
    }
    
    public boolean addAll(Collection c) {
    	return false;
    }
    
    public boolean retainAll(Collection c) {
    	return false;
    }

    
}
