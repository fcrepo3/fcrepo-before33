// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ExtensionHandler.java

package com.sun.xml.rpc.wsdl.parser;

import com.sun.xml.rpc.wsdl.framework.*;
import java.io.IOException;
import java.util.Map;
import org.w3c.dom.Element;

public abstract class ExtensionHandler {

    protected Map _extensionHandlers;

    protected ExtensionHandler() {
    }

    public abstract String getNamespaceURI();

    public void setExtensionHandlers(Map m) {
        _extensionHandlers = m;
    }

    public boolean doHandleExtension(ParserContext context, Extensible parent, Element e) {
        return false;
    }

    public void doHandleExtension(WriterContext writercontext, Extension extension1) throws IOException {
    }
}
