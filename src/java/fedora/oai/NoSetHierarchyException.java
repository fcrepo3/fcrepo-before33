package fedora.oai;

/**
 *
 * <p><b>Title:</b> NoSetHierarchyException.java</p>
 * <p><b>Description:</b> Signals that the repository does not support sets.</p>
 *
 * <p>This may occur while fulfilling a ListSets, ListIdentifiers, or ListRecords
 * request.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class NoSetHierarchyException
        extends OAIException {

    public NoSetHierarchyException() {
        super("noSetHierarchy", null);
    }

    public NoSetHierarchyException(String message) {
        super("noSetHierarchy", message);
    }

}