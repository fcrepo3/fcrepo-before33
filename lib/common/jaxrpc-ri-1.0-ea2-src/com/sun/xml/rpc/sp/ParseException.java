// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ParseException.java

package com.sun.xml.rpc.sp;


// Referenced classes of package com.sun.xml.rpc.sp:
//            StreamingParser

public class ParseException extends Exception {

    private String publicId;
    private String systemId;
    private int line;
    private int col;

    public ParseException(String message, String publicId, String systemId, int line, int col) {
        super(message);
        this.publicId = publicId;
        this.systemId = systemId;
        this.line = line;
        this.col = col;
    }

    public ParseException(String message, StreamingParser parser) {
        this(message, parser.publicId(), parser.systemId(), parser.line(), parser.column());
    }

    public ParseException(String message, String publicId, String systemId) {
        this(message, publicId, systemId, -1, -1);
    }

    public ParseException(String message) {
        this(message, null, null, -1, -1);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getName());
        if(publicId != null)
            sb.append(": " + publicId);
        if(systemId != null)
            sb.append(": " + systemId);
        if(line != -1) {
            sb.append(":" + line);
            if(col != -1)
                sb.append("," + col);
        }
        if(getMessage() != null)
            sb.append(": " + getMessage());
        return sb.toString();
    }
}
