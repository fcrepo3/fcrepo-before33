// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XSDBoxedBase64BinaryEncoder.java

package com.sun.xml.rpc.encoding.simpletype;

import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;

// Referenced classes of package com.sun.xml.rpc.encoding.simpletype:
//            SimpleTypeEncoder

public class XSDBoxedBase64BinaryEncoder
    implements SimpleTypeEncoder {

    private static final SimpleTypeEncoder encoder = new XSDBoxedBase64BinaryEncoder();
    private static final char encodeBase64[] = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
        'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 
        'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 
        'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
        'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 
        'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', 
        '8', '9', '+', '/'
    };
    private static final int decodeBase64[] = {
        62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 
        57, 58, 59, 60, 61, -1, -1, -1, -1, -1, 
        -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 
        8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 
        18, 19, 20, 21, 22, 23, 24, 25, -1, -1, 
        -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 
        32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 
        42, 43, 44, 45, 46, 47, 48, 49, 50, 51
    };

    private XSDBoxedBase64BinaryEncoder() {
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
        int blockCount = value.length / 3;
        int partialBlockLength = value.length % 3;
        if(partialBlockLength != 0)
            blockCount++;
        int encodedLength = blockCount * 4;
        StringBuffer encodedValue = new StringBuffer(encodedLength);
        int idx = 0;
        for(int i = 0; i < blockCount; i++) {
            int b1 = value[idx++].byteValue();
            int b2 = idx >= value.length ? 0 : ((int) (value[idx++].byteValue()));
            int b3 = idx >= value.length ? 0 : ((int) (value[idx++].byteValue()));
            if(b1 < 0)
                b1 += 256;
            if(b2 < 0)
                b2 += 256;
            if(b3 < 0)
                b3 += 256;
            char encodedChar = encodeBase64[b1 >> 2];
            encodedValue.append(encodedChar);
            encodedChar = encodeBase64[(b1 & 3) << 4 | b2 >> 4];
            encodedValue.append(encodedChar);
            encodedChar = encodeBase64[(b2 & 0xf) << 2 | b3 >> 6];
            encodedValue.append(encodedChar);
            encodedChar = encodeBase64[b3 & 0x3f];
            encodedValue.append(encodedChar);
        }

        switch(partialBlockLength) {
        case 1: // '\001'
            encodedValue.setCharAt(encodedLength - 1, '=');
            encodedValue.setCharAt(encodedLength - 2, '=');
            break;

        case 2: // '\002'
            encodedValue.setCharAt(encodedLength - 1, '=');
            break;
        }
        return encodedValue.toString();
    }

    public Object stringToObject(String str, XMLReader reader) throws Exception {
        if(str == null)
            return null;
        String uri = "";
        String encodedValue = str;
        int encodedLength = encodedValue.length();
        if(encodedLength == 0)
            return new Byte[0];
        int blockCount = encodedLength / 4;
        int partialBlockLength = 3;
        if(encodedValue.charAt(encodedLength - 1) == '=') {
            partialBlockLength--;
            if(encodedValue.charAt(encodedLength - 2) == '=')
                partialBlockLength--;
        }
        int valueLength = (blockCount - 1) * 3 + partialBlockLength;
        Byte value[] = new Byte[valueLength];
        int idx = 0;
        int encodedIdx = 0;
        for(int i = 0; i < blockCount; i++) {
            int x1 = decodeBase64[encodedValue.charAt(encodedIdx++) - 43];
            int x2 = decodeBase64[encodedValue.charAt(encodedIdx++) - 43];
            int x3 = decodeBase64[encodedValue.charAt(encodedIdx++) - 43];
            int x4 = decodeBase64[encodedValue.charAt(encodedIdx++) - 43];
            value[idx++] = new Byte((byte)(x1 << 2 | x2 >> 4));
            if(idx < valueLength)
                value[idx++] = new Byte((byte)((x2 & 0xf) << 4 | x3 >> 2));
            if(idx < valueLength)
                value[idx++] = new Byte((byte)((x3 & 3) << 6 | x4));
        }

        return value;
    }

    public void writeAdditionalNamespaceDeclarations(Object obj1, XMLWriter xmlwriter) throws Exception {
    }

}
