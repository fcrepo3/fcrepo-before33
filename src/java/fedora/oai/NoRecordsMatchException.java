package fedora.oai;

/**
 *
 * <p><b>Title:</b> NoRecordsMatchException.java</p>
 * <p><b>Description:</b> Signals that the combination of the values of the
 * from, until, set and metadataPrefix arguments results in an empty list.</p>
 *
 * <p>This may occur while fulfilling a ListIdentifiers or ListRecords request.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class NoRecordsMatchException
        extends OAIException {

    public NoRecordsMatchException() {
        super("noRecordsMatch", null);
    }

    public NoRecordsMatchException(String message) {
        super("noRecordsMatch", message);
    }

}