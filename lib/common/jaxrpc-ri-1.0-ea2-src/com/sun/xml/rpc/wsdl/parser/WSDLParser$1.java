// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   WSDLParser.java

package com.sun.xml.rpc.wsdl.parser;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

// Referenced classes of package com.sun.xml.rpc.wsdl.parser:
//            WSDLParser

class WSDLParser$1
    implements ErrorHandler {

    private final WSDLParser this$0; /* synthetic field */

    WSDLParser$1(WSDLParser this$0) {
        this.this$0 = this$0;
    }

    public void error(SAXParseException e) throws SAXParseException {
        throw e;
    }

    public void fatalError(SAXParseException e) throws SAXParseException {
        throw e;
    }

    public void warning(SAXParseException saxparseexception) throws SAXParseException {
    }
}
