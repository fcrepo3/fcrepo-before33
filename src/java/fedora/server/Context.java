package fedora.server;

import java.util.Iterator;

/**
 * A holder of context name-value pairs.
 *
 * @author cwilper@cs.cornell.edu
 */
public interface Context { 

    public abstract String get(String name);
    
    public abstract Iterator names();

}