// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   StreamingParser.java

package com.sun.xml.rpc.sp;

import java.io.IOException;

// Referenced classes of package com.sun.xml.rpc.sp:
//            ParseException

public abstract class StreamingParser {

    public static final int START = 0;
    public static final int END = 1;
    public static final int ATTR = 2;
    public static final int CHARS = 3;
    public static final int IWS = 4;
    public static final int PI = 5;

    protected StreamingParser() {
    }

    public abstract int parse() throws ParseException, IOException;

    public abstract int state();

    public abstract String name();

    public abstract String value();

    public abstract String uriString();

    public abstract int line();

    public abstract int column();

    public abstract String publicId();

    public abstract String systemId();

    public abstract boolean isValidating();

    public abstract boolean isCoalescing();

    public abstract boolean isNamespaceAware();

    private static void quote(StringBuffer sb, String s, int max) {
        boolean needDots = false;
        int limit = Math.min(s.length(), max);
        if(limit > max - 3) {
            needDots = true;
            limit = max - 3;
        }
        sb.append('"');
        for(int i = 0; i < limit; i++) {
            char c = s.charAt(i);
            if(c < ' ' || c > '~') {
                if(c <= '\377') {
                    if(c == '\n')
                        sb.append("\\n");
                    else
                    if(c == '\r') {
                        sb.append("\\r");
                    } else {
                        sb.append("\\x");
                        if(c < '\020')
                            sb.append('0');
                        sb.append(Integer.toHexString(c));
                    }
                } else
                if(c == '"') {
                    sb.append("\\\"");
                } else {
                    sb.append("\\u");
                    String n = Integer.toHexString(c);
                    for(int j = n.length(); j < 4; j++)
                        sb.append('0');

                    sb.append(n);
                }
            } else {
                sb.append(c);
            }
        }

        if(needDots)
            sb.append("...");
        sb.append('"');
    }

    public static String describe(int state, String name, String value, boolean articleNeeded) {
        StringBuffer sb = new StringBuffer();
        switch(state) {
        case 0: // '\0'
            if(articleNeeded)
                sb.append("a ");
            sb.append("start tag");
            if(name != null)
                sb.append(" for a \"" + name + "\" element");
            break;

        case 1: // '\001'
            if(articleNeeded)
                sb.append("an ");
            sb.append("end tag");
            if(name != null)
                sb.append(" for a \"" + name + "\" element");
            break;

        case 2: // '\002'
            if(name == null) {
                if(articleNeeded)
                    sb.append("an ");
                sb.append("attribute");
                break;
            }
            if(articleNeeded)
                sb.append("the ");
            sb.append("attribute \"" + name + "\"");
            if(value != null)
                sb.append(" with value \"" + value + "\"");
            break;

        case 3: // '\003'
            if(articleNeeded)
                sb.append("some ");
            sb.append("character data");
            if(value != null) {
                sb.append(": ");
                quote(sb, value, 40);
            }
            break;

        case 4: // '\004'
            if(articleNeeded)
                sb.append("some ");
            sb.append("ignorable whitespace");
            break;

        case 5: // '\005'
            if(articleNeeded)
                sb.append("a ");
            sb.append("processing instruction");
            if(name != null)
                sb.append(" with target \"" + name + "\"");
            break;

        case -1: 
            if(articleNeeded)
                sb.append("the ");
            sb.append("end of the document");
            break;

        default:
            throw new InternalError("Unknown parser state");
        }
        return sb.toString();
    }

    public abstract String describe(boolean flag);

    public String toString() {
        StringBuffer sb = new StringBuffer("[StreamingParser");
        if(systemId() != null)
            sb.append(" " + systemId());
        sb.append(": " + describe(false));
        sb.append("]");
        return sb.toString();
    }
}
