// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XMLReaderBase.java

package com.sun.xml.rpc.streaming;

import java.util.Iterator;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.streaming:
//            XMLReaderException, XMLReader, Attributes

public abstract class XMLReaderBase
    implements XMLReader {

    public XMLReaderBase() {
    }

    public int nextContent() {
label0:
        do {
            int state = next();
            switch(state) {
            case 4: // '\004'
            default:
                break;

            case 1: // '\001'
            case 2: // '\002'
            case 5: // '\005'
                return state;

            case 3: // '\003'
                if(getValue().trim().length() != 0)
                    break label0;
                break;
            }
        } while(true);
        return 3;
    }

    public int nextElementContent() {
        int state = nextContent();
        if(state == 3)
            throw new XMLReaderException("xmlreader.unexpectedCharacterContent", getValue());
        else
            return state;
    }

    public void skipElement() {
        skipElement(getElementId());
    }

    public abstract void close();

    public abstract void skipElement(int i);

    public abstract XMLReader recordElement();

    public abstract Iterator getPrefixes();

    public abstract String getURI(String s);

    public abstract int getLineNumber();

    public abstract int getElementId();

    public abstract String getValue();

    public abstract Attributes getAttributes();

    public abstract String getLocalName();

    public abstract String getURI();

    public abstract QName getName();

    public abstract int getState();

    public abstract int next();
}
