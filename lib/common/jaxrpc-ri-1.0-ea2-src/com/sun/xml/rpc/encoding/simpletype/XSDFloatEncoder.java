// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XSDFloatEncoder.java

package com.sun.xml.rpc.encoding.simpletype;

import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;

// Referenced classes of package com.sun.xml.rpc.encoding.simpletype:
//            SimpleTypeEncoder

public class XSDFloatEncoder
    implements SimpleTypeEncoder {

    private static final SimpleTypeEncoder encoder = new XSDFloatEncoder();

    private XSDFloatEncoder() {
    }

    public static SimpleTypeEncoder getInstance() {
        return encoder;
    }

    public String objectToString(Object obj, XMLWriter writer) throws Exception {
        if(obj == null)
            return null;
        Float f = (Float)obj;
        float fVal = f.floatValue();
        if(f.isInfinite())
            if(fVal == (-1.0F / 0.0F))
                return "-INF";
            else
                return "INF";
        if(f.isNaN())
            return "NaN";
        else
            return f.toString();
    }

    public Object stringToObject(String str, XMLReader reader) throws Exception {
        if(str == null)
            return null;
        if(str.equals("-INF"))
            return new Float((-1.0F / 0.0F));
        if(str.equals("INF"))
            return new Float((1.0F / 0.0F));
        if(str.equals("NaN"))
            return new Float((0.0F / 0.0F));
        else
            return new Float(str);
    }

    public void writeAdditionalNamespaceDeclarations(Object obj1, XMLWriter xmlwriter) throws Exception {
    }

}
