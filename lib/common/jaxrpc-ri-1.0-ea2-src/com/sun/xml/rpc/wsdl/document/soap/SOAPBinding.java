// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPBinding.java

package com.sun.xml.rpc.wsdl.document.soap;

import com.sun.xml.rpc.wsdl.framework.Extension;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.wsdl.document.soap:
//            SOAPStyle, SOAPConstants

public class SOAPBinding extends Extension {

    private String _transport;
    private SOAPStyle _style;

    public SOAPBinding() {
        _style = SOAPStyle.DOCUMENT;
    }

    public QName getElementName() {
        return SOAPConstants.QNAME_BINDING;
    }

    public String getTransport() {
        return _transport;
    }

    public void setTransport(String s) {
        _transport = s;
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
