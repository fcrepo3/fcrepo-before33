// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XMLWriterUtil.java

package com.sun.xml.rpc.streaming;

import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.streaming:
//            XMLWriter

public class XMLWriterUtil {

    private XMLWriterUtil() {
    }

    public static String encodeQName(XMLWriter writer, QName qname) {
        String namespaceURI = qname.getNamespaceURI();
        String localPart = qname.getLocalPart();
        if(namespaceURI == null || namespaceURI.equals(""))
            return localPart;
        String prefix = writer.getPrefix(namespaceURI);
        if(prefix == null) {
            writer.writeNamespaceDeclaration(namespaceURI);
            prefix = writer.getPrefix(namespaceURI);
        }
        return prefix + ":" + localPart;
    }
}
