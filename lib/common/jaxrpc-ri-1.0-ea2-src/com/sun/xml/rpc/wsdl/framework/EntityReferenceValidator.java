// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   EntityReferenceValidator.java

package com.sun.xml.rpc.wsdl.framework;

import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.wsdl.framework:
//            Kind

public interface EntityReferenceValidator {

    public abstract boolean isValid(Kind kind, QName qname);
}
