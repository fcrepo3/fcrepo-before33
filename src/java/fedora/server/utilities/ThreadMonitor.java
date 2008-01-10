/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.utilities;

/**
 * @author Chris Wilper
 */
public interface ThreadMonitor
        extends Runnable {

    public void requestStop();

}
