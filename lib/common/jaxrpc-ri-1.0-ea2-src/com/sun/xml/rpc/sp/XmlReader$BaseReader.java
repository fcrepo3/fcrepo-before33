// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XmlReader.java

package com.sun.xml.rpc.sp;

import java.io.*;

// Referenced classes of package com.sun.xml.rpc.sp:
//            XmlReader

abstract class XmlReader$BaseReader extends Reader {

    protected InputStream instream;
    protected byte buffer[];
    protected int start;
    protected int finish;

    XmlReader$BaseReader(InputStream stream) {
        super(stream);
        instream = stream;
        buffer = new byte[2048];
    }

    public boolean ready() throws IOException {
        return instream == null || finish - start > 0 || instream.available() != 0;
    }

    public void close() throws IOException {
        if(instream != null) {
            instream.close();
            start = finish = 0;
            buffer = null;
            instream = null;
        }
    }
}
