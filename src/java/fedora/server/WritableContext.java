package fedora.server;

import java.util.Iterator;
import java.util.Map;

/**
 * A Context object whose values can be written after instantiation.
 *
 * @author cwilper@cs.cornell.edu
 */
public class WritableContext 
        extends Parameterized implements Context {

    /**
     * Creates and initializes the <code>WritableContext</code>.
     * <p></p>
     * @param contextParameters A pre-loaded Map of name-value pairs 
     *        comprising the context.
     */
    public WritableContext(Map parameters) {
        super(parameters);
    }

    public String get(String name) {
        return getParameter(name);
    }

    public void set(String name, String value) {
        setParameter(name, value);
    }
    
    public Iterator names() {
        return parameterNames();
    }

}