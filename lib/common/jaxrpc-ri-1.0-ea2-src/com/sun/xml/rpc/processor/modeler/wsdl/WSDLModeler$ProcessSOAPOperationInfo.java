// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   WSDLModeler.java

package com.sun.xml.rpc.processor.modeler.wsdl;

import com.sun.xml.rpc.wsdl.document.BindingOperation;
import com.sun.xml.rpc.wsdl.document.Operation;
import com.sun.xml.rpc.wsdl.document.Port;
import com.sun.xml.rpc.wsdl.document.WSDLDocument;
import com.sun.xml.rpc.wsdl.document.soap.SOAPBinding;
import java.util.Map;

// Referenced classes of package com.sun.xml.rpc.processor.modeler.wsdl:
//            WSDLModeler

public class WSDLModeler$ProcessSOAPOperationInfo {

    public Port port;
    public Operation portTypeOperation;
    public BindingOperation bindingOperation;
    public SOAPBinding soapBinding;
    public WSDLDocument document;
    public boolean hasOverloadedOperations;
    public Map headers;
    public com.sun.xml.rpc.processor.model.Operation operation;
    public String uniqueOperationName;
    private final WSDLModeler this$0; /* synthetic field */

    public WSDLModeler$ProcessSOAPOperationInfo(WSDLModeler this$0, Port port, Operation portTypeOperation, BindingOperation bindingOperation, SOAPBinding soapBinding, WSDLDocument document, boolean hasOverloadedOperations, 
            Map headers) {
        this.this$0 = this$0;
        this.port = port;
        this.portTypeOperation = portTypeOperation;
        this.bindingOperation = bindingOperation;
        this.soapBinding = soapBinding;
        this.document = document;
        this.hasOverloadedOperations = hasOverloadedOperations;
        this.headers = headers;
    }
}
