// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XSDIntegerEncoder.java

package com.sun.xml.rpc.encoding.simpletype;

import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;
import java.math.BigInteger;

// Referenced classes of package com.sun.xml.rpc.encoding.simpletype:
//            SimpleTypeEncoder

public class XSDIntegerEncoder
    implements SimpleTypeEncoder {

    private static final SimpleTypeEncoder encoder = new XSDIntegerEncoder();

    private XSDIntegerEncoder() {
    }

    public static SimpleTypeEncoder getInstance() {
        return encoder;
    }

    public String objectToString(Object obj, XMLWriter writer) throws Exception {
        if(obj == null)
            return null;
        else
            return ((BigInteger)obj).toString();
    }

    public Object stringToObject(String str, XMLReader reader) throws Exception {
        if(str == null)
            return null;
        else
            return new BigInteger(str);
    }

    public void writeAdditionalNamespaceDeclarations(Object obj1, XMLWriter xmlwriter) throws Exception {
    }

}
