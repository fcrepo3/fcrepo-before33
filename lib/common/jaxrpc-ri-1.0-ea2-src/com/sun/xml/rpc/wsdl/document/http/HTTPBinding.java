// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   HTTPBinding.java

package com.sun.xml.rpc.wsdl.document.http;

import com.sun.xml.rpc.wsdl.framework.Entity;
import com.sun.xml.rpc.wsdl.framework.Extension;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.wsdl.document.http:
//            HTTPConstants

public class HTTPBinding extends Extension {

    private String _verb;

    public HTTPBinding() {
    }

    public QName getElementName() {
        return HTTPConstants.QNAME_BINDING;
    }

    public String getVerb() {
        return _verb;
    }

    public void setVerb(String s) {
        _verb = s;
    }

    public void validateThis() {
        if(_verb == null)
            failValidation("validation.missingRequiredAttribute", "verb");
    }
}
