/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

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