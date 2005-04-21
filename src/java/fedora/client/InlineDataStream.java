package fedora.client;

import java.io.File;

/**
 *
 * <p><b>Title:</b> InlineDataStream.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class InlineDataStream
        extends DataStream {

    public InlineDataStream(File tempDir, String id) {
        super(tempDir, id);
    }

    public final int getType() {
        return DataStream.INLINE;
    }

}