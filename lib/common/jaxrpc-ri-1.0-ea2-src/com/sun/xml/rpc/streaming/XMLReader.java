// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XMLReader.java

package com.sun.xml.rpc.streaming;

import java.util.Iterator;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.streaming:
//            Attributes

public interface XMLReader {

    public static final int BOF = 0;
    public static final int START = 1;
    public static final int END = 2;
    public static final int CHARS = 3;
    public static final int PI = 4;
    public static final int EOF = 5;

    public abstract int next();

    public abstract int nextContent();

    public abstract int nextElementContent();

    public abstract int getState();

    public abstract QName getName();

    public abstract String getURI();

    public abstract String getLocalName();

    public abstract Attributes getAttributes();

    public abstract String getValue();

    public abstract int getElementId();

    public abstract int getLineNumber();

    public abstract String getURI(String s);

    public abstract Iterator getPrefixes();

    public abstract XMLReader recordElement();

    public abstract void skipElement();

    public abstract void skipElement(int i);

    public abstract void close();
}
