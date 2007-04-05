/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage;

// import fedora.server.ParameterizedComponent;

import java.net.URL;
import java.util.Iterator;

/**
 *
 * <p><b>Title:</b> DORegistry.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public abstract class DORegistry {
//        extends ParameterizedComponent {

    public abstract URL add();

    public abstract URL get(String PID);

    public abstract void remove();

    public abstract Iterator ids();

}