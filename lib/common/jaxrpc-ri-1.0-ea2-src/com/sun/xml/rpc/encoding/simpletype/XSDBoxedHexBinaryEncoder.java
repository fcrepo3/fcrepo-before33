// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XSDBoxedHexBinaryEncoder.java

package com.sun.xml.rpc.encoding.simpletype;

import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;

// Referenced classes of package com.sun.xml.rpc.encoding.simpletype:
//            SimpleTypeEncoder

public class XSDBoxedHexBinaryEncoder
    implements SimpleTypeEncoder {

    private static final SimpleTypeEncoder encoder = new XSDBoxedHexBinaryEncoder();
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

    private XSDBoxedHexBinaryEncoder() {
    }

    public static SimpleTypeEncoder getInstance() {
        return encoder;
    }

    public String objectToString(Object obj, XMLWriter writer) throws Exception {
        if(obj == null)
            return null;
        Byte value[] = (Byte[])obj;
        if(value.length == 0)
            return "";
        StringBuffer encodedValue = new StringBuffer(value.length * 2);
        for(int i = 0; i < value.length; i++) {
            encodedValue.append(encodeHex[value[i].byteValue() >> 4 & 0xf]);
            encodedValue.append(encodeHex[value[i].byteValue() & 0xf]);
        }

        return encodedValue.toString();
    }

    public Object stringToObject(String str, XMLReader reader) throws Exception {
        if(str == null)
            return null;
        String encodedValue = str;
        int valueLength = encodedValue.length() / 2;
        Byte value[] = new Byte[valueLength];
        int encodedIdx = 0;
        for(int i = 0; i < valueLength; i++) {
            int nibble1 = decodeHex[encodedValue.charAt(encodedIdx++) - 48];
            int nibble2 = decodeHex[encodedValue.charAt(encodedIdx++) - 48];
            value[i] = new Byte((byte)(nibble1 << 4 | nibble2));
        }

        return value;
    }

    public void writeAdditionalNamespaceDeclarations(Object obj1, XMLWriter xmlwriter) throws Exception {
    }

}
