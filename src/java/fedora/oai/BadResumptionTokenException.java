package fedora.oai;

/**
 *
 * <p><b>Title:</b> BadResumptionTokenException.java</p>
 * <p><b>Description:</b> Signals that the value of the resumptionToken argument
 * is invalid or expired.</p>
 *
 * <p>This may occur while fulfilling a ListIdentifiers, ListRecords, or ListSets
 * request.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class BadResumptionTokenException
        extends OAIException {

    public BadResumptionTokenException() {
        super("badResumptionToken", null);
    }

    public BadResumptionTokenException(String message) {
        super("badResumptionToken", message);
    }

}