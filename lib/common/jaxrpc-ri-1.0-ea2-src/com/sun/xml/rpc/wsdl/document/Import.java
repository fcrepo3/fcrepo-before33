// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Import.java

package com.sun.xml.rpc.wsdl.document;

import com.sun.xml.rpc.wsdl.framework.Entity;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.wsdl.document:
//            WSDLConstants, WSDLDocumentVisitor, Documentation

public class Import extends Entity {

    private Documentation _documentation;
    private String _location;
    private String _namespace;

    public Import() {
    }

    public String getNamespace() {
        return _namespace;
    }

    public void setNamespace(String s) {
        _namespace = s;
    }

    public String getLocation() {
        return _location;
    }

    public void setLocation(String s) {
        _location = s;
    }

    public QName getElementName() {
        return WSDLConstants.QNAME_IMPORT;
    }

    public Documentation getDocumentation() {
        return _documentation;
    }

    public void setDocumentation(Documentation d) {
        _documentation = d;
    }

    public void accept(WSDLDocumentVisitor visitor) throws Exception {
        visitor.visit(this);
    }

    public void validateThis() {
        if(_location == null)
            failValidation("validation.missingRequiredAttribute", "location");
        if(_namespace == null)
            failValidation("validation.missingRequiredAttribute", "namespace");
    }
}
