// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XmlReader.java

package com.sun.xml.rpc.sp;

import java.io.IOException;
import java.io.InputStream;

// Referenced classes of package com.sun.xml.rpc.sp:
//            XmlReader

final class XmlReader$Iso8859_1Reader extends XmlReader$BaseReader {

    XmlReader$Iso8859_1Reader(InputStream in) {
        super(in);
    }

    public int read(char buf[], int offset, int len) throws IOException {
        if(super.instream == null)
            return -1;
        if(offset + len > buf.length || offset < 0)
            throw new ArrayIndexOutOfBoundsException();
        int i;
        for(i = 0; i < len; i++) {
            if(super.start >= super.finish) {
                super.start = 0;
                super.finish = super.instream.read(super.buffer, 0, super.buffer.length);
                if(super.finish <= 0) {
                    if(super.finish <= 0)
                        close();
                    break;
                }
            }
            buf[offset + i] = (char)(0xff & super.buffer[super.start++]);
        }

        if(i == 0 && super.finish <= 0)
            return -1;
        else
            return i;
    }
}
