// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPAddress.java

package com.sun.xml.rpc.wsdl.document.soap;

import com.sun.xml.rpc.wsdl.framework.Entity;
import com.sun.xml.rpc.wsdl.framework.Extension;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.wsdl.document.soap:
//            SOAPConstants

public class SOAPAddress extends Extension {

    private String _location;

    public SOAPAddress() {
    }

    public QName getElementName() {
        return SOAPConstants.QNAME_ADDRESS;
    }

    public String getLocation() {
        return _location;
    }

    public void setLocation(String s) {
        _location = s;
    }

    public void validateThis() {
        if(_location == null)
            failValidation("validation.missingRequiredAttribute", "location");
    }
}
