// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XMLWriterFactoryImpl.java

package com.sun.xml.rpc.streaming;

import java.io.OutputStream;

// Referenced classes of package com.sun.xml.rpc.streaming:
//            XMLWriterFactory, XMLWriterImpl, XMLWriter

public class XMLWriterFactoryImpl extends XMLWriterFactory {

    public XMLWriterFactoryImpl() {
    }

    public XMLWriter createXMLWriter(OutputStream stream) {
        return createXMLWriter(stream, "UTF-8");
    }

    public XMLWriter createXMLWriter(OutputStream stream, String encoding) {
        return createXMLWriter(stream, encoding, true);
    }

    public XMLWriter createXMLWriter(OutputStream stream, String encoding, boolean declare) {
        return new XMLWriterImpl(stream, encoding, declare);
    }
}
