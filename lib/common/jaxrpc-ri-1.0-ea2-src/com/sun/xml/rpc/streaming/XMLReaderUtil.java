// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XMLReaderUtil.java

package com.sun.xml.rpc.streaming;

import com.sun.xml.rpc.util.xml.XmlUtil;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.streaming:
//            XMLReaderException, XMLReader, Attributes

public class XMLReaderUtil {

    private XMLReaderUtil() {
    }

    public static QName getQNameValue(XMLReader reader, QName attributeName) {
        String attribute = reader.getAttributes().getValue(attributeName);
        return attribute != null ? decodeQName(reader, attribute) : null;
    }

    public static QName decodeQName(XMLReader reader, String rawName) {
        String prefix = XmlUtil.getPrefix(rawName);
        String local = XmlUtil.getLocalPart(rawName);
        String uri = prefix != null ? reader.getURI(prefix) : null;
        return new QName(uri, local);
    }

    public static void verifyReaderState(XMLReader reader, int expectedState) {
        if(reader.getState() != expectedState)
            throw new XMLReaderException("xmlreader.unexpectedState", new Object[] {
                getStateName(expectedState), getStateName(reader)
            });
        else
            return;
    }

    public static String getStateName(XMLReader reader) {
        return getStateName(reader.getState());
    }

    public static String getStateName(int state) {
        switch(state) {
        case 0: // '\0'
            return "BOF";

        case 1: // '\001'
            return "START";

        case 2: // '\002'
            return "END";

        case 3: // '\003'
            return "CHARS";

        case 4: // '\004'
            return "PI";

        case 5: // '\005'
            return "EOF";
        }
        return "UNKNOWN";
    }
}
