// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   StreamingParserImpl.java

package com.sun.xml.rpc.sp;

import java.io.*;

// Referenced classes of package com.sun.xml.rpc.sp:
//            StreamingParser, Parser, ParseException, StreamingParserFactory

public final class StreamingParserImpl extends StreamingParser {

    private Parser parser;
    private static final int DOC_END = -1;
    private static final int DOC_START = -2;
    private static final int EMPTY = -3;
    private static final int EXCEPTION = -4;
    private int cur;
    private String curName;
    private String curValue;
    private String curURI;
    private boolean validating;
    private boolean coalescing;
    private boolean namespaceAware;
    private int curLine;
    private int curCol;
    private String publicId;
    private String systemId;

    private StreamingParserImpl(StreamingParserFactory pf) {
        parser = null;
        cur = -3;
        curName = null;
        curValue = null;
        curURI = null;
        curLine = -1;
        curCol = -1;
        publicId = null;
        systemId = null;
        validating = pf.isValidating();
        coalescing = pf.isCoalescing();
        namespaceAware = pf.isNamespaceAware();
    }

    StreamingParserImpl(StreamingParserFactory pf, InputStream in) {
        this(pf);
        parser = new Parser(in, coalescing, namespaceAware);
    }

    StreamingParserImpl(StreamingParserFactory pf, File file) throws IOException {
        this(pf);
        parser = new Parser(file, coalescing, namespaceAware);
    }

    public int parse() throws ParseException, IOException {
        if(cur == -1) {
            return -1;
        } else {
            cur = parser.parse();
            curName = parser.getCurName();
            curValue = parser.getCurValue();
            curURI = parser.getCurURI();
            curLine = parser.getLineNumber();
            curCol = parser.getColumnNumber();
            return cur;
        }
    }

    public int state() {
        if(cur == -3)
            throw new IllegalStateException("Parser not started");
        if(cur < -1)
            throw new InternalError();
        else
            return cur;
    }

    public String name() {
        if(curName == null)
            throw new IllegalStateException("Name not defined in this state");
        else
            return curName;
    }

    public String value() {
        if(curValue == null)
            throw new IllegalStateException("Value not defined in this state");
        else
            return curValue;
    }

    public String uriString() {
        if(!namespaceAware)
            return null;
        if(curURI == null)
            throw new IllegalStateException("Value not defined in this state");
        else
            return curURI;
    }

    public int line() {
        return curLine;
    }

    public int column() {
        return curCol;
    }

    public String publicId() {
        return publicId;
    }

    public String systemId() {
        return systemId;
    }

    public boolean isValidating() {
        return validating;
    }

    public boolean isCoalescing() {
        return coalescing;
    }

    public boolean isNamespaceAware() {
        return namespaceAware;
    }

    public String describe(boolean articleNeeded) {
        return StreamingParser.describe(cur, curName, curValue, articleNeeded);
    }
}
