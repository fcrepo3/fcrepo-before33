// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Output.java

package com.sun.xml.rpc.wsdl.document;

import com.sun.xml.rpc.wsdl.framework.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.wsdl.document:
//            Message, Kinds, WSDLConstants, WSDLDocumentVisitor, 
//            Documentation

public class Output extends Entity {

    private Documentation _documentation;
    private String _name;
    private QName _message;

    public Output() {
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public QName getMessage() {
        return _message;
    }

    public void setMessage(QName n) {
        _message = n;
    }

    public Message resolveMessage(AbstractDocument document) {
        return (Message)document.find(Kinds.MESSAGE, _message);
    }

    public QName getElementName() {
        return WSDLConstants.QNAME_OUTPUT;
    }

    public Documentation getDocumentation() {
        return _documentation;
    }

    public void setDocumentation(Documentation d) {
        _documentation = d;
    }

    public void withAllQNamesDo(QNameAction action) {
        if(_message != null)
            action.perform(_message);
    }

    public void withAllEntityReferencesDo(EntityReferenceAction action) {
        super.withAllEntityReferencesDo(action);
        if(_message != null)
            action.perform(Kinds.MESSAGE, _message);
    }

    public void accept(WSDLDocumentVisitor visitor) throws Exception {
        visitor.preVisit(this);
        visitor.postVisit(this);
    }

    public void validateThis() {
        if(_message == null)
            failValidation("validation.missingRequiredAttribute", "message");
    }
}
