// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPOperation.java

package com.sun.xml.rpc.wsdl.document.soap;

import com.sun.xml.rpc.wsdl.framework.Extension;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.wsdl.document.soap:
//            SOAPConstants, SOAPStyle

public class SOAPOperation extends Extension {

    private String _soapAction;
    private SOAPStyle _style;

    public SOAPOperation() {
    }

    public QName getElementName() {
        return SOAPConstants.QNAME_OPERATION;
    }

    public String getSOAPAction() {
        return _soapAction;
    }

    public void setSOAPAction(String s) {
        _soapAction = s;
    }

    public SOAPStyle getStyle() {
        return _style;
    }

    public void setStyle(SOAPStyle s) {
        _style = s;
    }

    public boolean isDocument() {
        return _style == SOAPStyle.DOCUMENT;
    }

    public boolean isRPC() {
        return _style == SOAPStyle.RPC;
    }

    public void validateThis() {
    }
}
