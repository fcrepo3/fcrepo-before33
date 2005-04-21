package fedora.server;

/**
 *
 * <p><b>Title:</b> Debug.java</p>
 * <p><b>Description:</b> Provides static boolean that can be checked
 * for conditional debugging.  The default value is false.  Normally
 * the value is affected by the running server instance's fedora.fcfg file
 * "debug" parameter.<br/>
 * <b>Example Use:<b><br/>
 * <pre>
 * if (fedora.server.Debug.DEBUG) System.out.println("Something");
 * </pre></p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public abstract class Debug {

    public static boolean DEBUG = false;

}
