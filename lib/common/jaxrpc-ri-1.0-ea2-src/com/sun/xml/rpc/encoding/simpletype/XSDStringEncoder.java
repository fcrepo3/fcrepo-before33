// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XSDStringEncoder.java

package com.sun.xml.rpc.encoding.simpletype;

import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;
import javax.activation.DataHandler;

// Referenced classes of package com.sun.xml.rpc.encoding.simpletype:
//            SimpleTypeEncoder, AttachmentEncoder

public class XSDStringEncoder
    implements SimpleTypeEncoder, AttachmentEncoder {

    private static final SimpleTypeEncoder encoder = new XSDStringEncoder();

    private XSDStringEncoder() {
    }

    public static SimpleTypeEncoder getInstance() {
        return encoder;
    }

    public String objectToString(Object obj, XMLWriter writer) throws Exception {
        return (String)obj;
    }

    public Object stringToObject(String str, XMLReader reader) throws Exception {
        return str;
    }

    public void writeAdditionalNamespaceDeclarations(Object obj1, XMLWriter xmlwriter) throws Exception {
    }

    public DataHandler objectToDataHandler(Object obj) throws Exception {
        DataHandler dataHandler = new DataHandler(obj, "text/plain");
        return dataHandler;
    }

    public Object dataHandlerToObject(DataHandler dataHandler) throws Exception {
        return dataHandler.getContent();
    }

}
