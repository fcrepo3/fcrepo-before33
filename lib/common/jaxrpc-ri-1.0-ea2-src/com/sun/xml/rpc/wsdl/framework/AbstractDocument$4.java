// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   AbstractDocument.java

package com.sun.xml.rpc.wsdl.framework;

import java.util.Set;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.wsdl.framework:
//            QNameAction

class AbstractDocument$4
    implements QNameAction {

    private final AbstractDocument$3 this$1; /* synthetic field */

    AbstractDocument$4(AbstractDocument$3 this$1) {
        this.this$1 = this$1;
    }

    public void perform(QName name) {
        AbstractDocument$3.access$100(this$1).add(name.getNamespaceURI());
    }
}
