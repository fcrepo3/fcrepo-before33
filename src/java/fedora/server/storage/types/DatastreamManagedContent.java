
package fedora.server.storage.types;

import java.io.File;
import java.io.InputStream;

import fedora.common.Constants;

import fedora.server.Server;
import fedora.server.errors.InitializationException;
import fedora.server.errors.StreamIOException;
import fedora.server.storage.lowlevel.ILowlevelStorage;

/**
 * <p>
 * <b>Title:</b> DatastreamManagedContent.java
 * </p>
 * <p>
 * <b>Description:</b> Managed Content.
 * </p>
 * 
 * @author cwilper@cs.cornell.edu
 * @version $Id: DatastreamManagedContent.java,v 1.14 2006/02/02 21:05:07
 *          cwilper Exp $
 */
public class DatastreamManagedContent
        extends Datastream {

    /**
     * Internal scheme to indicating that a copy should made of the resource.
     */
    public static final String COPY_SCHEME = "copy://";

    public static final String TEMP_SCHEME = "temp://";

    public static final String UPLOADED_SCHEME = "uploaded://";

    private static ILowlevelStorage s_llstore;

    public DatastreamManagedContent() {
    }

    @Override
    public Datastream copy() {
        DatastreamManagedContent ds = new DatastreamManagedContent();
        copy(ds);
        return ds;
    }

    private ILowlevelStorage getLLStore() throws Exception {
        if (s_llstore == null) {
            try {
                Server server =
                        Server.getInstance(new File(Constants.FEDORA_HOME),
                                           false);
                s_llstore =
                        (ILowlevelStorage) server
                                .getModule("fedora.server.storage.lowlevel.ILowlevelStorage");
            } catch (InitializationException ie) {
                throw new Exception("Unable to get LLStore Module: "
                        + ie.getMessage(), ie);
            }
        }
        return s_llstore;
    }

    @Override
    public InputStream getContentStream() throws StreamIOException {
        try {
            return getLLStore().retrieveDatastream(DSLocation);
        } catch (Throwable th) {
            throw new StreamIOException("[DatastreamManagedContent] returned "
                    + " the error: \"" + th.getClass().getName()
                    + "\". Reason: " + th.getMessage());
        }
    }
}
