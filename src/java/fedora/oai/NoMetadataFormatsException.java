package fedora.oai;

/**
 *
 * <p><b>Title:</b> NoMetadataFormatsException.java</p>
 * <p><b>Description:</b> Signals that there are no metadata formats available
 * for the specified item.</p>
 *
 * <p>This may occur while fulfilling a ListMetadataFormats request.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class NoMetadataFormatsException
        extends OAIException {

    public NoMetadataFormatsException() {
        super("noMetadataFormats", null);
    }

    public NoMetadataFormatsException(String message) {
        super("noMetadataFormats", message);
    }

}