// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   StreamingParser.java

package com.sun.xml.rpc.streaming;

import com.sun.xml.rpc.sp.ParseException;
import com.sun.xml.rpc.sp.Parser;
import java.io.*;

// Referenced classes of package com.sun.xml.rpc.streaming:
//            StreamingException, Stream

public final class StreamingParser {

    public static final int START = 0;
    public static final int END = 1;
    public static final int ATTR = 2;
    public static final int CHARS = 3;
    public static final int IWS = 4;
    public static final int PI = 5;
    public static final int AT_END = 6;
    private static final int DOC_END = -1;
    private static final int DOC_START = -2;
    private static final int EMPTY = -3;
    private static final int EXCEPTION = -4;
    private Parser parser;
    private int currentState;
    private String currentName;
    private String currentValue;
    private String currentURI;
    private int currentLine;

    public StreamingParser(InputStream in) {
        parser = null;
        currentState = -3;
        currentName = null;
        currentValue = null;
        currentURI = null;
        currentLine = -1;
        parser = new Parser(in, true, true);
    }

    public StreamingParser(File file) throws IOException {
        parser = null;
        currentState = -3;
        currentName = null;
        currentValue = null;
        currentURI = null;
        currentLine = -1;
        parser = new Parser(file, true, true);
    }

    public Stream getStream() {
        return new StreamingParser$1(this);
    }

    public int next() {
        if(currentState == 6)
            return 6;
        try {
            currentState = parser.parse();
            if(currentState == -1)
                currentState = 6;
        }
        catch(ParseException e) {
            throw new StreamingException(e);
        }
        catch(IOException e) {
            throw new StreamingException(e);
        }
        currentName = parser.getCurName();
        currentValue = parser.getCurValue();
        currentURI = parser.getCurURI();
        currentLine = parser.getLineNumber();
        return currentState;
    }

    public int getState() {
        if(currentState == -3)
            throw new IllegalStateException("parser not started");
        if(currentState < -4)
            throw new InternalError();
        else
            return currentState;
    }

    public String getName() {
        return currentName;
    }

    public String getValue() {
        return currentValue;
    }

    public String getURI() {
        return currentURI;
    }

    static int access$000(StreamingParser x0) {
        return x0.currentState;
    }

    static String access$100(StreamingParser x0) {
        return x0.currentName;
    }

    static String access$200(StreamingParser x0) {
        return x0.currentValue;
    }

    static String access$300(StreamingParser x0) {
        return x0.currentURI;
    }

    static int access$400(StreamingParser x0) {
        return x0.currentLine;
    }
}
