// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XmlReader.java

package com.sun.xml.rpc.sp;

import java.io.*;

// Referenced classes of package com.sun.xml.rpc.sp:
//            XmlReader

final class XmlReader$Utf8Reader extends XmlReader$BaseReader {

    private char nextChar;

    XmlReader$Utf8Reader(InputStream stream) {
        super(stream);
    }

    public int read(char buf[], int offset, int len) throws IOException {
        int i = 0;
        int c = 0;
        if(len <= 0)
            return 0;
        if(offset + len > buf.length || offset < 0)
            throw new ArrayIndexOutOfBoundsException();
        if(nextChar != 0) {
            buf[offset + i++] = nextChar;
            nextChar = '\0';
        }
        while(i < len)  {
            if(super.finish <= super.start) {
                if(super.instream == null) {
                    c = -1;
                    break;
                }
                super.start = 0;
                super.finish = super.instream.read(super.buffer, 0, super.buffer.length);
                if(super.finish <= 0) {
                    close();
                    c = -1;
                    break;
                }
            }
            c = super.buffer[super.start] & 0xff;
            if((c & 0x80) == 0) {
                super.start++;
                buf[offset + i++] = (char)c;
            } else {
                int off = super.start;
                try {
                    if((super.buffer[off] & 0xe0) == 192) {
                        c = (super.buffer[off++] & 0x1f) << 6;
                        c += super.buffer[off++] & 0x3f;
                    } else
                    if((super.buffer[off] & 0xf0) == 224) {
                        c = (super.buffer[off++] & 0xf) << 12;
                        c += (super.buffer[off++] & 0x3f) << 6;
                        c += super.buffer[off++] & 0x3f;
                    } else
                    if((super.buffer[off] & 0xf8) == 240) {
                        c = (super.buffer[off++] & 7) << 18;
                        c += (super.buffer[off++] & 0x3f) << 12;
                        c += (super.buffer[off++] & 0x3f) << 6;
                        c += super.buffer[off++] & 0x3f;
                        if(c > 0x10ffff)
                            throw new CharConversionException("UTF-8 encoding of character 0x00" + Integer.toHexString(c) + " can't be converted to Unicode.");
                        if(c > 65535) {
                            c -= 0x10000;
                            nextChar = (char)(56320 + (c & 0x3ff));
                            c = 55296 + (c >> 10);
                        }
                    } else {
                        throw new CharConversionException("Unconvertible UTF-8 character beginning with 0x" + Integer.toHexString(super.buffer[super.start] & 0xff));
                    }
                }
                catch(ArrayIndexOutOfBoundsException arrayindexoutofboundsexception) {
                    c = 0;
                }
                if(off > super.finish) {
                    System.arraycopy(super.buffer, super.start, super.buffer, 0, super.finish - super.start);
                    super.finish -= super.start;
                    super.start = 0;
                    off = super.instream.read(super.buffer, super.finish, super.buffer.length - super.finish);
                    if(off < 0) {
                        close();
                        throw new CharConversionException("Partial UTF-8 char");
                    }
                    super.finish += off;
                } else {
                    for(super.start++; super.start < off; super.start++)
                        if((super.buffer[super.start] & 0xc0) != 128) {
                            close();
                            throw new CharConversionException("Malformed UTF-8 char -- is an XML encoding declaration missing?");
                        }

                    buf[offset + i++] = (char)c;
                    if(nextChar != 0 && i < len) {
                        buf[offset + i++] = nextChar;
                        nextChar = '\0';
                    }
                }
            }
        }
        if(i > 0)
            return i;
        else
            return c != -1 ? 0 : -1;
    }
}
