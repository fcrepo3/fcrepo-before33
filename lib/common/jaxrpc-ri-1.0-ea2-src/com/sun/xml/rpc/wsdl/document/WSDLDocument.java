// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   WSDLDocument.java

package com.sun.xml.rpc.wsdl.document;

import com.sun.xml.rpc.wsdl.framework.*;
import java.util.Set;

// Referenced classes of package com.sun.xml.rpc.wsdl.document:
//            Definitions, WSDLDocumentVisitor

public class WSDLDocument extends AbstractDocument {

    private Definitions _definitions;

    public WSDLDocument() {
    }

    public Definitions getDefinitions() {
        return _definitions;
    }

    public void setDefinitions(Definitions d) {
        _definitions = d;
    }

    public Set collectAllNamespaces() {
        Set result = super.collectAllNamespaces();
        if(_definitions.getTargetNamespaceURI() != null)
            result.add(_definitions.getTargetNamespaceURI());
        return result;
    }

    public void accept(WSDLDocumentVisitor visitor) throws Exception {
        _definitions.accept(visitor);
    }

    public void validate(EntityReferenceValidator validator) {
        WSDLDocument$GloballyValidatingAction action = new WSDLDocument$GloballyValidatingAction(this, this, validator);
        withAllSubEntitiesDo(action);
        if(action.getException() != null)
            throw action.getException();
        else
            return;
    }

    protected Entity getRoot() {
        return _definitions;
    }
}
