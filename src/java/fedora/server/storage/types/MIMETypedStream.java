package fedora.server.storage.types;

import java.io.InputStream;

/**
 *
 * <p><b>Title:</b> MIMETypedStream.java</p>
 * <p><b>Description:</b> Data structure for holding a MIME-typed stream.</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author rlw@virginia.edu
 * @version $Id$
 */
public class MIMETypedStream
{
    public String MIMEType;
    private InputStream stream;
    public Property[] header;

    /**
     * <p>Constructs a MIMETypedStream.</p>
     *
     * @param MIMEType The MIME type of the byte stream.
     * @param stream The byte stream.
     */
    public MIMETypedStream(String MIMEType, InputStream stream, Property[] header)
    {
        this.MIMEType = MIMEType;
        this.header = header;
        this.setStream(stream);
    }

    public InputStream getStream()
    {
      return stream;
    }

    public void setStream(InputStream stream)
    {
      this.stream = stream;
    }
}