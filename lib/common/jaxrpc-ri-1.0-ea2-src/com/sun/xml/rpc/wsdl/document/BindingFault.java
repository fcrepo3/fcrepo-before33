// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   BindingFault.java

package com.sun.xml.rpc.wsdl.document;

import com.sun.xml.rpc.wsdl.framework.*;
import java.util.Iterator;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.wsdl.document:
//            WSDLConstants, WSDLDocumentVisitor, Documentation

public class BindingFault extends Entity
    implements Extensible {

    private ExtensibilityHelper _helper;
    private Documentation _documentation;
    private String _name;

    public BindingFault() {
        _helper = new ExtensibilityHelper();
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public QName getElementName() {
        return WSDLConstants.QNAME_FAULT;
    }

    public Documentation getDocumentation() {
        return _documentation;
    }

    public void setDocumentation(Documentation d) {
        _documentation = d;
    }

    public void addExtension(Extension e) {
        _helper.addExtension(e);
    }

    public Iterator extensions() {
        return _helper.extensions();
    }

    public void withAllSubEntitiesDo(EntityAction action) {
        _helper.withAllSubEntitiesDo(action);
    }

    public void accept(WSDLDocumentVisitor visitor) throws Exception {
        visitor.preVisit(this);
        _helper.accept(visitor);
        visitor.postVisit(this);
    }

    public void validateThis() {
        if(_name == null)
            failValidation("validation.missingRequiredAttribute", "name");
    }
}
