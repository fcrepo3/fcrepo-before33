// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XSDDoubleEncoder.java

package com.sun.xml.rpc.encoding.simpletype;

import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;

// Referenced classes of package com.sun.xml.rpc.encoding.simpletype:
//            SimpleTypeEncoder

public class XSDDoubleEncoder
    implements SimpleTypeEncoder {

    private static final SimpleTypeEncoder encoder = new XSDDoubleEncoder();

    private XSDDoubleEncoder() {
    }

    public static SimpleTypeEncoder getInstance() {
        return encoder;
    }

    public String objectToString(Object obj, XMLWriter writer) throws Exception {
        if(obj == null)
            return null;
        Double d = (Double)obj;
        double dVal = d.doubleValue();
        if(d.isInfinite())
            if(dVal == (-1.0D / 0.0D))
                return "-INF";
            else
                return "INF";
        if(d.isNaN())
            return "NaN";
        else
            return d.toString();
    }

    public Object stringToObject(String str, XMLReader reader) throws Exception {
        if(str == null)
            return null;
        if(str.equals("-INF"))
            return new Double((-1.0D / 0.0D));
        if(str.equals("INF"))
            return new Double((1.0D / 0.0D));
        if(str.equals("NaN"))
            return new Double((0.0D / 0.0D));
        else
            return new Double(str);
    }

    public void writeAdditionalNamespaceDeclarations(Object obj1, XMLWriter xmlwriter) throws Exception {
    }

}
