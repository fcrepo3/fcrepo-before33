package fedora.oai;

/**
 *
 * <p><b>Title:</b> BadVerbException.java</p>
 * <p><b>Description:</b> Signals that the value of the verb argument is not a
 * legal OAI-PMH verb, the verb argument is missing, or the verb argument is
 * repeated. </p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class BadVerbException
        extends OAIException {

    public BadVerbException() {
        super("badVerb", null);
    }

    public BadVerbException(String message) {
        super("badVerb", message);
    }

}