package fedora.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Context that is read-only.
 *
 * @author cwilper@cs.cornell.edu
 */
public class ReadOnlyContext 
        extends Parameterized implements Context {

    public static ReadOnlyContext EMPTY=new ReadOnlyContext(null);

    /**
     * Creates and initializes the <code>Context</code>.
     *
     * @param contextParameters A pre-loaded Map of name-value pairs 
     *        comprising the context.
     */
    public ReadOnlyContext(Map parameters) {
        super(parameters);
    }

    public static ReadOnlyContext getCopy(Context source) {
        HashMap params=new HashMap();
        Iterator iter;
        iter=source.names();
        while (iter.hasNext()) {
            String k=(String) iter.next();
            params.put(k, source.get(k));
        }
        return new ReadOnlyContext(params);
    }

    public static ReadOnlyContext getUnion(Context a, Context b) {
        if (a==null) {
            if (b==null) {
                return EMPTY;
            } else {
                if (b.getClass().getName().equals("fedora.server.ReadOnlyContext")) {
                    return (ReadOnlyContext) b;
                } else {
                    return getCopy(b);
                }
            }
        } else {
            if (b==null) {
                if (a.getClass().getName().equals("fedora.server.ReadOnlyContext")) {
                    return (ReadOnlyContext) a;
                } else {
                    return getCopy(a);
                }
            } else {
                // read from a, then b, then create and return new ReadOnlyContext
                HashMap params=new HashMap();
                Iterator iter;
                iter=a.names();
                while (iter.hasNext()) {
                    String k=(String) iter.next();
                    params.put(k, a.get(k));
                }
                iter=b.names();
                while (iter.hasNext()) {
                    String k=(String) iter.next();
                    params.put(k, b.get(k));
                }
                return new ReadOnlyContext(params);
            }
        }
    }

    public String get(String name) {
        return getParameter(name);
    }
    
    public Iterator names() {
        return parameterNames();
    }

}