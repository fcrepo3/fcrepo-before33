// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   HTTPUrlReplacement.java

package com.sun.xml.rpc.wsdl.document.http;

import com.sun.xml.rpc.wsdl.framework.Extension;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.wsdl.document.http:
//            HTTPConstants

public class HTTPUrlReplacement extends Extension {

    public HTTPUrlReplacement() {
    }

    public QName getElementName() {
        return HTTPConstants.QNAME_URL_REPLACEMENT;
    }

    public void validateThis() {
    }
}
