// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XMLReaderFactoryImpl.java

package com.sun.xml.rpc.streaming;

import java.io.InputStream;
import org.xml.sax.InputSource;

// Referenced classes of package com.sun.xml.rpc.streaming:
//            XMLReaderFactory, XMLReaderImpl, XMLReader

public class XMLReaderFactoryImpl extends XMLReaderFactory {

    public XMLReaderFactoryImpl() {
    }

    public XMLReader createXMLReader(InputStream in) {
        return createXMLReader(new InputSource(in));
    }

    public XMLReader createXMLReader(InputSource source) {
        return new XMLReaderImpl(source);
    }
}
