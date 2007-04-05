/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.oai;

/**
 *
 * <p><b>Title:</b> CannotDisseminateFormatException.java</p>
 * <p><b>Description:</b> Signals that the metadata format identified by the
 * value given for the metadataPrefix argument is not supported by the item or
 * by the repository.</p>
 *
 * <p>This may occur while fulfilling a GetRecord, ListIdentifiers, or ListRecords
 * request.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class CannotDisseminateFormatException
        extends OAIException {

	private static final long serialVersionUID = 1L;
	
    public CannotDisseminateFormatException() {
        super("cannotDisseminateFormat", null);
    }

    public CannotDisseminateFormatException(String message) {
        super("cannotDisseminateFormat", message);
    }

}