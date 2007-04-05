/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

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

	private static final long serialVersionUID = 1L;
	
    public IDDoesNotExistException() {
        super("idDoesNotExist", null);
    }

    public IDDoesNotExistException(String message) {
        super("idDoesNotExist", message);
    }

}