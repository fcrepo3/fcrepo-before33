// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   DuplicateEntityException.java

package com.sun.xml.rpc.wsdl.framework;

import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.wsdl.framework:
//            ValidationException, Elemental, GloballyKnown, Identifiable, 
//            Entity

public class DuplicateEntityException extends ValidationException {

    public DuplicateEntityException(GloballyKnown entity) {
        super("entity.duplicateWithType", new Object[] {
            entity.getElementName().getLocalPart(), entity.getName()
        });
    }

    public DuplicateEntityException(Identifiable entity) {
        super("entity.duplicateWithType", new Object[] {
            entity.getElementName().getLocalPart(), entity.getID()
        });
    }

    public DuplicateEntityException(Entity entity, String name) {
        super("entity.duplicateWithType", new Object[] {
            entity.getElementName().getLocalPart(), name
        });
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.wsdl";
    }
}
