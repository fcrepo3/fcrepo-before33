package fedora.oai;

import java.io.InputStream;
import java.util.Set;

/**
 *
 * <p><b>Title:</b> Record.java</p>
 * <p><b>Description:</b> Metadata expressed in a single format with a header
 * and optional "about" data which is descriptive of the metadata (such as
 * rights of provenance statements).</p>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version 1.0
 * @see <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#Record">
 *      http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#Record</a>
 */
public interface Record {

    /**
     * Get the header portion of the record.
     */
    public abstract Header getHeader();

    /**
     * Get the metadata portion of the record.  This must be an xml chunk
     * in which the W3C schema is identified by the root element's
     * xsi:schemaLocation attribute.
     *
     * If getHeader().isAvailable() is false, this may be null.
     */
    public abstract String getMetadata();

    /**
     * Get the 'about' portions of the record.
     *
     * There will be zero or more items in the resulting Set.
     * These are descriptors of the metadata.  These must be xml chunks
     * in which the W3C schema is identified by the root element's
     * xsi:schemaLocation attribute.
     *
     * If getHeader().isAvailable() is false, this may be null.
     */
    public abstract Set getAbouts();

}