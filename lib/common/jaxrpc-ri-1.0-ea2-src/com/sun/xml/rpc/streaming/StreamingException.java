// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   StreamingException.java

package com.sun.xml.rpc.streaming;

import com.sun.xml.rpc.sp.ParseException;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;
import java.io.IOException;

public class StreamingException extends JAXRPCExceptionBase {

    public StreamingException(IOException e) {
        this("streaming.ioException", e.toString());
    }

    public StreamingException(ParseException e) {
        this("streaming.parseException", e.toString());
    }

    public StreamingException(String key) {
        super(key);
    }

    public StreamingException(String key, String arg) {
        super(key, arg);
    }

    public StreamingException(String key, Localizable localizable) {
        super(key, localizable);
    }

    public StreamingException(String key, Object args[]) {
        super(key, args);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.streaming";
    }
}
