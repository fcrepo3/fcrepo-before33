// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XSDLongEncoder.java

package com.sun.xml.rpc.encoding.simpletype;

import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;

// Referenced classes of package com.sun.xml.rpc.encoding.simpletype:
//            SimpleTypeEncoder

public class XSDLongEncoder
    implements SimpleTypeEncoder {

    private static final SimpleTypeEncoder encoder = new XSDLongEncoder();

    private XSDLongEncoder() {
    }

    public static SimpleTypeEncoder getInstance() {
        return encoder;
    }

    public String objectToString(Object obj, XMLWriter writer) throws Exception {
        if(obj == null)
            return null;
        else
            return ((Long)obj).toString();
    }

    public Object stringToObject(String str, XMLReader reader) throws Exception {
        if(str == null)
            return null;
        else
            return new Long(str);
    }

    public void writeAdditionalNamespaceDeclarations(Object obj1, XMLWriter xmlwriter) throws Exception {
    }

}
