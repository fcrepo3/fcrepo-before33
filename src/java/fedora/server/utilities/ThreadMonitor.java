package fedora.server.utilities;


/**
 *
 * <p><b>Title:</b> ThreadMonitor.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public interface ThreadMonitor
        extends Runnable {

    public void requestStop();

}