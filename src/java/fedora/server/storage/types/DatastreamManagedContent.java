package fedora.server.storage.types;

import fedora.server.errors.StreamIOException;
import fedora.server.storage.lowlevel.FileSystemLowlevelStorage;

import java.io.InputStream;

/**
 *
 * <p><b>Title:</b> DatastreamManagedContent.java</p>
 * <p><b>Description:</b> Managed Content.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class DatastreamManagedContent
        extends Datastream {

    public DatastreamManagedContent() {
    }

    public InputStream getContentStream()
            throws StreamIOException
    {
      try
      {
        return FileSystemLowlevelStorage.getDatastreamStore().
            retrieve(this.DSLocation);

      } catch (Throwable th)
      {
        throw new StreamIOException("[DatastreamManagedContent] returned "
            + " the error: \"" + th.getClass().getName() + "\". Reason: "
            + th.getMessage());
      }
    }
}