// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   StreamingParserFactoryImpl.java

package com.sun.xml.rpc.sp;

import java.io.*;

// Referenced classes of package com.sun.xml.rpc.sp:
//            StreamingParserFactory, StreamingParserImpl, StreamingParser

public class StreamingParserFactoryImpl extends StreamingParserFactory {

    private boolean validating;
    private boolean coalescing;
    private boolean namespaceAware;

    public StreamingParserFactoryImpl() {
        validating = false;
        coalescing = false;
        namespaceAware = false;
    }

    public void setValidating(boolean validating) {
        if(validating) {
            throw new UnsupportedOperationException("Validating parser is not supported");
        } else {
            this.validating = validating;
            return;
        }
    }

    public boolean isValidating() {
        return validating;
    }

    public void setCoalescing(boolean coalescing) {
        this.coalescing = coalescing;
    }

    public boolean isCoalescing() {
        return coalescing;
    }

    public void setNamespaceAware(boolean namespaceAware) {
        this.namespaceAware = namespaceAware;
    }

    public boolean isNamespaceAware() {
        return namespaceAware;
    }

    public StreamingParser newParser(InputStream in) {
        return new StreamingParserImpl(this, in);
    }

    public StreamingParser newParser(File file) throws IOException {
        return new StreamingParserImpl(this, file);
    }
}
