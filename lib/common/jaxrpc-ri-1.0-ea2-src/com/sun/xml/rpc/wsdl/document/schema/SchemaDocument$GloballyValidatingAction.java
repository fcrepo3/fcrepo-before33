// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SchemaDocument.java

package com.sun.xml.rpc.wsdl.document.schema;

import com.sun.xml.rpc.wsdl.framework.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.wsdl.document.schema:
//            SchemaDocument

class SchemaDocument$GloballyValidatingAction
    implements EntityAction, EntityReferenceAction {

    private ValidationException _exception;
    private AbstractDocument _document;
    private EntityReferenceValidator _validator;
    private final SchemaDocument this$0; /* synthetic field */

    public SchemaDocument$GloballyValidatingAction(SchemaDocument this$0, AbstractDocument document, EntityReferenceValidator validator) {
        this.this$0 = this$0;
        _document = document;
        _validator = validator;
    }

    public void perform(Entity entity) {
        try {
            entity.validateThis();
            entity.withAllEntityReferencesDo(this);
            entity.withAllSubEntitiesDo(this);
        }
        catch(ValidationException e) {
            if(_exception == null)
                _exception = e;
        }
    }

    public void perform(Kind kind, QName name) {
        com.sun.xml.rpc.wsdl.framework.GloballyKnown entity;
        try {
            entity = _document.find(kind, name);
        }
        catch(NoSuchEntityException e) {
            if(_exception == null && (_validator == null || !_validator.isValid(kind, name)))
                _exception = e;
        }
    }

    public ValidationException getException() {
        return _exception;
    }
}
