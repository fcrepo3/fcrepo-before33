// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   PrettyPrintingXMLWriterFactoryImpl.java

package com.sun.xml.rpc.processor.util;

import com.sun.xml.rpc.streaming.XMLWriter;
import com.sun.xml.rpc.streaming.XMLWriterFactory;
import java.io.OutputStream;

// Referenced classes of package com.sun.xml.rpc.processor.util:
//            PrettyPrintingXMLWriterImpl

public class PrettyPrintingXMLWriterFactoryImpl extends XMLWriterFactory {

    public PrettyPrintingXMLWriterFactoryImpl() {
    }

    public XMLWriter createXMLWriter(OutputStream stream) {
        return createXMLWriter(stream, "UTF-8");
    }

    public XMLWriter createXMLWriter(OutputStream stream, String encoding) {
        return createXMLWriter(stream, encoding, true);
    }

    public XMLWriter createXMLWriter(OutputStream stream, String encoding, boolean declare) {
        return new PrettyPrintingXMLWriterImpl(stream, encoding, declare);
    }
}
