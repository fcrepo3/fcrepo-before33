// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   StreamingParserFactory.java

package com.sun.xml.rpc.sp;

import java.io.*;

// Referenced classes of package com.sun.xml.rpc.sp:
//            StreamingParserFactoryImpl, StreamingParser

public abstract class StreamingParserFactory {

    protected StreamingParserFactory() {
    }

    public static StreamingParserFactory newInstance() {
        return new StreamingParserFactoryImpl();
    }

    public abstract void setValidating(boolean flag);

    public abstract boolean isValidating();

    public abstract void setCoalescing(boolean flag);

    public abstract boolean isCoalescing();

    public abstract void setNamespaceAware(boolean flag);

    public abstract boolean isNamespaceAware();

    public abstract StreamingParser newParser(InputStream inputstream);

    public abstract StreamingParser newParser(File file) throws IOException;
}
