// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SchemaDocument.java

package com.sun.xml.rpc.wsdl.document.schema;

import com.sun.xml.rpc.wsdl.framework.*;
import java.util.Set;

// Referenced classes of package com.sun.xml.rpc.wsdl.document.schema:
//            Schema

public class SchemaDocument extends AbstractDocument {

    private Schema _schema;

    public SchemaDocument() {
    }

    public Schema getSchema() {
        return _schema;
    }

    public void setSchema(Schema s) {
        _schema = s;
    }

    public Set collectAllNamespaces() {
        Set result = super.collectAllNamespaces();
        if(_schema.getTargetNamespaceURI() != null)
            result.add(_schema.getTargetNamespaceURI());
        return result;
    }

    public void validate(EntityReferenceValidator validator) {
        SchemaDocument$GloballyValidatingAction action = new SchemaDocument$GloballyValidatingAction(this, this, validator);
        withAllSubEntitiesDo(action);
        if(action.getException() != null)
            throw action.getException();
        else
            return;
    }

    protected Entity getRoot() {
        return _schema;
    }
}
