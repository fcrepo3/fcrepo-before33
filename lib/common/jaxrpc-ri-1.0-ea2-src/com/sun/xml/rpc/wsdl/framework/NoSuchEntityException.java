// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   NoSuchEntityException.java

package com.sun.xml.rpc.wsdl.framework;

import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.wsdl.framework:
//            ValidationException

public class NoSuchEntityException extends ValidationException {

    public NoSuchEntityException(QName name) {
        super("entity.notFoundByQName", new Object[] {
            name.getLocalPart(), name.getNamespaceURI()
        });
    }

    public NoSuchEntityException(String id) {
        super("entity.notFoundByString", id);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.wsdl";
    }
}
