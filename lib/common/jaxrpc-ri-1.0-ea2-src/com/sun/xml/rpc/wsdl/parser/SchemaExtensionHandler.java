// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SchemaExtensionHandler.java

package com.sun.xml.rpc.wsdl.parser;

import com.sun.xml.rpc.util.xml.XmlUtil;
import com.sun.xml.rpc.wsdl.document.schema.Schema;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import com.sun.xml.rpc.wsdl.framework.*;
import java.io.IOException;
import org.w3c.dom.Element;

// Referenced classes of package com.sun.xml.rpc.wsdl.parser:
//            ExtensionHandler, SchemaParser, SchemaWriter

public class SchemaExtensionHandler extends ExtensionHandler {

    public SchemaExtensionHandler() {
    }

    public String getNamespaceURI() {
        return "http://www.w3.org/2001/XMLSchema";
    }

    public boolean doHandleExtension(ParserContext context, Extensible parent, Element e) {
        if(XmlUtil.matchesTagNS(e, SchemaConstants.QNAME_SCHEMA)) {
            SchemaParser parser = new SchemaParser();
            parent.addExtension(parser.parseSchema(context, e, null));
            return true;
        } else {
            return false;
        }
    }

    public void doHandleExtension(WriterContext context, Extension extension) throws IOException {
        if(extension instanceof Schema) {
            SchemaWriter writer = new SchemaWriter();
            writer.writeSchema(context, (Schema)extension);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
