// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   AbstractDocument.java

package com.sun.xml.rpc.wsdl.framework;

import java.util.Set;

// Referenced classes of package com.sun.xml.rpc.wsdl.framework:
//            EntityAction, Entity, AbstractDocument

class AbstractDocument$3
    implements EntityAction {

    private final Set val$result; /* synthetic field */
    private final AbstractDocument this$0; /* synthetic field */

    AbstractDocument$3(AbstractDocument this$0, Set val$result) {
        this.this$0 = this$0;
        this.val$result = val$result;
    }

    public void perform(Entity entity) {
        entity.withAllQNamesDo(new AbstractDocument$4(this));
        entity.withAllSubEntitiesDo(this);
    }

    static Set access$100(AbstractDocument$3 x0) {
        return x0.val$result;
    }
}
