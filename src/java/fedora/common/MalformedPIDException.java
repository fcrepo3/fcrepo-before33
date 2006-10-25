package fedora.common;

/**
 * Thrown when a PID is not well-formed.
 * <p/>
 *
 * @version $Id$
 * @author cwilper@cs.cornell.edu
 */
public class MalformedPIDException
        extends Exception {

	private static final long serialVersionUID = 1L;
	
    /**
     * Construct a MalformedPIDException with the given reason.
     */
    public MalformedPIDException(String why) {
        super(why);
    }

}
