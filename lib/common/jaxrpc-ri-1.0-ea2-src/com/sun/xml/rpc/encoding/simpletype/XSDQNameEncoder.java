// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XSDQNameEncoder.java

package com.sun.xml.rpc.encoding.simpletype;

import com.sun.xml.rpc.encoding.DeserializationException;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;
import com.sun.xml.rpc.util.xml.XmlUtil;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding.simpletype:
//            SimpleTypeEncoder

public class XSDQNameEncoder
    implements SimpleTypeEncoder {

    private static final SimpleTypeEncoder encoder = new XSDQNameEncoder();

    private XSDQNameEncoder() {
    }

    public static SimpleTypeEncoder getInstance() {
        return encoder;
    }

    public String objectToString(Object obj, XMLWriter writer) throws Exception {
        if(obj == null)
            return null;
        QName qn = (QName)obj;
        String str = "";
        String nsURI = qn.getNamespaceURI();
        if(nsURI != null && nsURI.length() > 0) {
            String prefix = writer.getPrefix(nsURI);
            str = str + prefix + ":";
        }
        str = str + qn.getLocalPart();
        return str;
    }

    public Object stringToObject(String str, XMLReader reader) throws Exception {
        if(str == null)
            return null;
        String uri = "";
        String prefix = XmlUtil.getPrefix(str);
        if(prefix != null) {
            uri = reader.getURI(prefix);
            if(uri == null)
                throw new DeserializationException("xsd.unknownPrefix", prefix);
        }
        String localPart = XmlUtil.getLocalPart(str);
        return new QName(uri, localPart);
    }

    public void writeAdditionalNamespaceDeclarations(Object obj, XMLWriter writer) throws Exception {
        QName value = (QName)obj;
        if(value != null) {
            String uri = value.getNamespaceURI();
            if(!uri.equals("") && writer.getPrefix(uri) == null)
                writer.writeNamespaceDeclaration(uri);
        }
    }

}
