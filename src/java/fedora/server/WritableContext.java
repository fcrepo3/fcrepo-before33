package fedora.server;

import java.util.Iterator;
import java.util.Map;

/**
 *
 * <p><b>Title:</b> WritableContext.java</p>
 * <p><b>Description:</b> A Context object whose values can be written after
 * instantiation.</p>
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