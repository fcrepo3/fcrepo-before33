package fedora.oai;

/**
 * Signals that the value of the identifier argument is unknown or illegal in
 * this repository.
 *
 * This may occur while fulfilling a GetRecord or ListMetadataFormats request.
 */

/**
 *
 * <p><b>Title:</b> </p>
 * <p><b>Description:</b> Signals that the value of the identifier argument is
 * unknown or illegal in this repository.</p>
 *
 * <p> may occur while fulfilling a GetRecord or ListMetadataFormats request.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class IDDoesNotExistException
        extends OAIException {

    public IDDoesNotExistException() {
        super("idDoesNotExist", null);
    }

    public IDDoesNotExistException(String message) {
        super("idDoesNotExist", message);
    }

}