package fedora.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * <p><b>Title:</b> ReadOnlyContext.java</p>
 * <p><b>Description:</b> Context that is read-only.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class ReadOnlyContext
        extends Parameterized implements Context {

    public static ReadOnlyContext EMPTY=new ReadOnlyContext(null);

    /**
     * Creates and initializes the <code>Context</code>.
     *
     * @param parameters A pre-loaded Map of name-value pairs
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