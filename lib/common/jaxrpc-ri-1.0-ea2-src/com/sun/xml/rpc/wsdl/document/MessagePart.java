// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   MessagePart.java

package com.sun.xml.rpc.wsdl.document;

import com.sun.xml.rpc.wsdl.framework.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.wsdl.document:
//            WSDLConstants, WSDLDocumentVisitor

public class MessagePart extends Entity {

    private String _name;
    private QName _descriptor;
    private Kind _descriptorKind;

    public MessagePart() {
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public QName getDescriptor() {
        return _descriptor;
    }

    public void setDescriptor(QName n) {
        _descriptor = n;
    }

    public Kind getDescriptorKind() {
        return _descriptorKind;
    }

    public void setDescriptorKind(Kind k) {
        _descriptorKind = k;
    }

    public QName getElementName() {
        return WSDLConstants.QNAME_PART;
    }

    public void withAllQNamesDo(QNameAction action) {
        if(_descriptor != null)
            action.perform(_descriptor);
    }

    public void withAllEntityReferencesDo(EntityReferenceAction action) {
        super.withAllEntityReferencesDo(action);
        if(_descriptor != null && _descriptorKind != null)
            action.perform(_descriptorKind, _descriptor);
    }

    public void accept(WSDLDocumentVisitor visitor) throws Exception {
        visitor.visit(this);
    }

    public void validateThis() {
        if(_descriptorKind == null || _descriptor == null)
            failValidation("validation.missingRequiredProperty", "descriptor");
    }
}
