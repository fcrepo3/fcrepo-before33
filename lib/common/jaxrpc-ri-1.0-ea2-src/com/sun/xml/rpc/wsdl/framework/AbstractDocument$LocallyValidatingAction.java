// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   AbstractDocument.java

package com.sun.xml.rpc.wsdl.framework;


// Referenced classes of package com.sun.xml.rpc.wsdl.framework:
//            ValidationException, EntityAction, Entity, AbstractDocument

class AbstractDocument$LocallyValidatingAction
    implements EntityAction {

    private ValidationException _exception;
    private final AbstractDocument this$0; /* synthetic field */

    public AbstractDocument$LocallyValidatingAction(AbstractDocument this$0) {
        this.this$0 = this$0;
    }

    public void perform(Entity entity) {
        try {
            entity.validateThis();
            entity.withAllSubEntitiesDo(this);
        }
        catch(ValidationException e) {
            if(_exception == null)
                _exception = e;
        }
    }

    public ValidationException getException() {
        return _exception;
    }
}
