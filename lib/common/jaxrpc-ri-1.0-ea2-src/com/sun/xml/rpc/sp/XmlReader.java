// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XmlReader.java

package com.sun.xml.rpc.sp;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

final class XmlReader extends Reader {

    private static final int MAXPUSHBACK = 512;
    private Reader in;
    private String assignedEncoding;
    private boolean closed;
    private static final Map charsets;

    public static Reader createReader(InputStream in) throws IOException {
        return new XmlReader(in);
    }

    public static Reader createReader(InputStream in, String encoding) throws IOException {
        if(encoding == null)
            return new XmlReader(in);
        if("UTF-8".equalsIgnoreCase(encoding) || "UTF8".equalsIgnoreCase(encoding))
            return new XmlReader$Utf8Reader(in);
        if("US-ASCII".equalsIgnoreCase(encoding) || "ASCII".equalsIgnoreCase(encoding))
            return new XmlReader$AsciiReader(in);
        if("ISO-8859-1".equalsIgnoreCase(encoding))
            return new XmlReader$Iso8859_1Reader(in);
        else
            return new InputStreamReader(in, std2java(encoding));
    }

    private static String std2java(String encoding) {
        String temp = encoding.toUpperCase();
        temp = (String)charsets.get(temp);
        return temp == null ? encoding : temp;
    }

    public String getEncoding() {
        return assignedEncoding;
    }

    private XmlReader(InputStream stream) throws IOException {
        super(stream);
        PushbackInputStream pb;
        if(stream instanceof PushbackInputStream)
            pb = (PushbackInputStream)stream;
        else
            pb = new PushbackInputStream(stream, 512);
        byte buf[] = new byte[4];
        int len = pb.read(buf);
        if(len > 0)
            pb.unread(buf, 0, len);
        if(len == 4)
label0:
            switch(buf[0] & 0xff) {
            default:
                break;

            case 0: // '\0'
                if(buf[1] == 60 && buf[2] == 0 && buf[3] == 63) {
                    setEncoding(pb, "UnicodeBig");
                    return;
                }
                break;

            case 60: // '<'
                switch(buf[1] & 0xff) {
                default:
                    break label0;

                case 0: // '\0'
                    if(buf[2] == 63 && buf[3] == 0) {
                        setEncoding(pb, "UnicodeLittle");
                        return;
                    }
                    break label0;

                case 63: // '?'
                    break;
                }
                if(buf[2] == 120 && buf[3] == 109) {
                    useEncodingDecl(pb, "UTF8");
                    return;
                }
                break;

            case 76: // 'L'
                if(buf[1] == 111 && (0xff & buf[2]) == 167 && (0xff & buf[3]) == 148) {
                    useEncodingDecl(pb, "CP037");
                    return;
                }
                break;

            case 254: 
                if((buf[1] & 0xff) == 255) {
                    setEncoding(pb, "UTF-16");
                    return;
                }
                break;

            case 255: 
                if((buf[1] & 0xff) == 254) {
                    setEncoding(pb, "UTF-16");
                    return;
                }
                break;
            }
        setEncoding(pb, "UTF-8");
    }

    private void useEncodingDecl(PushbackInputStream pb, String encoding) throws IOException {
        byte buffer[] = new byte[512];
        int len = pb.read(buffer, 0, buffer.length);
        pb.unread(buffer, 0, len);
        Reader r = new InputStreamReader(new ByteArrayInputStream(buffer, 4, len), encoding);
        int c;
        if((c = r.read()) != 108) {
            setEncoding(pb, "UTF-8");
            return;
        }
        StringBuffer buf = new StringBuffer();
        StringBuffer keyBuf = null;
        String key = null;
        boolean sawEq = false;
        char quoteChar = '\0';
        boolean sawQuestion = false;
label0:
        for(int i = 0; i < 507; i++) {
            if((c = r.read()) == -1)
                break;
            if(c == 32 || c == 9 || c == 10 || c == 13)
                continue;
            if(i == 0)
                break;
            if(c == 63)
                sawQuestion = true;
            else
            if(sawQuestion) {
                if(c == 62)
                    break;
                sawQuestion = false;
            }
            if(key == null || !sawEq) {
                if(keyBuf == null) {
                    if(!Character.isWhitespace((char)c)) {
                        keyBuf = buf;
                        buf.setLength(0);
                        buf.append((char)c);
                        sawEq = false;
                    }
                } else
                if(Character.isWhitespace((char)c))
                    key = keyBuf.toString();
                else
                if(c == 61) {
                    if(key == null)
                        key = keyBuf.toString();
                    sawEq = true;
                    keyBuf = null;
                    quoteChar = '\0';
                } else {
                    keyBuf.append((char)c);
                }
                continue;
            }
            if(Character.isWhitespace((char)c))
                continue;
            if(c == 34 || c == 39) {
                if(quoteChar == 0) {
                    quoteChar = (char)c;
                    buf.setLength(0);
                    continue;
                }
                if(c == quoteChar) {
                    if("encoding".equals(key)) {
                        assignedEncoding = buf.toString();
                        for(i = 0; i < assignedEncoding.length(); i++) {
                            c = assignedEncoding.charAt(i);
                            if((c < 65 || c > 90) && (c < 97 || c > 122) && (i == 0 || i <= 0 || c != 45 && (c < 48 || c > 57) && c != 46 && c != 95))
                                break label0;
                        }

                        setEncoding(pb, assignedEncoding);
                        return;
                    }
                    key = null;
                    continue;
                }
            }
            buf.append((char)c);
        }

        setEncoding(pb, "UTF-8");
    }

    private void setEncoding(InputStream stream, String encoding) throws IOException {
        assignedEncoding = encoding;
        in = createReader(stream, encoding);
    }

    public int read(char buf[], int off, int len) throws IOException {
        if(closed)
            return -1;
        int val = in.read(buf, off, len);
        if(val == -1)
            close();
        return val;
    }

    public int read() throws IOException {
        if(closed)
            throw new IOException("closed");
        int val = in.read();
        if(val == -1)
            close();
        return val;
    }

    public boolean markSupported() {
        return in != null ? in.markSupported() : false;
    }

    public void mark(int value) throws IOException {
        if(in != null)
            in.mark(value);
    }

    public void reset() throws IOException {
        if(in != null)
            in.reset();
    }

    public long skip(long value) throws IOException {
        return in != null ? in.skip(value) : 0L;
    }

    public boolean ready() throws IOException {
        return in != null ? in.ready() : false;
    }

    public void close() throws IOException {
        if(closed) {
            return;
        } else {
            in.close();
            in = null;
            closed = true;
            return;
        }
    }

    static  {
        charsets = new HashMap(31);
        charsets.put("UTF-16", "Unicode");
        charsets.put("ISO-10646-UCS-2", "Unicode");
        charsets.put("EBCDIC-CP-US", "cp037");
        charsets.put("EBCDIC-CP-CA", "cp037");
        charsets.put("EBCDIC-CP-NL", "cp037");
        charsets.put("EBCDIC-CP-WT", "cp037");
        charsets.put("EBCDIC-CP-DK", "cp277");
        charsets.put("EBCDIC-CP-NO", "cp277");
        charsets.put("EBCDIC-CP-FI", "cp278");
        charsets.put("EBCDIC-CP-SE", "cp278");
        charsets.put("EBCDIC-CP-IT", "cp280");
        charsets.put("EBCDIC-CP-ES", "cp284");
        charsets.put("EBCDIC-CP-GB", "cp285");
        charsets.put("EBCDIC-CP-FR", "cp297");
        charsets.put("EBCDIC-CP-AR1", "cp420");
        charsets.put("EBCDIC-CP-HE", "cp424");
        charsets.put("EBCDIC-CP-BE", "cp500");
        charsets.put("EBCDIC-CP-CH", "cp500");
        charsets.put("EBCDIC-CP-ROECE", "cp870");
        charsets.put("EBCDIC-CP-YU", "cp870");
        charsets.put("EBCDIC-CP-IS", "cp871");
        charsets.put("EBCDIC-CP-AR2", "cp918");
    }
}
