// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XSDHexBinaryEncoder.java

package com.sun.xml.rpc.encoding.simpletype;

import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;

// Referenced classes of package com.sun.xml.rpc.encoding.simpletype:
//            SimpleTypeEncoder

public class XSDHexBinaryEncoder
    implements SimpleTypeEncoder {

    private static final SimpleTypeEncoder encoder = new XSDHexBinaryEncoder();
    private static final char encodeHex[] = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
        'a', 'b', 'c', 'd', 'e', 'f'
    };
    private static final int decodeHex[] = {
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 
        -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 
        13, 14, 15, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 
        11, 12, 13, 14, 15
    };

    private XSDHexBinaryEncoder() {
    }

    public static SimpleTypeEncoder getInstance() {
        return encoder;
    }

    public String objectToString(Object obj, XMLWriter writer) throws Exception {
        if(obj == null)
            return null;
        byte value[] = (byte[])obj;
        if(value.length == 0)
            return "";
        StringBuffer encodedValue = new StringBuffer(value.length * 2);
        for(int i = 0; i < value.length; i++) {
            encodedValue.append(encodeHex[value[i] >> 4 & 0xf]);
            encodedValue.append(encodeHex[value[i] & 0xf]);
        }

        return encodedValue.toString();
    }

    public Object stringToObject(String str, XMLReader reader) throws Exception {
        if(str == null)
            return null;
        String encodedValue = str;
        int valueLength = encodedValue.length() / 2;
        byte value[] = new byte[valueLength];
        int encodedIdx = 0;
        for(int i = 0; i < valueLength; i++) {
            int nibble1 = decodeHex[encodedValue.charAt(encodedIdx++) - 48];
            int nibble2 = decodeHex[encodedValue.charAt(encodedIdx++) - 48];
            value[i] = (byte)(nibble1 << 4 | nibble2);
        }

        return value;
    }

    public void writeAdditionalNamespaceDeclarations(Object obj1, XMLWriter xmlwriter) throws Exception {
    }

}
