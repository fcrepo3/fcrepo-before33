// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ExtensionHandlerBase.java

package com.sun.xml.rpc.wsdl.parser;

import com.sun.xml.rpc.wsdl.document.WSDLConstants;
import com.sun.xml.rpc.wsdl.document.mime.MIMEConstants;
import com.sun.xml.rpc.wsdl.framework.*;
import javax.xml.rpc.namespace.QName;
import org.w3c.dom.Element;

// Referenced classes of package com.sun.xml.rpc.wsdl.parser:
//            ExtensionHandler

public abstract class ExtensionHandlerBase extends ExtensionHandler {

    protected ExtensionHandlerBase() {
    }

    public boolean doHandleExtension(ParserContext context, Extensible parent, Element e) {
        if(parent.getElementName().equals(WSDLConstants.QNAME_DEFINITIONS))
            return handleDefinitionsExtension(context, parent, e);
        if(parent.getElementName().equals(WSDLConstants.QNAME_TYPES))
            return handleTypesExtension(context, parent, e);
        if(parent.getElementName().equals(WSDLConstants.QNAME_BINDING))
            return handleBindingExtension(context, parent, e);
        if(parent.getElementName().equals(WSDLConstants.QNAME_OPERATION))
            return handleOperationExtension(context, parent, e);
        if(parent.getElementName().equals(WSDLConstants.QNAME_INPUT))
            return handleInputExtension(context, parent, e);
        if(parent.getElementName().equals(WSDLConstants.QNAME_OUTPUT))
            return handleOutputExtension(context, parent, e);
        if(parent.getElementName().equals(WSDLConstants.QNAME_FAULT))
            return handleFaultExtension(context, parent, e);
        if(parent.getElementName().equals(WSDLConstants.QNAME_SERVICE))
            return handleServiceExtension(context, parent, e);
        if(parent.getElementName().equals(WSDLConstants.QNAME_PORT))
            return handlePortExtension(context, parent, e);
        if(parent.getElementName().equals(MIMEConstants.QNAME_PART))
            return handleMIMEPartExtension(context, parent, e);
        else
            return false;
    }

    protected abstract boolean handleDefinitionsExtension(ParserContext parsercontext, Extensible extensible, Element element);

    protected abstract boolean handleTypesExtension(ParserContext parsercontext, Extensible extensible, Element element);

    protected abstract boolean handleBindingExtension(ParserContext parsercontext, Extensible extensible, Element element);

    protected abstract boolean handleOperationExtension(ParserContext parsercontext, Extensible extensible, Element element);

    protected abstract boolean handleInputExtension(ParserContext parsercontext, Extensible extensible, Element element);

    protected abstract boolean handleOutputExtension(ParserContext parsercontext, Extensible extensible, Element element);

    protected abstract boolean handleFaultExtension(ParserContext parsercontext, Extensible extensible, Element element);

    protected abstract boolean handleServiceExtension(ParserContext parsercontext, Extensible extensible, Element element);

    protected abstract boolean handlePortExtension(ParserContext parsercontext, Extensible extensible, Element element);

    protected abstract boolean handleMIMEPartExtension(ParserContext parsercontext, Extensible extensible, Element element);
}
